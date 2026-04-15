package com.tondo.api.infrastructure.aws.bedrock.dto

data class BedrockImageRequest(
    val prompt: String,
    val negativePrompt: String = "",
    val width: Int = 1024,
    val height: Int = 1024
)