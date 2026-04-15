package com.tondo.api.infrastructure.aws.bedrock.service

import com.tondo.api.infrastructure.aws.bedrock.dto.BedrockImageRequest
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("local")
class MockAiService : AiService {
    override fun chat(userMessage: String): String {
        // 간단한 에코 응답을 반환
        return "Echo: $userMessage"
    }

    override fun generateImage(request: BedrockImageRequest): String {
        // 간단한 이미지 URL을 반환
        return "https://example.com/generated-image.png"
    }
}