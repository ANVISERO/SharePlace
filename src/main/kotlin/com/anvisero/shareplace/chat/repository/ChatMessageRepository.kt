package com.anvisero.shareplace.chat.repository

import com.anvisero.shareplace.chat.model.ChatMessage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatMessageRepository : MongoRepository<ChatMessage, String> {

    fun findByRoomIdOrderByTimestampAsc(roomId: String): List<ChatMessage>

    fun findByRoomIdOrderByTimestampDesc(roomId: String, pageable: Pageable): Page<ChatMessage>
}