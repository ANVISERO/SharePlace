package com.anvisero.shareplace.chat.payload

import com.anvisero.shareplace.chat.model.enum.RoomType

data class CreateChatRoomRequest(
    val name: String?,
    val participantIds: Set<String>,
    val type: RoomType
)
