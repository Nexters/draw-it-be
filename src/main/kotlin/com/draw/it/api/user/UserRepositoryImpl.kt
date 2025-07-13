package com.draw.it.api.user

import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository
) : UserRepository {

    override fun findByProviderAndProviderId(provider: OAuth2Provider, providerId: String): User? {
        return userJpaRepository.findByProviderAndProviderId(provider, providerId)
    }

    override fun save(user: User): User {
        return userJpaRepository.save(user)
    }
}