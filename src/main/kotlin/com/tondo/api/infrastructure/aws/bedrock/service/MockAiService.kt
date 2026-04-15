package com.tondo.api.infrastructure.aws.bedrock.service

import com.tondo.api.domain.ArtworkRepresentation
import com.tondo.api.infrastructure.aws.bedrock.dto.BedrockImageRequest
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("local")
class MockAiService : AiService {
    override fun generateDocentReport(artworkRepresentation: ArtworkRepresentation): String {
        // 간단한 에코 응답을 반환
        return "Echo: Docent Report for hz=${artworkRepresentation.averageHz}"
    }

    override fun generateImage(request: BedrockImageRequest): String {
        // 간단한 더미 Base64 이미지를 반환
        return "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg=="
    }
}