package com.tondo.api.controller

import com.tondo.api.dto.ArtworkCreateRequest
import com.tondo.api.dto.ArtworkCreateResponse
import com.tondo.api.dto.ArtworkResultResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@RestController
@RequestMapping("/api/v1/artworks")
class ArtworkController {

    // PoC 및 프론트엔드 개발을 위해, (taskId, ArtworkCreateRequest) 형태의 Entry 를 저장하는 In-memory 저장소
    // 이후 PostgreSQL Repository 에 접근하는 Service 로직으로 교체 예정.
    private val storage = ConcurrentHashMap<String, ArtworkCreateRequest>()

    @PostMapping
    fun createArtwork(@RequestBody request: ArtworkCreateRequest) : ResponseEntity<ArtworkCreateResponse> {
        val taskId = UUID.randomUUID().toString()
        storage[taskId] = request
        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .body(ArtworkCreateResponse(taskId))
    }

    @GetMapping("/{taskId}")
    fun getArtwork(@PathVariable taskId: String) : ResponseEntity<ArtworkResultResponse> {
        if(!storage.containsKey(taskId)) {return ResponseEntity.notFound().build()}

        return ResponseEntity.status(HttpStatus.OK)
            .body(ArtworkResultResponse(
                taskId = taskId,
                imageUrl = "https://example.com/image/stub-artwork.png", // 실제 이미지 URL로 교체 예정
                report = "가짜 도슨트 리포트입니다 워후!" // 실제 보고서 내용으로 교체 예정
            ))
    }
}