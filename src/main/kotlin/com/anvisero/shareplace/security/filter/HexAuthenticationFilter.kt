package com.anvisero.shareplace.security.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolderStrategy
import org.springframework.security.crypto.codec.Hex
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.web.filter.OncePerRequestFilter

class HexAuthenticationFilter(
    private val authenticationManager: AuthenticationManager,
    private val authenticationEntryPoint: AuthenticationEntryPoint
): OncePerRequestFilter() {
    private val securityContextHolderStrategy: SecurityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy()
    private val securityContextRepository: SecurityContextRepository = RequestAttributeSecurityContextRepository()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authentication  = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authentication != null && authentication.startsWith("Hex ")) {
            val rawToken = authentication.replace("^Hex ".toRegex(), "")
            val token = Hex.decode(rawToken).toString(Charsets.UTF_8)
            val tokenParts = token.split(":")

            val authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(tokenParts[0], tokenParts[1])

            try {
                val authenticationResult = authenticationManager.authenticate(authenticationRequest)
                val context = securityContextHolderStrategy.createEmptyContext()
                context.authentication = authenticationResult
                securityContextHolderStrategy.context = context
                securityContextRepository.saveContext(context, request, response)
            } catch (authException: AuthenticationException) {
                securityContextHolderStrategy.clearContext()
                authenticationEntryPoint.commence(request, response, authException)
                return
            }
        }

       filterChain.doFilter(request, response)
    }
}