package com.tondo.api.controller

import com.tondo.api.dto.ArtworkCreateRequest
import com.tondo.api.dto.ArtworkCreateResponse
import com.tondo.api.dto.ArtworkResultResponse
import com.tondo.api.infrastructure.sse.SseEmitterManager
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/api/v1/artworks")
class ArtworkController(
    private val artworkOrchestrator: com.tondo.api.application.ArtworkOrchestrator,
    private val sseEmitterManager: SseEmitterManager
) {

    @PostMapping
    fun createArtwork(@RequestBody request: ArtworkCreateRequest) : ResponseEntity<ArtworkCreateResponse> {
        // 비동기 파이프라인 시작 후 곧바로 응답
        val response = artworkOrchestrator.startArtworkCreation(request)
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response)
    }

    @GetMapping("/{taskId}")
    fun getArtwork(@PathVariable taskId: String) : ResponseEntity<ArtworkResultResponse> {
        // TODO: 나중에 실제 DB(ArtworkRepository)에서 taskId로 조회하도록 구현
        // if(!storage.containsKey(taskId)) {return ResponseEntity.notFound().build()}

        return ResponseEntity.status(HttpStatus.OK)
            .body(ArtworkResultResponse(
                taskId = taskId,
                imageUrl = "https://example.com/image/stub-artwork.png", // 실제 이미지 URL로 교체 예정
                report = "가짜 도슨트 리포트입니다 워후!" // 실제 보고서 내용으로 교체 예정
            ))
    }

    @GetMapping("/{taskId}/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamProgress(@PathVariable taskId: String) : SseEmitter {
        return sseEmitterManager.connect(taskId)
    }
}