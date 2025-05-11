package com.anvisero.shareplace.security.client

import com.anvisero.shareplace.model.yandex.YandexUser
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity
import org.springframework.web.client.postForObject
import org.springframework.web.util.UriComponentsBuilder

@Component
class YandexClient(private val restTemplate: RestTemplate) {
    private val YANDEX_LOGIN_BASE_URL = "https://login.yandex.ru"

    fun getUserInfo(oauthToken: String): YandexUser? {
        val path = "/info"
        val uri = UriComponentsBuilder
            .fromUriString("$YANDEX_LOGIN_BASE_URL$path")
            .queryParam("oauth_token", oauthToken)
            .build()
            .toUriString()
        return restTemplate.postForObject(uri, YandexUser::class.java)
    }
}