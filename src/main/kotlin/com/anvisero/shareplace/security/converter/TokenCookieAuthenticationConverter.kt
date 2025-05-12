package com.anvisero.shareplace.security.converter

import com.anvisero.shareplace.security.model.Token
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationConverter
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import java.util.function.Function
import java.util.stream.Stream

class TokenCookieAuthenticationConverter(
    private val tokenCookieStringDeserializer: Function<String?, Token?>?
) : AuthenticationConverter {

    override fun convert(request: HttpServletRequest): Authentication? {
        if (request.cookies != null) {
            return Stream.of(*request.cookies)
                .filter { cookie: Cookie? -> cookie!!.name == "auth-token" }
                .findFirst()
                .map(Function { cookie: Cookie? ->
                    val token: Token? = this.tokenCookieStringDeserializer!!.apply(cookie!!.value)
                    PreAuthenticatedAuthenticationToken(token, cookie.value)
                })
                .orElse(null)
        }

        return null
    }
}