package com.tondo.api.service

import com.tondo.api.domain.Artwork
import com.tondo.api.repository.ArtworkRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ArtworkService (
    private val artworkRepository: ArtworkRepository
){
    fun saveArtwork(artwork: Artwork): Artwork {
        return artworkRepository.save(artwork)
    }

    fun getArtworkByTaskId(taskId: UUID?): Artwork {
        return artworkRepository.findByTaskId(taskId)
            ?: throw NoSuchElementException("No artwork found for taskId: $taskId")
    }

    fun getArtworksByUserId(userId: Long, page: Int, size: Int): Page<Artwork> {
        val pageable = PageRequest.of(page, size)
        return artworkRepository.findByUserId(userId, pageable)
    }
}