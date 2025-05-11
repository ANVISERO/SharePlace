package com.anvisero.shareplace.service

import com.anvisero.shareplace.model.User
import com.anvisero.shareplace.model.UserInfo
import com.anvisero.shareplace.model.enum.AccountStatus
import com.anvisero.shareplace.model.enum.Role
import com.anvisero.shareplace.model.yandex.YandexUser
import com.anvisero.shareplace.repository.UserInfoRepository
import com.anvisero.shareplace.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

    fun existsByYandexId(yandexId: String): Boolean {
        log.info("retrieve user with yandex id: $yandexId")
        return userRepository.existsByYandexId(yandexId)
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

    fun convertToUser(yandexUser: YandexUser): User {
        return User(
            yandexId = yandexUser.id,
            role = Role.USER,
            email = yandexUser.defaultEmail,
            phone = yandexUser.defaultPhone.number,
            accountStatus = AccountStatus.NEW
        )
    }

    fun convertToUserInfo(yandexUser: YandexUser): UserInfo {
        return UserInfo(
            name = yandexUser.firstName,
            surname = yandexUser.lastName,
            profilePictureUrl = yandexUser.defaultAvatarId,
            birthday = yandexUser.birthday,
            sex = yandexUser.sex
        )
    }
}