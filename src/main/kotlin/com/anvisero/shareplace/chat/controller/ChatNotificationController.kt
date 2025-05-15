package com.anvisero.shareplace.chat.controller

import com.anvisero.shareplace.chat.model.ChatNotification
import com.anvisero.shareplace.chat.service.ChatNotificationService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chat/notifications")
class ChatNotificationController(
    private val chatNotificationService: ChatNotificationService
) {

    private val logger = LoggerFactory.getLogger(ChatNotificationController::class.java)

    @GetMapping
    fun getMyNotifications(
        @PageableDefault(size = 20, sort = ["createdAt"]) pageable: Pageable
    ):
            ResponseEntity<Page<ChatNotification>> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetails
        val notifications = chatNotificationService.getNotificationsForCurrentUser(userDetails.username, pageable)
        return ResponseEntity.ok(notifications)
    }

    @GetMapping("/unread")
    fun getMyUnreadNotifications(): ResponseEntity<List<ChatNotification>> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetails
        logger.info("непрочитанные сообщения пользователя ${userDetails.username}")
        val notifications = chatNotificationService.getUnreadNotificationsForCurrentUser(userDetails.username)
        return ResponseEntity.ok(notifications)
    }

    @GetMapping("/unread/count")
    fun getMyUnreadNotificationCount(): ResponseEntity<Long> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetails

        val count = chatNotificationService.countUnreadNotificationsForCurrentUser(userDetails.username)
        return ResponseEntity.ok(count)
    }

    @PostMapping("/{notificationId}/read")
    fun markNotificationAsRead(@PathVariable notificationId: String): ResponseEntity<ChatNotification> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetails

        val notification = chatNotificationService.markNotificationAsRead(notificationId, userDetails.username)
        return notification?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @PostMapping("/{chatId}/read-all")
    fun markAllNotificationsForChatAsRead(
        @PathVariable chatId: String,
        @PageableDefault(size = 20, sort = ["createdAt"]) pageable: Pageable
    ): ResponseEntity<List<ChatNotification>> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetails

        val notifications = chatNotificationService.markAllNotificationsForChatAsRead(chatId, userDetails.username, pageable)
        return ResponseEntity.ok(notifications)
    }

    @PostMapping("/read-all")
    fun markAllNotificationsAsRead( @PageableDefault(size = 20, sort = ["createdAt"]) pageable: Pageable): ResponseEntity<List<ChatNotification>> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetails

        val notifications = chatNotificationService.markAllNotificationsAsReadForCurrentUser(userDetails.username, pageable)
        return ResponseEntity.ok(notifications)
    }
}