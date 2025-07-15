package com.draw.it.api.user.domain

interface UserRepository {
    fun save(user: User): User
    fun findByProviderAndProviderId(provider: OAuth2Provider, providerId: String): User?
    fun getBy(id: Long): User
}