package com.tondo.api.infrastructure.aws.bedrock.dto

data class BedrockMessage(
    val role: String,
    val content: String
)