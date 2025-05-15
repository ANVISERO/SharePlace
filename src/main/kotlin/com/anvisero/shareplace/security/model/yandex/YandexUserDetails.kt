package com.anvisero.shareplace.security.model.yandex

import com.anvisero.shareplace.security.model.Token
import com.anvisero.shareplace.user.model.User
import com.anvisero.shareplace.user.model.enum.AccountStatus
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.Instant

class YandexUserDetails(
    private val user: User,
    private val token: Token? = null,
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))
    }

    override fun getPassword(): String? = null

    override fun getUsername(): String = user.id.toString()

    override fun isAccountNonExpired(): Boolean =
        user.accountStatus != AccountStatus.BLOCKED

    override fun isAccountNonLocked(): Boolean =
        user.accountStatus != AccountStatus.BLOCKED

    override fun isCredentialsNonExpired(): Boolean {
        if (token != null) {
            return token.expiresAt.isAfter(Instant.now())
        }
        return user.accountStatus != AccountStatus.BLOCKED
    }

    override fun isEnabled(): Boolean =
        user.accountStatus != AccountStatus.BLOCKED
}