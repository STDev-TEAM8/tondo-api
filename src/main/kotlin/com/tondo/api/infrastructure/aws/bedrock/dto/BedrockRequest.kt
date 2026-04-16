package com.tondo.api.infrastructure.aws.bedrock.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class BedrockRequest(
    @JsonProperty("anthropic_version")
    val anthropicVersion: String = "bedrock-2023-05-31",
    @JsonProperty("max_tokens")
    val maxTokens: Int = 1024,
    val messages: List<BedrockMessage>
)