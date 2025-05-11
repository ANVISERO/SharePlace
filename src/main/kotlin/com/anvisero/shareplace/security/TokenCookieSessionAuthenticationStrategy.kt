package com.anvisero.shareplace.security

import com.anvisero.shareplace.security.model.Token
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.session.SessionAuthenticationException
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.function.Function

class TokenCookieSessionAuthenticationStrategy : SessionAuthenticationStrategy {
    private var tokenCookieFactory: Function<Authentication, Token> = TokenCookieFactory()

    private var tokenStringSerializer: Function<Token, String?> = Function { o: Token -> Objects.toString(o) }

    @Throws(SessionAuthenticationException::class)
    override fun onAuthentication(
        authentication: Authentication?, request: HttpServletRequest?,
        response: HttpServletResponse
    ) {
        if (authentication is UsernamePasswordAuthenticationToken) {
            val token: Token = this.tokenCookieFactory.apply(authentication)
            val tokenString = this.tokenStringSerializer.apply(token)

            val cookie = Cookie("__Host-auth-token", tokenString)
            cookie.path = "/"
            cookie.domain = null
            cookie.secure = true
            cookie.isHttpOnly = true
            cookie.maxAge = ChronoUnit.SECONDS.between(Instant.now(), token.expiresAt).toInt()

            response.addCookie(cookie)
        }
    }

    fun setTokenCookieFactory(tokenCookieFactory: Function<Authentication, Token>) {
        this.tokenCookieFactory = tokenCookieFactory
    }

    fun setTokenStringSerializer(tokenStringSerializer: Function<Token, String?>) {
        this.tokenStringSerializer = tokenStringSerializer
    }
}