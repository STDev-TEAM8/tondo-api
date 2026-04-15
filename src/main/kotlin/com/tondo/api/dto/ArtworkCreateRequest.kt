package com.tondo.api.dto

/**
 * 이미지 생성 요청 DTO
 */
data class ArtworkCreateRequest(
    val taskId: String,
    val averageHz: Double,
    val averageVolulme: Double,
    val averageTimbre: Double,
    val base64Image: String // 이 필드의 크기가 클 수 있습니다! JSON 페이로드 크기 보고, request body size limit 늘려야할 수 있습니다!
)
