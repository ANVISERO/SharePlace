package com.anvisero.shareplace.repository

import com.anvisero.shareplace.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Long> {

    fun findByYandexId(yandexId: String): User?
    fun existsByYandexId(yandexId: String): Boolean
}