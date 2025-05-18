package com.anvisero.shareplace.security.config

import com.anvisero.shareplace.security.TokenCookieSessionAuthenticationStrategy
import com.anvisero.shareplace.security.configurer.TokenCookieAuthenticationConfigurer
import com.anvisero.shareplace.security.serialize.TokenCookieJweStringDeserializer
import com.anvisero.shareplace.security.serialize.TokenCookieJweStringSerializer
import com.anvisero.shareplace.user.service.UserService
import com.nimbusds.jose.crypto.DirectDecrypter
import com.nimbusds.jose.crypto.DirectEncrypter
import com.nimbusds.jose.jwk.OctetSequenceKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity(debug = false)
class SecurityConfig {

    @Bean
    @Throws(Exception::class)
    fun tokenCookieJweStringSerializer(
        @Value("\${jwt.cookie-token-key}") cookieTokenKey: String
    ): TokenCookieJweStringSerializer? {
        return TokenCookieJweStringSerializer(
            DirectEncrypter(
                OctetSequenceKey.parse(cookieTokenKey)
            )
        )
    }

    @Bean
    @Throws(java.lang.Exception::class)
    fun tokenCookieAuthenticationConfigurer(
        @Value("\${jwt.cookie-token-key}") cookieTokenKey: String,
        userService: UserService
    ): TokenCookieAuthenticationConfigurer {
        return TokenCookieAuthenticationConfigurer()
            .tokenCookieStringDeserializer(
                TokenCookieJweStringDeserializer(
                    DirectDecrypter(
                        OctetSequenceKey.parse(cookieTokenKey)
                    )
                )
            )
            .userService(userService)
    }

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        tokenCookieJweStringSerializer: TokenCookieJweStringSerializer,
        tokenCookieAuthenticationConfigurer: TokenCookieAuthenticationConfigurer
    ): SecurityFilterChain {
        val strategy = TokenCookieSessionAuthenticationStrategy()
        strategy.setTokenStringSerializer(tokenCookieJweStringSerializer)

        http
//            .with(HexConfigurer()) {}
            .cors { corsConfigurer ->
                corsConfigurer.configurationSource(corsConfigurationSource())
            }
            .csrf { it.disable() }
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers("/api/v1/auth/yandex/signin").permitAll()
                    .requestMatchers("/error").permitAll()
                    .anyRequest().authenticated()
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .sessionAuthenticationStrategy(strategy)
            }
            .with(tokenCookieAuthenticationConfigurer) {}
//            .csrf { csrf ->
//                csrf.csrfTokenRepository(CookieCsrfTokenRepository())
//                    .csrfTokenRequestHandler(CsrfTokenRequestAttributeHandler())
//                    .sessionAuthenticationStrategy { _, _, _ -> }
//            }
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()

        // 1. Укажите РАЗРЕШЕННЫЕ ИСТОЧНИКИ (URL вашего фронтенда)
        // НЕ ИСПОЛЬЗУЙТЕ "*", если allowCredentials = true
        config.allowedOrigins = listOf(
            "http://localhost:3000", // Для локальной разработки фронтенда
            "http://localhost:5173", // Если используете Vite, как в предыдущих примерах
            "https://your-production-frontend.com" // Для продакшена
            // Добавьте другие домены, если необходимо
        )

        // 2. Укажите РАЗРЕШЕННЫЕ HTTP-МЕТОДЫ
        config.allowedMethods = listOf(
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "OPTIONS", // OPTIONS всегда должен быть разрешен для preflight-запросов
            "PATCH"
        )

        config.allowedHeaders = listOf(
            "Authorization",        // Для токенов аутентификации
            "Content-Type",         // Для указания типа контента (например, application/json)
            "Accept",
            "X-Requested-With",
            "Origin",               // Обычно добавляется браузером
            "Access-Control-Request-Method", // Обычно добавляется браузером
            "Access-Control-Request-Headers" // Обычно добавляется браузером
        )

        config.exposedHeaders = listOf(
            "Content-Disposition"
        )

        config.allowCredentials = true

        config.maxAge = 3600L

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)

        return source
    }

}
