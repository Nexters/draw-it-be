package com.draw.it.api.user

interface UserRepository {
    fun findByProviderAndProviderId(provider: OAuth2Provider, providerId: String): User?
    fun save(user: User): User
}