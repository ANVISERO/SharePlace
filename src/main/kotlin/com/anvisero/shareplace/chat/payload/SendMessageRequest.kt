package com.anvisero.shareplace.chat.payload

data class SendMessageRequest(
    val content: String,
    val senderUsername: String
)
