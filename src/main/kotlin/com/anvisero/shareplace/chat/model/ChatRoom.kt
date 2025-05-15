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
    val participantsInfo: Map<String, ParticipantInfo> = emptyMap(),
    val type: RoomType,
    @Indexed // Индекс для сортировки/фильтрации по времени последней активности
    var lastActivityAt: Instant? = Instant.now(),
    @CreatedDate
    var createdAt: Instant? = null, // Будет установлено автоматически Spring Data, если null
    val creatorId: String? = null
)

data class ParticipantInfo(
    val userId: String,
    val firstName: String,
    val surname: String,
    val profilePictureUrl: String?
)

