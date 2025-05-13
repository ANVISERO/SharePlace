package com.anvisero.shareplace.user.payload

import java.time.LocalDate

data class UserInfoUpdateRequest(
    val name: String,
    val surname: String,
    val phone: String,
    val profilePictureUrl: String,
    val birthday: LocalDate?,
    val sex: String,
    val profession: String?,
    val aboutMe: String?,
    val languages: String?
)
