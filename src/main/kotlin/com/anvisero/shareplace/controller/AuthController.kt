package com.anvisero.shareplace.controller

import com.anvisero.shareplace.payload.yandex.YandexLoginRequest
import com.anvisero.shareplace.security.service.YandexService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val yandexService: YandexService
) {
    val log: Logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/yandex/signin")
    fun yandexSignIn(@RequestBody yandexLoginRequest: YandexLoginRequest): ResponseEntity<String> {
        log.info("yandex login $yandexLoginRequest")
        val user = yandexService.loginUser(yandexLoginRequest.oauthToken)
        return ResponseEntity.ok(user)
    }
}