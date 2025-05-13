package com.anvisero.shareplace.user.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class PingController {
    @GetMapping("/v1/ping")
    fun pingV1(): ResponseEntity<Map<String, String>> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetails
        return ResponseEntity.ok().body(
            mapOf(
                "status" to "ping",
                "password" to userDetails.password,
                "authorities" to userDetails.authorities.stream().toArray().joinToString(",")
            )
        )
    }

    @GetMapping("/v2/ping")
    fun pingV2(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok().body(
            mapOf(
                "status" to "ping",
                "username" to userDetails.username,
                "authorities" to userDetails.authorities.stream().toArray().joinToString(",")
            )
        )
    }
}