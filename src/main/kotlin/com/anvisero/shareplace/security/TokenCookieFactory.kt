package com.anvisero.shareplace.security

import com.anvisero.shareplace.security.model.Token
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.function.Function

class TokenCookieFactory : Function<Authentication, Token> {
    private var tokenTtl: Duration = Duration.ofDays(1)

    override fun apply(authentication: Authentication): Token {
        val now = Instant.now()
        return Token(
            UUID.randomUUID(), authentication.name,
            authentication.authorities.stream()
                .map { obj: GrantedAuthority? -> obj!!.authority }.toList(),
            now, now.plus(this.tokenTtl)
        )
    }

    fun setTokenTtl(tokenTtl: Duration) {
        this.tokenTtl = tokenTtl
    }
}