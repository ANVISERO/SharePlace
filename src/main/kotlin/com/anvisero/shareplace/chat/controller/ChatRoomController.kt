package com.anvisero.shareplace.chat.controller

import com.anvisero.shareplace.chat.model.ChatRoom
import com.anvisero.shareplace.chat.payload.CreateChatRoomRequest
import com.anvisero.shareplace.chat.service.ChatRoomService
import com.anvisero.shareplace.exception.NotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chat/rooms")
class ChatRoomController(
    private val chatRoomService: ChatRoomService
) {

    @PostMapping
    fun createChatRoom(@RequestBody request: CreateChatRoomRequest): ResponseEntity<ChatRoom> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetails
        val chatRoom = chatRoomService.createChatRoom(request, userDetails.username)
        return ResponseEntity.status(HttpStatus.CREATED).body(chatRoom)
    }

    @GetMapping("/{roomId}")
    fun getChatRoom(@PathVariable roomId: String): ResponseEntity<ChatRoom> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetails

        val chatRoom = chatRoomService.getChatRoomById(roomId, userDetails.username)
        return chatRoom?.let { ResponseEntity.ok(it) }
            ?: throw NotFoundException("Chat room", roomId)
    }

    @GetMapping
    fun getCurrentUserChatRooms(
        @PageableDefault(size = 20, sort = ["lastActivityAt"]) pageable: Pageable
    ): ResponseEntity<Page<ChatRoom>> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetails

        val chatRooms = chatRoomService.getChatRoomsForCurrentUser(userDetails.username, pageable)
        return ResponseEntity.ok(chatRooms)
    }
}