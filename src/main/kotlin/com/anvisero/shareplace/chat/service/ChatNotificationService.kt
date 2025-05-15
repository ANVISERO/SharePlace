package com.anvisero.shareplace.chat.service

import com.anvisero.shareplace.chat.model.ChatMessage
import com.anvisero.shareplace.chat.model.ChatNotification
import com.anvisero.shareplace.chat.model.ChatRoom
import com.anvisero.shareplace.chat.repository.ChatNotificationRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ChatNotificationService(
    private val chatNotificationRepository: ChatNotificationRepository
) {

    private val logger = LoggerFactory.getLogger(ChatNotificationService::class.java)

    fun getNotificationsForCurrentUser(recipientUserId: String, pageable: Pageable): Page<ChatNotification> {
        return chatNotificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(recipientUserId, pageable)
    }

    fun createNotificationsForNewMessage(savedMessage: ChatMessage, room: ChatRoom) {
        val senderId = savedMessage.senderId
        val senderInfo = room.participantsInfo[senderId]
        val senderDisplayName = senderInfo?.let { "${it.firstName} ${it.surname}" } ?: "User $senderId"

        val notificationMessageText = "Новое сообщение от $senderDisplayName: " +
                if (savedMessage.content.length > 50) "${savedMessage.content.take(50)}..." else savedMessage.content

        val notificationsToCreate = mutableListOf<ChatNotification>()

        room.participantsInfo.keys.forEach { participantId ->
            // Не отправляем уведомление самому себе
            if (participantId != senderId) {
                val notification = ChatNotification(
                    recipientUserId = participantId,
                    message = notificationMessageText,
                    roomId = savedMessage.roomId,
                    isRead = false
                )
                notificationsToCreate.add(notification)
            }
        }

        if (notificationsToCreate.isNotEmpty()) {
            try {
                val savedNotifications = chatNotificationRepository.saveAll(notificationsToCreate)
                logger.info("Создано ${savedNotifications.size} уведомлений для сообщения ID ${savedMessage.id} в комнате ${savedMessage.roomId}")

            } catch (e: Exception) {
                logger.error("Ошибка при сохранении уведомлений для сообщения ID ${savedMessage.id}: ", e)
            }
        }
    }

    fun getUnreadNotificationsForCurrentUser(recipientUserId: String): List<ChatNotification> {
        logger.info("findByRecipientUserIdAndIsReadOrderByCreatedAtDesc")
        return chatNotificationRepository.findByRecipientUserIdAndIsReadOrderByCreatedAtDesc(recipientUserId, false)
    }

    fun countUnreadNotificationsForCurrentUser(recipientUserId: String): Long {
        return chatNotificationRepository.countByRecipientUserId(recipientUserId)
    }

    fun markNotificationAsRead(notificationId: String, recipientUserId: String): ChatNotification? {
        return chatNotificationRepository.findById(notificationId)
            .filter { it.recipientUserId == recipientUserId }
            .map {
                chatNotificationRepository.save(it)
            }
            .orElse(null)
    }

    fun markAllNotificationsAsReadForCurrentUser(
        recipientUserId: String,
        pageable: Pageable
    ): List<ChatNotification> {
        val unreadNotifications = chatNotificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(
            recipientUserId,
            pageable = pageable
        )
        return chatNotificationRepository.saveAll(unreadNotifications)
    }

    fun markAllNotificationsForChatAsRead(
        chatId: String,
        recipientUserId: String,
        pageable: Pageable
    ): List<ChatNotification> {
        val pageOfUnreadNotifications: Page<ChatNotification> = chatNotificationRepository.findByRecipientUserIdAndRoomIdAndIsReadOrderByCreatedAtDesc(
            recipientUserId,
            roomId = chatId,
            isRead = false,
            pageable = pageable
        )
        val notificationsToProcess: List<ChatNotification> = pageOfUnreadNotifications.content

        notificationsToProcess.forEach { notification ->
            notification.isRead = true
        }

        return chatNotificationRepository.saveAll(notificationsToProcess)
    }
}