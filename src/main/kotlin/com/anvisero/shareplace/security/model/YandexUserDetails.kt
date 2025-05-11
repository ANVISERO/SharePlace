package com.anvisero.shareplace.security.model

import com.anvisero.shareplace.model.User
import com.anvisero.shareplace.model.enum.AccountStatus
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class YandexUserDetails(
    private val user: User
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))
    }

    override fun getPassword(): String? = null // OAuth-пользователи без пароля

    override fun getUsername(): String = user.yandexId

    override fun isAccountNonExpired(): Boolean =
        user.accountStatus != AccountStatus.BLOCKED

    override fun isAccountNonLocked(): Boolean =
        user.accountStatus != AccountStatus.BLOCKED

    override fun isCredentialsNonExpired(): Boolean =
        user.accountStatus != AccountStatus.BLOCKED

    override fun isEnabled(): Boolean =
        user.accountStatus != AccountStatus.BLOCKED
}