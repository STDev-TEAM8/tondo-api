package com.tondo.api.application

import com.tondo.api.domain.Artwork
import com.tondo.api.domain.ArtworkCreationEvent
import com.tondo.api.domain.ArtworkCreationStage.*
import com.tondo.api.domain.ArtworkRepresentation
import com.tondo.api.dto.ArtworkCreateRequest
import com.tondo.api.dto.ArtworkCreateResponse
import com.tondo.api.dto.ArtworkResultResponse
import com.tondo.api.infrastructure.aws.bedrock.dto.BedrockImageRequest
import com.tondo.api.infrastructure.aws.bedrock.service.AiService
import com.tondo.api.infrastructure.aws.s3.ExternalStorageService
import com.tondo.api.infrastructure.qr.QrGenerationCommand
import com.tondo.api.infrastructure.qr.QrGenerator
import com.tondo.api.service.ArtworkService
import com.tondo.api.exception.TondoException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.Base64
import java.util.UUID

@Service
class ArtworkOrchestrator(
    private val artworkService: ArtworkService,
    private val aiService: AiService,
    private val storageService: ExternalStorageService,
    private val qrGenerator: QrGenerator,
    private val eventPublisher: ApplicationEventPublisher,
    @org.springframework.beans.factory.annotation.Value("\${app.frontend.base-url}")
    private val frontendBaseUrl: String
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 스테이지 실행을 감싸는 래퍼
     * try-catch 스파게티 없이 파이프라인의 핵심 비즈니스 로직과 이벤트 발행을 분리합니다.
     */
    private suspend fun <T> executeStage(
        taskId: UUID,
        stage: com.tondo.api.domain.ArtworkCreationStage,
        action: suspend () -> T
    ): T {
        log.info("[Task: {}] Starting stage: {}", taskId, stage)
        return runCatching { action() }
            .onSuccess { 
                log.info("[Task: {}] Successfully completed stage: {}", taskId, stage)
                eventPublisher.publishEvent(ArtworkCreationEvent(taskId, stage, isSuccess = true))
            }
            .onFailure { error -> 
                log.error("[Task: {}] Failed at stage: {}. Error: {}", taskId, stage, error.message, error)
                eventPublisher.publishEvent(ArtworkCreationEvent(taskId, stage, isSuccess = false, errorMessage = error.message))
            }
            .getOrThrow()
    }

    /**
     * 클라이언트 요청 시 즉시 태스크 ID를 반환하고, 실제 처리는 코루틴 백그라운드 워커에서 진행합니다.
     */
    fun startArtworkCreation(request: ArtworkCreateRequest): ArtworkCreateResponse {
        
        // 1. 도메인이 스스로 식별자를 발급
        val artworkRepresentation = try {
            ArtworkRepresentation(
                averageHz = request.averageHz,
                averageVolume = request.averageVolulme,
                averageTimbre = request.averageTimbre,
                base64Image = Base64.getDecoder().decode(request.base64Image)
            )
        } catch (e: IllegalArgumentException) {
            log.error("Invalid Base64 image data provided for request with uuid: {}", request.uuid)
            throw TondoException(
                errorCode = "INVALID_IMAGE_FORMAT",
                message = "Invalid Base64 image data.",
                status = HttpStatus.BAD_REQUEST,
                cause = e
            )
        }

        val taskId = artworkRepresentation.taskId
        val taskIdString = taskId.toString()

        // 2. 비동기 파이프라인 트리거 (Fire-And-Forget)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 스텝 1. 도슨트 생성
                executeStage(taskId, DOCENT_GENERATED) {
                    val report = aiService.generateDocentReport(artworkRepresentation)
                    artworkRepresentation.report = report
                }

                // 스텝 2. 이미지 생성
                val base64Image = executeStage(taskId, IMAGE_GENERATED) {
                    val imageRequest = BedrockImageRequest.fromDomain(artworkRepresentation)
                    aiService.generateImage(imageRequest)
                }

                // 스텝 3. 리소스 업로드 (이미지 & QR)
                executeStage(taskId, STORAGE_UPLOADED) {
                    // 이미지 S3 업로드
                    val imageBytes = Base64.getDecoder().decode(base64Image)
                    artworkRepresentation.finalImageUrl = storageService.upload(imageBytes, "artworks/artwork_${taskIdString}.png")

                    // QR 코드 URL 구성 (프론트엔드 라우팅 스펙: /result/{taskId}?uuid={sessionUuid})
                    val resultUrl = "$frontendBaseUrl/result/${taskIdString}?uuid=${request.uuid}"
                    val qrCommand = QrGenerationCommand(content = resultUrl, width = 256, height = 256)
                    
                    // QR 이미지 생성 및 S3 업로드
                    val qrBytes = qrGenerator.generate(qrCommand)
                    artworkRepresentation.qrImageUrl = storageService.upload(qrBytes, "qrcodes/qr_${taskIdString}.png")
                }

                // 스텝 4. 검증 및 DB 저장
                executeStage(taskId, COMPLETED) {
                    if (artworkRepresentation.isReadyToSave()) {
                        val artwork = Artwork(
                            taskId = artworkRepresentation.taskId,
                            createdAt = artworkRepresentation.creatdAt,
                            imageUrl = artworkRepresentation.finalImageUrl!!,
                            docentReport = artworkRepresentation.report!!,
                            qrImageUrl = artworkRepresentation.qrImageUrl!!
                        )
                        artworkService.saveArtwork(artwork)
                    } else {
                        throw IllegalStateException("Artwork 생성이 정상적으로 완료되지 않았습니다.")
                    }
                }
            } catch (e: Exception) {
                // 실패 시 이벤트는 executeStage() 내부의 onFailure에서 이미 발송되었으며, SSE 커넥션도 종료됨.
                // 여기서는 파이프라인 중단만 처리
                log.error("[Task: {}] Artwork creation pipeline aborted", taskIdString, e)
            }
        }

        // 3. 프론트엔드에는 대기 없이 즉시 생성된 번호를 응답
        return ArtworkCreateResponse(taskIdString)
    }

    fun getArtworkResult(taskId: String): ArtworkResultResponse? {
        val artwork = artworkService.getArtworkByTaskId(UUID.fromString(taskId))
        return ArtworkResultResponse(
            taskId = artwork.taskId.toString(),
            imageUrl = artwork.imageUrl,
            report = artwork.docentReport,
            qrImageUrl = artwork.qrImageUrl
        )
    }
}
