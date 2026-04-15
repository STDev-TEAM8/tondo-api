package com.tondo.api.repository

import com.tondo.api.domain.Artwork
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ArtworkRepository : JpaRepository<Artwork, Long> {
    fun findByTaskId(taskId: UUID?): Artwork? // taskId 로 유일하게 Artwork 식별가능하므로 Nullable 한 Single Object 로 리턴 타입 정의
}