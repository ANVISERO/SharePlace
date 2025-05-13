package com.anvisero.shareplace.user.repository

import com.anvisero.shareplace.user.model.UserInfo
import com.anvisero.shareplace.user.model.UserSearchResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserInfoRepository: JpaRepository<UserInfo, Long> {

    @Query("""
    SELECT NEW com.anvisero.shareplace.user.model.UserSearchResponse(u.id, u.name, u.surname, u.email)
    FROM UserInfo u 
    WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(u.surname) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))
""")
    fun searchByNameOrEmail(@Param("query") query: String, pageable: Pageable): List<UserSearchResponse>
}