package com.anvisero.shareplace.chat.model

import com.anvisero.shareplace.chat.model.enum.RoomType
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "chat_rooms")
data class ChatRoom(
    @Id
    val id: String? = null,
    var name: String?,
    @Indexed
    val participantIds: Set<String>,
    val type: RoomType,
    @Indexed
    var lastActivityAt: Instant? = Instant.now(),
    @CreatedDate
    var createdAt: Instant? = null,
    val creatorId: String? = null
)
