package com.anvisero.shareplace.chat.repository

import com.anvisero.shareplace.chat.model.ChatNotification
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatNotificationRepository : MongoRepository<ChatNotification, String> {

    fun findByRecipientUserIdOrderByCreatedAtDesc(recipientUserId: String, pageable: Pageable): Page<ChatNotification>

    fun findByRecipientUserIdOrderByCreatedAtDesc(recipientUserId: String): List<ChatNotification>

    fun countByRecipientUserId(recipientUserId: String): Long
}