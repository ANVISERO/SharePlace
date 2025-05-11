package com.anvisero.shareplace.security.configurer

import com.anvisero.shareplace.security.converter.HexAuthenticationConverter
import com.anvisero.shareplace.security.filter.HexAuthenticationFilter
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.AuthenticationConverter
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler
import org.springframework.security.web.authentication.AuthenticationFilter
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

class HexConfigurer : AbstractHttpConfigurer<HexConfigurer, HttpSecurity>() {
    private var authenticationEntryPoint: AuthenticationEntryPoint =
        AuthenticationEntryPoint { request, response, authException ->
            response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Hex")
            response.sendError(HttpStatus.UNAUTHORIZED.value())
        }


    override fun init(builder: HttpSecurity) {
        builder.exceptionHandling {
            it.authenticationEntryPoint(authenticationEntryPoint)
        }
    }

    override fun configure(builder: HttpSecurity) {
        val authenticationManager = builder.getSharedObject(AuthenticationManager::class.java)
        val authenticationFilter = AuthenticationFilter(authenticationManager, HexAuthenticationConverter())
        authenticationFilter.setSuccessHandler { request, response, authException -> }
        authenticationFilter.setFailureHandler(AuthenticationEntryPointFailureHandler(authenticationEntryPoint))

        builder.addFilterBefore(authenticationFilter, BasicAuthenticationFilter::class.java)
    }

    fun authenticationEntryPoint(authenticationEntryPoint: AuthenticationEntryPoint): HexConfigurer {
        this.authenticationEntryPoint = authenticationEntryPoint
        return this
    }
}