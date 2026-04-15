package com.tondo.api.infrastructure.aws.bedrock.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.tondo.api.infrastructure.aws.AwsProperties
import com.tondo.api.infrastructure.aws.bedrock.dto.BedrockImageRequest
import com.tondo.api.infrastructure.aws.bedrock.dto.BedrockMessage
import com.tondo.api.infrastructure.aws.bedrock.dto.BedrockRequest
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

    override fun chat(userMessage: String): String {
        val requestBody = objectMapper.writeValueAsString(
            BedrockRequest(
                messages = listOf(BedrockMessage(role = "user", content = userMessage))
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
        val requestBody = mapOf(
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