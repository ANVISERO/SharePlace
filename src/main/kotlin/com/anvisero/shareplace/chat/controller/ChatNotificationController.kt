package com.anvisero.shareplace.chat.controller

import com.anvisero.shareplace.chat.payload.NotificationResponse
import com.anvisero.shareplace.chat.service.ChatNotificationService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chat/notifications")
// @CrossOrigin(origins = ["http://localhost:3000"])
class ChatNotificationController(
    private val chatNotificationService: ChatNotificationService
) {

    @GetMapping
    fun getMyNotifications(
        @PageableDefault(size = 20, sort = ["createdAt"]) pageable: Pageable
    ):
            ResponseEntity<Page<NotificationResponse>> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetails
        val notifications = chatNotificationService.getNotificationsForCurrentUser(userDetails.username, pageable)
        return ResponseEntity.ok(notifications)
    }

    @GetMapping("/unread")
    fun getMyUnreadNotifications(): ResponseEntity<List<NotificationResponse>> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetails

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
    fun markNotificationAsRead(@PathVariable notificationId: String): ResponseEntity<NotificationResponse> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetails

        val notification = chatNotificationService.markNotificationAsRead(notificationId, userDetails.username)
        return notification?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @PostMapping("/read-all")
    fun markAllNotificationsAsRead(): ResponseEntity<List<NotificationResponse>> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetails

        val notifications = chatNotificationService.markAllNotificationsAsReadForCurrentUser(userDetails.username)
        return ResponseEntity.ok(notifications)
    }
}