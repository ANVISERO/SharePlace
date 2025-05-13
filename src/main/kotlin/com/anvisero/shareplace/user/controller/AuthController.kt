package com.anvisero.shareplace.user.controller

import com.anvisero.shareplace.security.model.yandex.YandexLoginRequest
import com.anvisero.shareplace.security.service.YandexService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val yandexService: YandexService
) {
    val log: Logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping("/yandex/signin")
    fun yandexSignIn(
        @RequestBody yandexLoginRequest: YandexLoginRequest,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<String> {
        log.info("yandex login $yandexLoginRequest")
        val user = yandexService.loginUser(yandexLoginRequest.oauthToken, request, response)
        return ResponseEntity.ok(user)
    }
}