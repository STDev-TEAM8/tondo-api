package com.tondo.api.infrastructure.aws.bedrock.dto

import com.tondo.api.domain.ArtworkRepresentation
import com.tondo.api.infrastructure.aws.bedrock.template.BedrockPromptTemplate

data class BedrockImageRequest(
    val prompt: String,
    val negativePrompt: String = "",
    val width: Int = 1024,
    val height: Int = 1024,
    val referenceImageBase64: String? = null
) {
    companion object {
        fun fromDomain(rep: ArtworkRepresentation): BedrockImageRequest {
            val generatedPrompt = BedrockPromptTemplate.createImageGenerationPrompt()
            // ArtworkRepresentation의 base64Image가 ByteArray이므로 AWS 스펙에 맞게 Base64 String으로 인코딩합니다.
            val encodedImage = java.util.Base64.getEncoder().encodeToString(rep.base64Image)
            
            return BedrockImageRequest(
                prompt = generatedPrompt,
                referenceImageBase64 = encodedImage,
                width = 1024,
                height = 1024
            )
        }
    }
}