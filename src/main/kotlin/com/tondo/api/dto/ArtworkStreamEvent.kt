package com.tondo.api.dto

data class ArtworkStreamEvent(
    val progress: Int, // 0부터 100까지의 진행률
    val status: String,
    val message: String
)
