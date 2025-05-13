package com.anvisero.shareplace.chat.payload

import java.time.Instant

data class ChatMessageResponse(
    val id: String,
    val roomId: String,
    val senderId: String,
    val senderUsername: String,
    val content: String,
    val timestamp: Instant?
)
