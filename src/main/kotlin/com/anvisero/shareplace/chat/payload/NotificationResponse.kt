package com.anvisero.shareplace.chat.payload

import java.time.Instant

data class NotificationResponse(
    val id: String,
    val recipientUserId: String,
    val message: String,
    val roomId: String?,
    val createdAt: Instant?,
)
