package com.anvisero.shareplace.security.service

import com.anvisero.shareplace.security.model.Token
import com.anvisero.shareplace.security.model.YandexUserDetails
import com.anvisero.shareplace.service.UserService
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken

class TokenAuthenticationUserDetailsService(
    private val userService: UserService?,
) : AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserDetails(authenticationToken: PreAuthenticatedAuthenticationToken): UserDetails {
        if (authenticationToken.principal is Token) {
            val token = authenticationToken.principal as Token
            println("Token principal: $token")
            val user = userService!!.findByYandexId(token.subject)
            if (user == null) {
                throw Exception("loadUserDetails User Not Found")
            }

            val userDetails = YandexUserDetails(user)
            println("userDetails.toString(): ${userDetails.toString()}")
            return userDetails
        }

        throw UsernameNotFoundException("Principal must me of type Token")
    }
}