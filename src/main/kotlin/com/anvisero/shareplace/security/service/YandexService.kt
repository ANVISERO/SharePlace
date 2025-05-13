package com.anvisero.shareplace.security.service

import com.anvisero.shareplace.exception.NotFoundException
import com.anvisero.shareplace.security.TokenCookieSessionAuthenticationStrategy
import com.anvisero.shareplace.security.client.YandexClient
import com.anvisero.shareplace.security.model.yandex.YandexUserDetails
import com.anvisero.shareplace.security.serialize.TokenCookieJweStringSerializer
import com.anvisero.shareplace.user.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Service

@Service
class YandexService(
    private val yandexClient: YandexClient,
    private val userService: UserService,
    private val tokenCookieJweStringSerializer: TokenCookieJweStringSerializer
//    private val tokenProvider: JwtTokenProvider
) {

    val log: Logger = LoggerFactory.getLogger(YandexService::class.java)

    fun loginUser(
        oauthToken: String,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): String {
        log.info("loginUser begin")
        val strategy = TokenCookieSessionAuthenticationStrategy()
        strategy.setTokenStringSerializer(tokenCookieJweStringSerializer)
        log.info("loginUser after setTokenStringSerializer")


        val yandexUser = yandexClient.getUserInfo(oauthToken)
            ?: throw NotFoundException("Пользователь", "undefined")

        log.info("yandexUser registerUser $yandexUser")

        val user = userService.findByYandexId(yandexUser.id)
            ?: userService.registerUser(yandexUser)

        log.info("user registerUser $user")

        val userDetails = YandexUserDetails(user)
        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities).apply {
            details = WebAuthenticationDetailsSource().buildDetails(request)
        }

        // Установка в SecurityContext
        SecurityContextHolder.getContext().authentication = authentication

        // Вызов стратегии — записываем токен в cookie
        strategy.onAuthentication(authentication, request, response)

//        val userDetails = YandexUserDetails(user)
//
//        val token = tokenProvider.generateToken(
//            UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
//        ) ?: throw Exception("unable to login facebook user id ${yandexUser.id}")

        return "token"
    }


}