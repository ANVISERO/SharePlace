package com.anvisero.shareplace.chat.model.controller

import com.anvisero.shareplace.chat.payload.ChatMessageResponse
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

@Controller // Используем @Controller, т.к. есть и @MessageMapping, и @GetMapping
// @CrossOrigin(origins = ["http://localhost:3000"]) // Для REST эндпоинта
class ChatMessageController(
    private val chatMessageService: ChatMessageService,
    private val simpMessagingTemplate: SimpMessagingTemplate // Для отправки сообщений через WebSocket
) {
    private val logger = LoggerFactory.getLogger(ChatMessageController::class.java)

    @MessageMapping("/chat.sendMessage/{roomId}")
    // @SendTo("/topic/room/{roomId}") // Можно использовать @SendTo, если не нужна доп. логика
    fun handleWebSocketMessage(
        @DestinationVariable roomId: String,
        @Payload request: SendMessageRequest // Предполагаем, что клиент шлет только контент
        // Principal principal - для получения аутентифицированного пользователя
    ) {
        logger.info("Received WebSocket message for room $roomId: $request")
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetails
        // В реальном приложении senderId и senderUsername нужно получать из Principal или SecurityContext
        // Для примера, chatMessageService.saveMessage сам получит их из getCurrentUserId/Username
        try {
            val savedMessage = chatMessageService.saveMessage(roomId, request, userDetails.username)
            // Отправляем сообщение всем подписчикам данного топика комнаты
            simpMessagingTemplate.convertAndSend("/topic/room/$roomId", savedMessage)
            logger.info("Message sent to /topic/room/$roomId")
        } catch (e: Exception) {
            logger.error("Error processing message for room $roomId: ${e.message}", e)
            // Можно отправить сообщение об ошибке обратно отправителю через /user/{userId}/queue/errors
            // simpMessagingTemplate.convertAndSendToUser(principal.name, "/queue/errors", e.message ?: "Unknown error")
        }
    }

    @GetMapping("/api/chat/rooms/{roomId}/messages")
    @ResponseBody // Убедимся, что возвращаем JSON
    fun getMessageHistory(
        @PathVariable roomId: String,
        @PageableDefault(size = 50, sort = ["timestamp"]) pageable: Pageable
    ): ResponseEntity<Page<ChatMessageResponse>> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetails
        // Предполагается, что service проверит права доступа текущего пользователя к комнате
        val messages = chatMessageService.getMessagesForRoom(roomId, userDetails.username, pageable)
        return ResponseEntity.ok(messages)
    }
}