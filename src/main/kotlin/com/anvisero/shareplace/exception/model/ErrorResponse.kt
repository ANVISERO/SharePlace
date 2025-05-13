package com.anvisero.shareplace.exception.model

import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime,
    val error: String,
    val message: String,
    val path: String
)
