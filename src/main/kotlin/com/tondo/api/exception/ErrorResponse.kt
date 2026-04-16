package com.tondo.api.exception

import java.time.LocalDateTime

data class ErrorResponse(
    val errorCode: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
