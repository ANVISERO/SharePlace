package com.anvisero.shareplace.security.configurer

import com.anvisero.shareplace.security.filter.RequestJwtTokensFilter
import com.anvisero.shareplace.security.model.Token
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.access.ExceptionTranslationFilter
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationFilter
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider
import org.springframework.security.web.csrf.CsrfFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import java.util.function.Function

class JwtAuthenticationConfigurer

    : AbstractHttpConfigurer<JwtAuthenticationConfigurer, HttpSecurity>() {
    private var refreshTokenStringSerializer: Function<Token, String?> = Function { obj: Token -> obj.toString() }

    private var accessTokenStringSerializer: Function<Token, String?> = Function { obj: Token -> obj.toString() }

    private var accessTokenStringDeserializer: Function<String?, Token>? = null

    private var refreshTokenStringDeserializer: Function<String?, Token>? = null

    private var jdbcTemplate: JdbcTemplate? = null

    @Throws(java.lang.Exception::class)
    override fun init(builder: HttpSecurity) {

    }

    @Throws(Exception::class)
    override fun configure(builder: HttpSecurity) {
        val requestJwtTokensFilter: RequestJwtTokensFilter = RequestJwtTokensFilter()
        requestJwtTokensFilter.setAccessTokenStringSerializer(this.accessTokenStringSerializer)
        requestJwtTokensFilter.setRefreshTokenStringSerializer(this.refreshTokenStringSerializer)

        builder.addFilterAfter(requestJwtTokensFilter, ExceptionTranslationFilter::class.java)

//        val jwtAuthenticationFilter: AuthenticationFilter = AuthenticationFilter(
//            builder.getSharedObject<C?>(AuthenticationManager::class.java),
//            JwtAuthenticationConverter(this.accessTokenStringDeserializer, this.refreshTokenStringDeserializer)
//        )
//        jwtAuthenticationFilter
//            .setSuccessHandler(AuthenticationSuccessHandler { request: HttpServletRequest?, response: HttpServletResponse?, authentication: Authentication? ->
//                CsrfFilter.skipRequest(
//                    request
//                )
//            })
//        jwtAuthenticationFilter
//            .setFailureHandler(AuthenticationFailureHandler { request: HttpServletRequest?, response: HttpServletResponse?, exception: AuthenticationException? ->
//                response!!.sendError(
//                    HttpServletResponse.SC_FORBIDDEN
//                )
//            })
//
//        val authenticationProvider = PreAuthenticatedAuthenticationProvider()
//        authenticationProvider.setPreAuthenticatedUserDetailsService(
//            TokenAuthenticationUserDetailsService(this.jdbcTemplate)
//        )
//
//        val refreshTokenFilter: RefreshTokenFilter = RefreshTokenFilter()
//        refreshTokenFilter.setAccessTokenStringSerializer(this.accessTokenStringSerializer)
//
//        val jwtLogoutFilter: JwtLogoutFilter = JwtLogoutFilter(this.jdbcTemplate)
//
//        builder.addFilterAfter(requestJwtTokensFilter, ExceptionTranslationFilter::class.java)
//            .addFilterBefore(jwtAuthenticationFilter, CsrfFilter::class.java)
//            .addFilterAfter(refreshTokenFilter, ExceptionTranslationFilter::class.java)
//            .addFilterAfter(jwtLogoutFilter, ExceptionTranslationFilter::class.java)
//            .authenticationProvider(authenticationProvider)
    }

    fun refreshTokenStringSerializer(
        refreshTokenStringSerializer: Function<Token, String?>
    ): JwtAuthenticationConfigurer {
        this.refreshTokenStringSerializer = refreshTokenStringSerializer
        return this
    }

    fun accessTokenStringSerializer(
        accessTokenStringSerializer: Function<Token, String?>
    ): JwtAuthenticationConfigurer {
        this.accessTokenStringSerializer = accessTokenStringSerializer
        return this
    }

    fun accessTokenStringDeserializer(
        accessTokenStringDeserializer: Function<String?, Token>
    ): JwtAuthenticationConfigurer {
        this.accessTokenStringDeserializer = accessTokenStringDeserializer
        return this
    }

    fun refreshTokenStringDeserializer(
        refreshTokenStringDeserializer: Function<String?, Token>
    ): JwtAuthenticationConfigurer {
        this.refreshTokenStringDeserializer = refreshTokenStringDeserializer
        return this
    }

    fun jdbcTemplate(jdbcTemplate: JdbcTemplate): JwtAuthenticationConfigurer {
        this.jdbcTemplate = jdbcTemplate
        return this
    }
}