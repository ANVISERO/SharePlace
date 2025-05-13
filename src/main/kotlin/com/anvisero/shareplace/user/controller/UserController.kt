package com.anvisero.shareplace.user.controller

import com.anvisero.shareplace.user.model.UserInfo
import com.anvisero.shareplace.user.model.UserSearchResponse
import com.anvisero.shareplace.user.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) {

    val log: Logger = LoggerFactory.getLogger(AuthController::class.java)

    @GetMapping("/{id}/profile")
    fun getProfile(
        @PathVariable id: Long
    ): ResponseEntity<UserInfo> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetails
        val userInfo = userService.getUserProfile(id, userDetails.username)
        return ResponseEntity.ok(userInfo)
    }

    @GetMapping("/me")
    fun getMyProfile(): ResponseEntity<UserInfo> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetails
        val userInfo = userService.getMyUserProfile(userDetails.username)
        return ResponseEntity.ok(userInfo)
    }

    @PutMapping("/{id}/profile")
    fun updateProfile(
        @PathVariable id: Long,
        @RequestBody update: UserInfo
    ): ResponseEntity<UserInfo> {
        val userDetails = SecurityContextHolder.getContext().authentication.principal as UserDetails
        val updated = userService.updateUserProfile(id, userDetails.username, update)
        return ResponseEntity.ok(updated)
    }

    @GetMapping("/search")
    fun searchUsers(@RequestParam query: String): ResponseEntity<List<UserSearchResponse>> {
        val users = userService.searchByQuery(query)
        return ResponseEntity.ok(users)
    }

}