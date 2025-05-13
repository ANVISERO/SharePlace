package com.anvisero.shareplace.chat.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "chat_messages")
data class ChatMessage(
    @Id
    val id: String? = null,
    @Indexed
    val roomId: String,
    @Indexed
    val senderId: String,
    val senderUsername: String,
    val content: String,
    @CreatedDate
    @Indexed
    var timestamp: Instant? = null
)
