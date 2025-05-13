package com.anvisero.shareplace.chat.service

import com.anvisero.shareplace.chat.model.ChatMessage
import com.anvisero.shareplace.chat.payload.ChatMessageResponse
import com.anvisero.shareplace.chat.payload.SendMessageRequest
import com.anvisero.shareplace.chat.repository.ChatMessageRepository
import com.anvisero.shareplace.chat.repository.ChatRoomRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ChatMessageService(
    private val chatMessageRepository: ChatMessageRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomService: ChatRoomService
) {
    fun saveMessage(roomId: String, request: SendMessageRequest, senderId: String): ChatMessageResponse {

        // Проверка, что пользователь является участником комнаты
        val room = chatRoomRepository.findById(roomId)
            .orElseThrow { NoSuchElementException("Chat room not found with id: $roomId") }
        if (!room.participantIds.contains(senderId)) {
            throw IllegalAccessException("User $senderId is not a participant of room $roomId")
        }

        val chatMessage = ChatMessage(
            roomId = roomId,
            senderId = senderId,
            senderUsername = request.senderUsername,
            content = request.content
        )
        val savedMessage = chatMessageRepository.save(chatMessage)

        chatRoomService.updateRoomActivity(roomId)

        // TODO: Здесь можно создать и отправить уведомления другим участникам комнаты
        // notificationService.createAndSendNewMessageNotifications(savedMessage)

        return savedMessage.toResponse()
    }

    fun getMessagesForRoom(roomId: String, currentUserId: String, pageable: Pageable): Page<ChatMessageResponse> {
        chatRoomRepository.findById(roomId)
            .filter { it.participantIds.contains(currentUserId) }
            .orElseThrow { NoSuchElementException("Chat room not found or access denied for room id: $roomId") }

        return chatMessageRepository.findByRoomIdOrderByTimestampDesc(roomId, pageable)
            .map { it.toResponse() }
    }

    private fun ChatMessage.toResponse(): ChatMessageResponse = ChatMessageResponse(
        id = this.id!!,
        roomId = this.roomId,
        senderId = this.senderId,
        senderUsername = this.senderUsername,
        content = this.content,
        timestamp = this.timestamp
    )
}