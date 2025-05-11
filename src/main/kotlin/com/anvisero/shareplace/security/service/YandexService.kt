package com.anvisero.shareplace.security.service

import com.anvisero.shareplace.security.client.YandexClient
import com.anvisero.shareplace.service.UserService
import org.hibernate.annotations.NotFound
import org.springframework.stereotype.Service

@Service
class YandexService(
    private val yandexClient: YandexClient,
    private val userService: UserService,
) {

    fun loginUser(oauthToken: String): String {
        val yandexUser = yandexClient.getUserInfo(oauthToken)
        println(yandexUser.toString())

//        if (yandexUser != null) {
//            if (!userService.existsByYandexId(yandexUser.id)) {
//                userService.registerUser(yandexUser)
//            }
//
//        } else {
//            throw Exception("Yandex User not found")
//        }

        return yandexUser.toString()
    }


}