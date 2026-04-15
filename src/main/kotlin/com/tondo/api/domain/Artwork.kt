package com.tondo.api.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    name = "artworks",
    indexes = [Index(columnList = "task_id", name = "idx_artwork_task_id")],
)
class Artwork (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "artwork_seq")
    @SequenceGenerator(name = "artwork_seq", sequenceName = "artwork_seq", allocationSize = 10)
    val artworkId: Long? = null,

    val taskId: UUID, // PostgreSQL Native UUID  로 JPA 가 자동으로 매핑 -> Page Utilization 효율 향상
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val imageUrl: String,
    val docentReport: String,
    val qrImageUrl: String,
)