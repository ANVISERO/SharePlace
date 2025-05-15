package com.anvisero.shareplace.user.service

import com.anvisero.shareplace.exception.ForbiddenException
import com.anvisero.shareplace.exception.NotFoundException
import com.anvisero.shareplace.security.model.yandex.YandexUser
import com.anvisero.shareplace.user.model.User
import com.anvisero.shareplace.user.model.UserInfo
import com.anvisero.shareplace.user.model.UserSearchResponse
import com.anvisero.shareplace.user.model.enum.AccountStatus
import com.anvisero.shareplace.user.model.enum.Role
import com.anvisero.shareplace.user.repository.UserInfoRepository
import com.anvisero.shareplace.user.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userInfoRepository: UserInfoRepository
) {
    val log: Logger = LoggerFactory.getLogger(UserService::class.java)

    fun findByYandexId(yandexId: String): User? {
        log.info("retrieve user with yandex id: $yandexId")
        return userRepository.findByYandexId(yandexId)
    }

fun findById(userId: String): User? {
        log.info("retrieve user with user id: $userId")
        return userRepository.findById(userId.toLong()).orElseThrow {  NotFoundException("Пользователь", userId) }
    }

    fun registerUser(yandexUser: YandexUser): User {
        log.info("register user with yandex id: ${yandexUser.id}")

        val user = convertToUser(yandexUser)
        val userInfo = convertToUserInfo(yandexUser)

        val savedUser = userRepository.save(user)
        userInfo.id = savedUser.id!!
        val savedUserInfo = userInfoRepository.save(userInfo)
        return savedUser
    }

    fun getUserProfile(requestedId: Long, userId: String):
            UserInfo {
        val user = userRepository.findById(requestedId)
            .orElseThrow { NotFoundException("Пользователь", requestedId) }

        if (user.id.toString() != userId) {
            throw ForbiddenException()
        }

        return userInfoRepository.findById(requestedId)
            .orElseThrow { NotFoundException("Информация о пользователе", requestedId) }
    }

    fun getMyUserProfile(userId: String): UserInfo {
        val user : User = userRepository.findById(userId.toLong()).orElseThrow { Exception("Can not convert String to Long") }
            ?: throw NotFoundException("Пользователь", userId)

        return userInfoRepository.findById(user.id!!)
            .orElseThrow { NotFoundException("Информация о пользователе", user.id.toString()) }
    }

    fun updateUserProfile(requestedId: Long, userId: String, update: UserInfo): UserInfo {
        val user = userRepository.findById(requestedId)
            .orElseThrow { NotFoundException("Пользователь", requestedId) }

        if (user.id.toString() != userId) {
            throw ForbiddenException()
        }

        val existing = userInfoRepository.findById(requestedId)
            .orElseThrow { NotFoundException("Информация о пользователе", requestedId) }

        existing.apply {
            name = update.name
            surname = update.surname
            email = update.email
            phone = update.phone
            profilePictureUrl = update.profilePictureUrl
            birthday = update.birthday
            sex = update.sex
            profession = update.profession
            aboutMe = update.aboutMe
            languages = update.languages
        }

        return userInfoRepository.save(existing)
    }

    fun searchByQuery(query: String): List<UserSearchResponse> {
        val pageable = PageRequest.of(0, 10)
        val users = userInfoRepository.searchByNameOrEmail(query, pageable)
        return users
    }

    fun convertToUser(yandexUser: YandexUser): User {
        return User(
            yandexId = yandexUser.id,
            role = Role.USER,
            accountStatus = AccountStatus.NEW
        )
    }

    fun convertToUserInfo(yandexUser: YandexUser): UserInfo {
        return UserInfo(
            name = yandexUser.firstName,
            surname = yandexUser.lastName,
            email = yandexUser.defaultEmail,
            phone = yandexUser.defaultPhone?.number ?: null,
            profilePictureUrl = yandexUser.defaultAvatarId,
            birthday = yandexUser.birthday,
            sex = yandexUser.sex
        )
    }
}