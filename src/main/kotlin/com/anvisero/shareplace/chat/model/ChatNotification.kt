package com.anvisero.shareplace.chat.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "chat_notifications")
data class ChatNotification(
    @Id
    val id: String? = null,
    @Indexed
    val recipientUserId: String,
    val message: String,
    val roomId: String? = null,
    var isRead: Boolean = false,
    @CreatedDate
    @Indexed
    var createdAt: Instant? = null
)
