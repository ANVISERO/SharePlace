package com.anvisero.shareplace.chat.repository

import com.anvisero.shareplace.chat.model.ChatRoom
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ChatRoomRepository : MongoRepository<ChatRoom, String> {

    @Query("{ 'participantsInfo.?0': { \$exists: true } }")
    fun findByParticipantInInfoMapOrderByLastActivityAtDesc(userId: String, pageable: Pageable): Page<ChatRoom>

    // Пример для другого метода, если он был и его нужно адаптировать:
    // fun findByParticipantIdsContaining(userId: String): List<ChatRoom>
    @Query("{ 'participantsInfo.?0': { \$exists: true } }")
    fun findByParticipantInInfoMap(userId: String): List<ChatRoom>
}