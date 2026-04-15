package com.tondo.api.domain

import java.time.LocalDateTime
import java.util.UUID

data class ArtworkRepresentation(
    val taskId: UUID = java.util.UUID.randomUUID(),

    val averageHz: Double,
    val averageVolume: Double,
    val averageTimbre: Double,
    val base64Image: ByteArray, // 현행 기획 및 설계 상 중복 제거와 같은 비교 로직이 필요하지 않음 -> hashCode(), equals() 오버라이드 불필요
    val creatdAt: LocalDateTime = LocalDateTime.now(),

    // LLM 호출 이후 채울 필드 : nullable 로 선언
    var report: String? = null,
    var finalImageUrl: String? = null,
    var qrImageUrl: String? = null
)
{
    // Domain method: report 와 finalImageUrl 이 모두 null 이 아니면 저장할 준비가 된 것으로 간주
    fun isReadyToSave(): Boolean {
        return report != null && finalImageUrl != null && qrImageUrl != null
    }
}
