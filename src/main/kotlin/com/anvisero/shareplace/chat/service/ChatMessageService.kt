package com.anvisero.shareplace.chat.service

import com.anvisero.shareplace.chat.controller.ChatMessageController
import com.anvisero.shareplace.chat.model.ChatMessage
import com.anvisero.shareplace.chat.payload.SendMessageRequest
import com.anvisero.shareplace.chat.repository.ChatMessageRepository
import com.anvisero.shareplace.chat.repository.ChatRoomRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ChatMessageService(
    private val chatMessageRepository: ChatMessageRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatRoomService: ChatRoomService,
    private val chatNotificationService: ChatNotificationService
) {
    private val logger = LoggerFactory.getLogger(ChatMessageService::class.java)

    fun saveMessage(roomId: String, request: SendMessageRequest): ChatMessage {
        val room = chatRoomRepository.findById(roomId)
            .orElseThrow { NoSuchElementException("Chat room not found with id: $roomId") }
        if (!room.participantsInfo.keys.contains(request.userId)) {
            throw IllegalAccessException("User ${request.userId} is not a participant of room $roomId")
        }

        val chatMessage = ChatMessage(
            roomId = roomId,
            senderId = request.userId,
            content = request.content
        )
        logger.info("Saving message to $roomId")
        val savedMessage = chatMessageRepository.save(chatMessage)

        chatRoomService.updateRoomActivity(roomId)
        logger.info("Update activity to $roomId")


        // TODO: Здесь можно создать и отправить уведомления другим участникам комнаты
        // notificationService.createAndSendNewMessageNotifications(savedMessage)

        try {
            chatNotificationService.createNotificationsForNewMessage(savedMessage, room)
        } catch (e: Exception) {
            // Логируем ошибку, но не прерываем основной процесс сохранения сообщения,
            // если создание уведомлений не является критичным для успеха операции.
            logger.error("Ошибка при создании уведомлений для сообщения ID ${savedMessage.id}: ", e)
        }

        return savedMessage
    }

    fun getMessagesForRoom(roomId: String, currentUserId: String, pageable: Pageable): Page<ChatMessage> {
        chatRoomRepository.findById(roomId)
            .filter { it.participantsInfo.keys.contains(currentUserId) }
            .orElseThrow { NoSuchElementException("Chat room not found or access denied for room id: $roomId") }

        return chatMessageRepository.findByRoomIdOrderByTimestampDesc(roomId, pageable)
    }
}