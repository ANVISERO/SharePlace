package com.anvisero.shareplace.chat.controller

import com.anvisero.shareplace.chat.model.ChatMessage
import com.anvisero.shareplace.chat.payload.SendMessageRequest
import com.anvisero.shareplace.chat.service.ChatMessageService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class ChatMessageController(
    private val chatMessageService: ChatMessageService,
    private val simpMessagingTemplate: SimpMessagingTemplate
) {
    private val logger = LoggerFactory.getLogger(ChatMessageController::class.java)

    @MessageMapping("/chat.sendMessage/{roomId}")
    fun handleWebSocketMessage(
        @DestinationVariable roomId: String,
        @Payload request: SendMessageRequest
    ) {
        logger.info("Received WebSocket message for room $roomId: $request")
        try {
            val savedMessage = chatMessageService.saveMessage(roomId, request)
            simpMessagingTemplate.convertAndSend("/topic/room/$roomId", savedMessage)
            logger.info("Message sent to /topic/room/$roomId")
        } catch (e: Exception) {
            logger.error("Error processing message for room $roomId: ${e.message}", e)
        }
    }

    @GetMapping("/api/chat/rooms/{roomId}/messages")
    @ResponseBody
    fun getMessageHistory(
        @PathVariable roomId: String,
        @PageableDefault(size = 50, sort = ["timestamp"]) pageable: Pageable
    ): ResponseEntity<Page<ChatMessage>> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetails
        val messages = chatMessageService.getMessagesForRoom(roomId, userDetails.username, pageable)
        return ResponseEntity.ok(messages)
    }
}