package com.tondo.api.infrastructure.qr

data class QrGenerationCommand(
    val content: String,
    val width: Int,
    val height: Int
)
