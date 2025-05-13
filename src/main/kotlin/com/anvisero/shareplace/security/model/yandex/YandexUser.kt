package com.anvisero.shareplace.security.model.yandex

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class YandexUser(
    val id: String,
    val login: String,
    @field:JsonProperty("display_name")
    val displayName: String,
    @field:JsonProperty("real_name")
    val realName: String,
    @field:JsonProperty("first_name")
    val firstName: String,
    @field:JsonProperty("last_name")
    val lastName: String,
    val sex: String,
    @field:JsonProperty("default_email")
    val defaultEmail: String,
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val birthday: LocalDate? = null,
    @field:JsonProperty("default_avatar_id")
    val defaultAvatarId: String,
    @field:JsonProperty("default_phone")
    val defaultPhone: YandexPhone? = null,
)
