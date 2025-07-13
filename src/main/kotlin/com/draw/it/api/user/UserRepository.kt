package com.draw.it.api.user

interface UserRepository {
    fun save(user: User): User
    fun findByProviderAndProviderId(provider: OAuth2Provider, providerId: String): User?
}