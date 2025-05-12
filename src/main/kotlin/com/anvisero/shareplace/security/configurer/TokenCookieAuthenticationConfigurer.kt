package com.anvisero.shareplace.security.configurer

import com.anvisero.shareplace.security.converter.TokenCookieAuthenticationConverter
import com.anvisero.shareplace.security.model.Token
import com.anvisero.shareplace.security.service.TokenAuthenticationUserDetailsService
import com.anvisero.shareplace.service.UserService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler
import org.springframework.security.web.authentication.AuthenticationFilter
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider
import org.springframework.security.web.csrf.CsrfFilter
import java.util.function.Function

class TokenCookieAuthenticationConfigurer
    : AbstractHttpConfigurer<TokenCookieAuthenticationConfigurer?, HttpSecurity>() {
    private var tokenCookieStringDeserializer: Function<String?, Token?>? = null

    private var userService: UserService? = null

    @Throws(Exception::class)
    override fun init(builder: HttpSecurity) {
        builder.logout { logout: LogoutConfigurer<HttpSecurity> ->
            logout.addLogoutHandler(
                CookieClearingLogoutHandler("auth-token")
            )
//                .addLogoutHandler { request: HttpServletRequest?, response: HttpServletResponse?, authentication: Authentication? ->
//                    if (authentication != null &&
//                        authentication.principal is TokenUser
//                    ) {
//                        this.jdbcTemplate!!.update(
//                            "insert into t_deactivated_token (id, c_keep_until) values (?, ?)",
//                            user.getToken().id(), Date.from(user.getToken().expiresAt())
//                        )
//
//                        response!!.setStatus(HttpServletResponse.SC_NO_CONTENT)
//                    }
//                }
        }
    }

    @Throws(Exception::class)
    override fun configure(builder: HttpSecurity) {
        val cookieAuthenticationFilter = AuthenticationFilter(
            builder.getSharedObject(AuthenticationManager::class.java),
            TokenCookieAuthenticationConverter(this.tokenCookieStringDeserializer)
        )
        cookieAuthenticationFilter.setSuccessHandler { _, _, _ -> }
        cookieAuthenticationFilter.setFailureHandler(
            AuthenticationEntryPointFailureHandler(
                Http403ForbiddenEntryPoint()
            )
        )

        val authenticationProvider = PreAuthenticatedAuthenticationProvider()
        authenticationProvider.setPreAuthenticatedUserDetailsService(
            TokenAuthenticationUserDetailsService(userService)
        )

        builder.addFilterAfter(cookieAuthenticationFilter, CsrfFilter::class.java)
            .authenticationProvider(authenticationProvider)
    }

    fun tokenCookieStringDeserializer(
        tokenCookieStringDeserializer: Function<String?, Token?>?
    ): TokenCookieAuthenticationConfigurer {
        this.tokenCookieStringDeserializer = tokenCookieStringDeserializer
        return this
    }

    fun userService(
        userService: UserService
    ): TokenCookieAuthenticationConfigurer {
        this.userService = userService
        return this
    }
}