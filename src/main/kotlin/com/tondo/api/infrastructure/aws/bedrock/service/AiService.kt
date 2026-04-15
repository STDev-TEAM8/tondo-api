package com.tondo.api.infrastructure.aws.bedrock.service

import com.tondo.api.domain.ArtworkRepresentation
import com.tondo.api.infrastructure.aws.bedrock.dto.BedrockImageRequest

// Local 환경 에서 AI 서비스와의 통신을 추상화하기 위한 인터페이스
interface AiService {
    fun generateDocentReport(artworkRepresentation: ArtworkRepresentation): String
    fun generateImage(request: BedrockImageRequest): String
}