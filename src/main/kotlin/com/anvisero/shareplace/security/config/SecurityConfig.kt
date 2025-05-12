package com.anvisero.shareplace.security.config

import com.anvisero.shareplace.security.TokenCookieSessionAuthenticationStrategy
import com.anvisero.shareplace.security.configurer.TokenCookieAuthenticationConfigurer
import com.anvisero.shareplace.security.serialize.TokenCookieJweStringDeserializer
import com.anvisero.shareplace.security.serialize.TokenCookieJweStringSerializer
import com.anvisero.shareplace.service.UserService
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
@EnableWebSecurity(debug = true)
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
                    .requestMatchers("/yandex/signin").permitAll()
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

//        http
////            .authorizeHttpRequests { authorizeHttpRequests ->
////                authorizeHttpRequests.anyRequest().authenticated()
////                authorizeHttpRequests.requestMatchers("/hello.html").permitAll()
////            }
////            .csrf { disable() }
////            .httpBasic(withDefaults())
////            .addFilterBefore(DeniedClientFilter(), DisableEncodeUrlFilter::class.java)
////            .formLogin { }
//            .authorizeHttpRequests { authorizeHttpRequests ->
//                authorizeHttpRequests.requestMatchers("/hello.html").permitAll()
//                    .anyRequest().authenticated()
//            }
////            .exceptionHandling {
////                it.authenticationEntryPoint { request, response, authException ->
////                    authException.printStackTrace()
//////                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
////                    response.sendRedirect("http://localhost:8081/login")
////                }
////            }
//        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()

        config.allowedOrigins = mutableListOf<String?>("http://localhost:5173") // разрешаем только фронт
        config.setAllowedMethods(mutableListOf<String?>("GET", "POST", "OPTIONS"))
        config.allowedHeaders = mutableListOf<String?>("*") // или явно перечисли нужные заголовки
        config.allowCredentials = true // разрешить куки и авторизацию
        config.maxAge = 3600L // кеш preflight-запроса на 1 час

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config) // применяем ко всем endpoint'ам

        return source
    }

}
