package com.anvisero.shareplace.model

import com.anvisero.shareplace.model.enum.AccountStatus
import com.anvisero.shareplace.model.enum.Role
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    var id: Long? = null,
    @Column(name = "yandex_id")
    var yandexId: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    var role: Role,
    @Column(name = "email")
    var email: String,
    @Column(name = "password")
    var password: String? = null,
    @Column(name = "phone")
    var phone: String,
    @CreatedDate
    @Column(name = "created_at")
    var createdAt: Instant? = null,
    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,
    @Column(name = "account_status")
    var accountStatus: AccountStatus
)