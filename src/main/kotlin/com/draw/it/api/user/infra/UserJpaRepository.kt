package com.draw.it.api.user.infra

import com.draw.it.api.user.domain.OAuth2Provider
import com.draw.it.api.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface UserJpaRepository : JpaRepository<User, Long> {
    fun findByProviderAndProviderId(provider: OAuth2Provider, providerId: String): User?
    fun countByCreatedAtBetween(startDate: LocalDateTime, endDate: LocalDateTime): Long
}