package com.anvisero.shareplace.chat.service

import com.anvisero.shareplace.chat.model.ChatRoom
import com.anvisero.shareplace.chat.model.enum.RoomType
import com.anvisero.shareplace.chat.payload.ChatRoomResponse
import com.anvisero.shareplace.chat.payload.CreateChatRoomRequest
import com.anvisero.shareplace.chat.repository.ChatRoomRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ChatRoomService(
    private val chatRoomRepository: ChatRoomRepository
) {

    fun createChatRoom(request: CreateChatRoomRequest, currentUserId: String): ChatRoomResponse {
        val allParticipantIds: Set<String> = request.participantIds + currentUserId

        if (request.type == RoomType.PRIVATE && allParticipantIds.size != 2) {
            throw IllegalArgumentException("Private chat rooms must have exactly two participants.")
        }
        if (request.type == RoomType.GROUP && request.name.isNullOrBlank()) {
            throw IllegalArgumentException("Group chat rooms must have a name.")
        }

        // TODO: Проверить, существует ли уже приватный чат между этими двумя пользователями, чтобы избежать дубликатов

        val chatRoom = ChatRoom(
            name = request.name,
            participantIds = allParticipantIds,
            type = request.type,
            creatorId = currentUserId,
            lastActivityAt = Instant.now()
        )
        val savedRoom = chatRoomRepository.save(chatRoom)
        return savedRoom.toResponse()
    }

    fun getChatRoomById(roomId: String, currentUserId: String): ChatRoomResponse? {
        return chatRoomRepository.findById(roomId)
            .filter { it.participantIds.contains(currentUserId) }
            .map { it.toResponse() }
            .orElse(null) // Или бросить ResourceNotFoundException
    }

    fun getChatRoomsForCurrentUser(currentUserId: String, pageable: Pageable): Page<ChatRoomResponse> {
        return chatRoomRepository.findByParticipantIdsContainingOrderByLastActivityAtDesc(currentUserId, pageable)
            .map { it.toResponse() }
    }

    fun updateRoomActivity(roomId: String) {
        chatRoomRepository.findById(roomId).ifPresent { room ->
            room.lastActivityAt = Instant.now()
            chatRoomRepository.save(room)
        }
    }

    private fun ChatRoom.toResponse(): ChatRoomResponse = ChatRoomResponse(
        id = this.id!!,
        name = this.name,
        participantIds = this.participantIds,
        type = this.type,
        lastActivityAt = this.lastActivityAt,
        createdAt = this.createdAt,
        creatorId = this.creatorId
    )
}