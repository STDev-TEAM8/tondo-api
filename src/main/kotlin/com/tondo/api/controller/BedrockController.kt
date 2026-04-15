package com.tondo.api.controller


import com.tondo.api.infrastructure.bedrock.dto.BedrockChatRequest
import com.tondo.api.infrastructure.bedrock.dto.BedrockChatResponse
import com.tondo.api.infrastructure.bedrock.dto.BedrockImageRequest
import com.tondo.api.infrastructure.bedrock.dto.BedrockImageResponse
import com.tondo.api.infrastructure.bedrock.service.BedrockService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/bedrock")
class BedrockController(
    private val bedrockService: BedrockService
) {
    @PostMapping("/chat")
    fun chat(@RequestBody request: BedrockChatRequest): ResponseEntity<BedrockChatResponse> {
        val response = bedrockService.chat(request.message)
        return ResponseEntity.ok(BedrockChatResponse(response))
    }

    @PostMapping("/image")
    fun generateImage(@RequestBody request: BedrockImageRequest): ResponseEntity<BedrockImageResponse> {
        val base64 = bedrockService.generateImage(request)
        return ResponseEntity.ok(BedrockImageResponse(base64))
    }
}