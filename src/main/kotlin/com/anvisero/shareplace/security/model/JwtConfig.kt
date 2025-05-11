package com.anvisero.shareplace.security.model

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class JwtConfig(
    @Value("\${security.jwt.uri:}")
    val uri: String? = null,
    @Value("\${security.jwt.header:}")
    val header: String? = null,
    @Value("\${security.jwt.prefix:}")
    val prefix: String? = null,
    @Value("\${security.jwt.expiration:}")
    val expiration: Int = 0,
    @Value("\${security.jwt.secret:}")
    val secret: String? = null
)
