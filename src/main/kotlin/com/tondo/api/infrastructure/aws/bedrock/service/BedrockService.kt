package com.tondo.api.infrastructure.aws.bedrock.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.tondo.api.domain.ArtworkRepresentation
import com.tondo.api.infrastructure.aws.AwsProperties
import com.tondo.api.infrastructure.aws.bedrock.dto.BedrockImageRequest
import com.tondo.api.infrastructure.aws.bedrock.dto.BedrockMessage
import com.tondo.api.infrastructure.aws.bedrock.dto.BedrockRequest
import com.tondo.api.infrastructure.aws.bedrock.template.BedrockPromptTemplate
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest
import software.amazon.awssdk.services.bedrockruntime.model.ModelErrorException

@Service
@Profile("prod")
class BedrockService(
    @Qualifier("bedrockRuntimeClient")
    private val bedrockClient: BedrockRuntimeClient,
    @Qualifier("imageBedrockClient")
    private val imageBedrockClient: BedrockRuntimeClient,
    private val props: AwsProperties,
    private val objectMapper: ObjectMapper
) : AiService {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val MAX_RETRY = 3
        private const val RETRY_DELAY_MS = 1000L
    }

    override fun generateDocentReport(artworkRepresentation: ArtworkRepresentation): String {
        val prompt = BedrockPromptTemplate.createDocentPrompt(
            averageHz = artworkRepresentation.averageHz,
            averageTimbre = artworkRepresentation.averageTimbre,
            averageVolume = artworkRepresentation.averageVolume,
            voiceColor = artworkRepresentation.voiceColor
        )

        val requestBody = objectMapper.writeValueAsString(
            BedrockRequest(
                messages = listOf(BedrockMessage(role = "user", content = prompt))
            )
        )

        val request = InvokeModelRequest.builder()
            .modelId(props.bedrock.modelId)
            .body(SdkBytes.fromUtf8String(requestBody))
            .contentType("application/json")
            .build()

        val response = bedrockClient.invokeModel(request)
        val result = objectMapper.readTree(response.body().asUtf8String())
        return result["content"][0]["text"].asText()
    }

    override fun generateImage(request: BedrockImageRequest): String {
        val modelId = props.bedrock.imageModelId
        val isStability = modelId.contains("stability")

        val requestBody = if (isStability) {
            buildStabilityRequest(request)
        } else {
            buildNovaRequest(request)
        }

        val bodyJson = objectMapper.writeValueAsString(requestBody)

        log.info(
            "Bedrock image request: modelId={}, hasRef={}, promptLength={}",
            modelId, request.referenceImageBase64 != null, request.prompt.length
        )

        val invokeRequest = InvokeModelRequest.builder()
            .modelId(modelId)
            .body(SdkBytes.fromUtf8String(bodyJson))
            .contentType("application/json")
            .build()

        var lastException: Exception? = null
        for (attempt in 1..MAX_RETRY) {
            try {
                val response = imageBedrockClient.invokeModel(invokeRequest)
                val responseBody = response.body().asUtf8String()
                val result = objectMapper.readTree(responseBody)

                if (isStability) {
                    // 1. Safety Filter 체크
                    val finishReason = result["finish_reasons"]?.get(0)?.asText()
                    if (finishReason == "Filter reason: prompt") {
                        log.error("Safety Filter Triggered! Prompt: ${request.prompt}")
                        throw IllegalArgumentException("Stability AI가 프롬프트를 거절했습니다. (Content Filtered)")
                    }

                    // 2. 이미지 데이터 추출 (Core v1은 'images' 배열 혹은 'image' 필드로 응답함)
                    val base64Image = when {
                        result.has("images") && result["images"].isArray -> result["images"][0].asText()
                        result.has("image") -> result["image"].asText()
                        else -> null
                    }

                    return base64Image ?: throw IllegalStateException("Stability response missing image data: $responseBody")
                } else {
                    // Nova / Titan 응답 처리
                    return result["images"][0].asText()
                }
            } catch (e: ModelErrorException) {
                lastException = e
                log.warn("Bedrock image generation failed (attempt {}/{}): {}", attempt, MAX_RETRY, e.message)
                if (attempt < MAX_RETRY) {
                    Thread.sleep(RETRY_DELAY_MS * attempt)
                }
            } catch (e: IllegalArgumentException) {
                throw e // 필터링 에러는 즉시 중단
            }
        }
        throw lastException ?: IllegalStateException("Unknown error during image generation")
    }

    private fun buildNovaRequest(request: BedrockImageRequest): Map<String, Any> {
        return if (request.referenceImageBase64 != null) {
            mapOf(
                "taskType" to "IMAGE_VARIATION",
                "imageVariationParams" to mapOf(
                    "text" to request.prompt,
                    "images" to listOf(request.referenceImageBase64),
                    "similarityStrength" to 0.7
                ),
                "imageGenerationConfig" to mapOf(
                    "width" to request.width,
                    "height" to request.height,
                    "numberOfImages" to 1,
                    "quality" to "standard"
                )
            )
        } else {
            mapOf(
                "taskType" to "TEXT_IMAGE",
                "textToImageParams" to mapOf(
                    "text" to request.prompt
                ),
                "imageGenerationConfig" to mapOf(
                    "width" to request.width,
                    "height" to request.height,
                    "numberOfImages" to 1,
                    "quality" to "standard"
                )
            )
        }
    }

    private fun buildStabilityRequest(request: BedrockImageRequest): Map<String, Any> {
        val body = mutableMapOf<String, Any>(
            "prompt" to request.prompt,
            "output_format" to "png"
        )

        if (request.referenceImageBase64 != null) {
            body["mode"] = "image-to-image"
            body["image"] = request.referenceImageBase64
            body["strength"] = 0.50 // Fine-tuning 을 위해 조정해야하는 파라미터. 0으로 가까울수록 Skeletal 이미지에 가깝게, 멀 수록 artistic.
        }

        if (request.negativePrompt.isNotBlank()) {
            body["negative_prompt"] = request.negativePrompt
        }

        return body
    }
}