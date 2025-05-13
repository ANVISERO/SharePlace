package com.anvisero.shareplace.chat.repository

import com.anvisero.shareplace.chat.model.ChatRoom
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRoomRepository : MongoRepository<ChatRoom, String> {

    fun findByParticipantIdsContainingOrderByLastActivityAtDesc(userId: String, pageable: Pageable): Page<ChatRoom>

    fun findByParticipantIdsContaining(userId: String): List<ChatRoom>
}