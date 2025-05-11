package com.anvisero.shareplace.security.model

import java.time.Instant
import java.util.UUID

data class Token(
    val id: UUID,
    val subject: String,
    val authorities: List<String>,
    val createdAt: Instant,
    val expiresAt: Instant
)
