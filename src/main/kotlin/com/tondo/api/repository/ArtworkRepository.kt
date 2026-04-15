package com.tondo.api.repository

import com.tondo.api.domain.Artwork
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ArtworkRepository : JpaRepository<Artwork, Long> {

    fun findByUserId(userId: Long, pageable: Pageable): Page<Artwork> // Abusive 한 상황을 고려해 방어 차원에서 Pageable 적용 / 나중에 수정하면 수정 범위가 큼.
    fun findByTaskId(taskId: Long): Artwork? // taskId 로 유일하게 Artwork 식별가능하므로 Nullable 한 Single Object 로 리턴 타입 정의


}