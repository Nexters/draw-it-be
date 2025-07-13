package com.draw.it.api.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<User, Long> {
    fun findByProviderAndProviderId(provider: OAuth2Provider, providerId: String): User?
}