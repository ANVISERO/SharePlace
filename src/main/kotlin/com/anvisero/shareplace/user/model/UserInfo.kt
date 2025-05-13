package com.anvisero.shareplace.user.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "users_info")
data class UserInfo(
    @Id
    @Column(name = "user_id")
    var id: Long? = null,
    @Column(name = "name")
    var name: String,
    @Column(name = "surname")
    var surname: String,
    @Column(name = "email")
    var email: String,
    @Column(name = "phone")
    var phone: String? = null,
    @Column(name = "profile_picture_url")
    var profilePictureUrl: String,
    @Column(name = "birthday")
    var birthday: LocalDate? = null,
    @Column(name = "sex")
    var sex: String,
    @Column(name = "profession")
    var profession: String? = null,
    @Column(name = "about_me")
    var aboutMe: String? = null,
    @Column(name = "languages")
    var languages: String? = null,
    @CreatedDate
    @Column(name = "created_at")
    var createdAt: Instant? = null,
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,
)
