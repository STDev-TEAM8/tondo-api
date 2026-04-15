package com.tondo.api.exception

import org.springframework.http.HttpStatus

open class TondoException(
    val errorCode: String,
    override val message: String,
    val status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    cause: Throwable? = null
) : RuntimeException(message, cause)
