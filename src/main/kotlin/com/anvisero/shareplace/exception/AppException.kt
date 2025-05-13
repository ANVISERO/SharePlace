package com.anvisero.shareplace.exception

import org.springframework.http.HttpStatus

open class AppException(
    override val message: String,
    val status: HttpStatus
) : RuntimeException(message)