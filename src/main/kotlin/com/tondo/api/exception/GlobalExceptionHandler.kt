package com.tondo.api.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(TondoException::class)
    fun handleTondoException(e: TondoException): ResponseEntity<ErrorResponse> {
        log.error("TondoException: code={}, message={}", e.errorCode, e.message)
        val response = ErrorResponse(
            errorCode = e.errorCode,
            message = e.message
        )
        return ResponseEntity.status(e.status).body(response)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message = e.bindingResult.fieldErrors.joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        log.error("Validation error: {}", message)
        val response = ErrorResponse(
            errorCode = "INVALID_INPUT",
            message = message
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(e: Exception): ResponseEntity<ErrorResponse> {
        log.error("Unhandled Exception: ", e)
        val response = ErrorResponse(
            errorCode = "INTERNAL_SERVER_ERROR",
            message = e.message ?: "An unexpected error occurred"
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }
}
