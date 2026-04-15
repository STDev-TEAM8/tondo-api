package com.tondo.api.infrastructure.bedrock.dto

data class BedrockRequest(
    val anthropicVersion: String = "bedrock-2023-05-31",
    val maxTokens: Int = 1024,
    val messages: List<BedrockMessage>
)