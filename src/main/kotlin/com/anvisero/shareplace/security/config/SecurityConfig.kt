package com.anvisero.shareplace.security.config

import com.anvisero.shareplace.security.configurer.JwtAuthenticationConfigurer
import com.anvisero.shareplace.security.utils.serialize.AccessTokenJwsStringSerializer
import com.anvisero.shareplace.security.utils.serialize.RefreshTokenJweStringSerializer
import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.crypto.DirectEncrypter
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.jwk.OctetSequenceKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import java.text.ParseException

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
//            .with(HexConfigurer()) {}
            .csrf { it.disable() }  // Отключаем CSRF для API
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers("/yandex/signin").permitAll()
                    .anyRequest().authenticated()
            }
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

}
