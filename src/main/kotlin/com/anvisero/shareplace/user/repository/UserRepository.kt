package com.anvisero.shareplace.user.repository

import com.anvisero.shareplace.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Long> {

    fun findByYandexId(yandexId: String): User?
}