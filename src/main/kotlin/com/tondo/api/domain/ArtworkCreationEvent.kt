package com.tondo.api.domain

import java.util.UUID

/**
 * 아트워크 생성 진행 상태 변경 시 발행되는 통합 이벤트
 */
data class ArtworkCreationEvent(
    val taskId: UUID,
    val stage: ArtworkCreationStage,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)
