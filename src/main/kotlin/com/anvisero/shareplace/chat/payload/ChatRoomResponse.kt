package com.anvisero.shareplace.chat.payload

import com.anvisero.shareplace.chat.model.enum.RoomType
import java.time.Instant

data class ChatRoomResponse(
    val id: String,
    val name: String?,
    val participantIds: Set<String>,
    val type: RoomType,
    val lastActivityAt: Instant?,
    val createdAt: Instant?,
    val creatorId: String?
)
