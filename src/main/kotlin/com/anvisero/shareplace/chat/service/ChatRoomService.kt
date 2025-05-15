package com.anvisero.shareplace.chat.service

import com.anvisero.shareplace.chat.model.ChatRoom
import com.anvisero.shareplace.chat.model.ParticipantInfo
import com.anvisero.shareplace.chat.model.enum.RoomType
import com.anvisero.shareplace.chat.payload.CreateChatRoomRequest
import com.anvisero.shareplace.chat.repository.ChatRoomRepository
import com.anvisero.shareplace.user.model.UserInfo
import com.anvisero.shareplace.user.repository.UserInfoRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.Instant
import kotlin.collections.associate

@Service
class ChatRoomService(
    private val chatRoomRepository: ChatRoomRepository,
    private val userInfoRepository: UserInfoRepository,
) {

    fun createChatRoom(request: CreateChatRoomRequest, currentUserId: String): ChatRoom {
        val allParticipantIdsForNewRoom: Set<String> = request.participantIds + currentUserId

        if (request.type == RoomType.PRIVATE && allParticipantIdsForNewRoom.size != 2) {
            throw IllegalArgumentException("Приватные чаты должны иметь ровно двух участников.")
        }
        if (request.type == RoomType.GROUP && request.name.isNullOrBlank()) {
            throw IllegalArgumentException("Групповые чаты должны иметь имя.")
        }

        val participantLongIds = allParticipantIdsForNewRoom .mapNotNull { it.toLongOrNull() }
        val userInfosFound: List<UserInfo> = if (participantLongIds.isNotEmpty()) {
            userInfoRepository.findAllById(participantLongIds)
        } else {
            emptyList()
        }

        val participantsInfoMap: Map<String, ParticipantInfo> = userInfosFound
            .associate { userInfo ->
                val participantIdString = userInfo.id.toString()
                participantIdString to ParticipantInfo(
                    userId = participantIdString,
                    firstName = userInfo.name,
                    surname = userInfo.surname,
                    profilePictureUrl = userInfo.profilePictureUrl
                )
            }

        val chatRoom = ChatRoom(
            name = request.name,
            participantsInfo = participantsInfoMap,
            type = request.type,
            creatorId = currentUserId,
            lastActivityAt = Instant.now(),
            createdAt = Instant.now()
        )
        val savedRoom = chatRoomRepository.save(chatRoom)

        return savedRoom
    }


    fun getChatRoomById(roomId: String, currentUserId: String): ChatRoom? {
        return chatRoomRepository.findById(roomId)
            .filter { it.participantsInfo.keys.contains(currentUserId) }
            .orElse(null)
    }

    fun getChatRoomsForCurrentUser(currentUserId: String, pageable: Pageable): Page<ChatRoom> {
        val chatRoomsPage = chatRoomRepository.findByParticipantInInfoMapOrderByLastActivityAtDesc(currentUserId, pageable)

        return chatRoomsPage
        }

    fun updateRoomActivity(roomId: String) {
        chatRoomRepository.findById(roomId).ifPresent { room ->
            room.lastActivityAt = Instant.now()
            chatRoomRepository.save(room)
        }
    }

}