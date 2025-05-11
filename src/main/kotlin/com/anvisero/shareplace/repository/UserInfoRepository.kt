package com.anvisero.shareplace.repository

import com.anvisero.shareplace.model.UserInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserInfoRepository: JpaRepository<UserInfo, String> {
}