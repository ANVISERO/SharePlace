package com.anvisero.shareplace.chat.service

import com.anvisero.shareplace.chat.model.ChatNotification
import com.anvisero.shareplace.chat.payload.NotificationResponse
import com.anvisero.shareplace.chat.repository.ChatNotificationRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ChatNotificationService(
    private val chatNotificationRepository: ChatNotificationRepository
) {
    fun getNotificationsForCurrentUser(recipientUserId: String, pageable: Pageable): Page<NotificationResponse> {
        return chatNotificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(recipientUserId, pageable)
            .map { it.toResponse() }
    }

    fun getUnreadNotificationsForCurrentUser(recipientUserId: String): List<NotificationResponse> {
        return chatNotificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(recipientUserId)
            .map { it.toResponse() }
    }

    fun countUnreadNotificationsForCurrentUser(recipientUserId: String): Long {
        return chatNotificationRepository.countByRecipientUserId(recipientUserId)
    }

    fun markNotificationAsRead(notificationId: String, recipientUserId: String): NotificationResponse? {
        return chatNotificationRepository.findById(notificationId)
            .filter { it.recipientUserId == recipientUserId }
            .map {
                chatNotificationRepository.save(it).toResponse()
            }
            .orElse(null) // Или бросить исключение
    }

    fun markAllNotificationsAsReadForCurrentUser(recipientUserId: String): List<NotificationResponse> {
        val unreadNotifications = chatNotificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(recipientUserId)
        return chatNotificationRepository.saveAll(unreadNotifications).map { it.toResponse() }
    }

    fun createNotification(
        recipientUserId: String,
        message: String,
        roomId: String? = null,
    ): ChatNotification {
        val notification = ChatNotification(
            recipientUserId = recipientUserId,
            message = message,
            roomId = roomId,
        )
        // Здесь можно добавить логику отправки push-уведомлений или WebSocket уведомлений
        return chatNotificationRepository.save(notification)
    }


    private fun ChatNotification.toResponse(): NotificationResponse = NotificationResponse(
        id = this.id!!,
        recipientUserId = this.recipientUserId,
        message = this.message,
        roomId = this.roomId,
        createdAt = this.createdAt,
    )
}