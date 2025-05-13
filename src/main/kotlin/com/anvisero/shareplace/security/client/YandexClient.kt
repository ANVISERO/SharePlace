package com.anvisero.shareplace.security.client

import com.anvisero.shareplace.security.model.yandex.YandexUser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForObject
import org.springframework.web.util.UriComponentsBuilder

@Component
class YandexClient(private val restTemplate: RestTemplate) {
    private val YANDEX_LOGIN_BASE_URL = "https://login.yandex.ru"
    val log: Logger = LoggerFactory.getLogger(YandexClient::class.java)


    fun getUserInfo(oauthToken: String): YandexUser? {
        val path = "/info"
        val uri = UriComponentsBuilder
            .fromUriString("$YANDEX_LOGIN_BASE_URL$path")
            .queryParam("oauth_token", oauthToken)
            .build()
            .toUriString()
        log.info("loginUser after uri $uri")
        return restTemplate.postForObject(url = uri, request = YandexUser::class.java)
    }
}