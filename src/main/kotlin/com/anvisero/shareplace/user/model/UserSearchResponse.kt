package com.anvisero.shareplace.user.model

data class UserSearchResponse(
    var id: Long,
    val name: String,
    val surname: String,
    val email: String
)
