package com.tondo.api.infrastructure.aws.bedrock.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.tondo.api.domain.ArtworkRepresentation
import com.tondo.api.infrastructure.aws.AwsProperties
import com.tondo.api.infrastructure.aws.bedrock.dto.BedrockImageRequest
import com.tondo.api.infrastructure.aws.bedrock.dto.BedrockMessage
import com.tondo.api.infrastructure.aws.bedrock.dto.BedrockRequest
import com.tondo.api.infrastructure.aws.bedrock.template.BedrockPromptTemplate
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest

@Service
@Profile("prod")
class BedrockService(
    private val bedrockClient: BedrockRuntimeClient,
    private val props: AwsProperties,
    private val objectMapper: ObjectMapper
) : AiService {

    override fun generateDocentReport(artworkRepresentation: ArtworkRepresentation): String {
        val prompt = BedrockPromptTemplate.createDocentPrompt(
            averageHz = artworkRepresentation.averageHz,
            averageTimbre = artworkRepresentation.averageTimbre,
            averageVolume = artworkRepresentation.averageVolume
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
        val requestBody = if (request.referenceImageBase64 != null) {
            mapOf(
                "taskType" to "IMAGE_VARIATION",
                "imageVariationParams" to mapOf(
                    "text" to request.prompt,
                    "negativeText" to request.negativePrompt,
                    "images" to listOf(request.referenceImageBase64),
                    "similarityStrength" to 0.7 // 원본 형태 보존을 위한 적절한 값
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
                    "text" to request.prompt,
                    "negativeText" to request.negativePrompt
                ),
                "imageGenerationConfig" to mapOf(
                    "width" to request.width,
                    "height" to request.height,
                    "numberOfImages" to 1,
                    "quality" to "standard"
                )
            )
        }

        val response = bedrockClient.invokeModel(
            InvokeModelRequest.builder()
                .modelId(props.bedrock.modelId)
                .body(SdkBytes.fromUtf8String(objectMapper.writeValueAsString(requestBody)))
                .contentType("application/json")
                .build()
        )

        val result = objectMapper.readTree(response.body().asUtf8String())
        return result["images"][0].asText() // base64 문자열 반환
    }
}