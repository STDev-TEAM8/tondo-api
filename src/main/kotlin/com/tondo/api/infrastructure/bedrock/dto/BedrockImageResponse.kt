package com.tondo.api.infrastructure.bedrock.dto

data class BedrockImageResponse(
    val base64Image: String  // 프론트에서 "data:image/png;base64,{base64Image}" 로 사용
)