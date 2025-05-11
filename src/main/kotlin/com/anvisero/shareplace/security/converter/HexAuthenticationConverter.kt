package com.anvisero.shareplace.security.converter

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.codec.Hex
import org.springframework.security.web.authentication.AuthenticationConverter

class HexAuthenticationConverter: AuthenticationConverter {
    override fun convert(request: HttpServletRequest): Authentication? {
        val authentication = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authentication != null && authentication.startsWith("Hex ")) {
            val rawToken = authentication.replace("^Hex ".toRegex(), "")
            val token = Hex.decode(rawToken).toString(Charsets.UTF_8)
            val tokenParts = token.split(":")

           return UsernamePasswordAuthenticationToken.unauthenticated(tokenParts[0], tokenParts[1])
        }

        return null
    }
}