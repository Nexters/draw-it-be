package com.draw.it.api.user.infra

import com.draw.it.api.user.domain.OAuth2Provider
import com.draw.it.api.user.domain.User
import com.draw.it.api.user.domain.UserRepository
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository
) : UserRepository {
    override fun save(user: User): User {
        return userJpaRepository.save(user)
    }

    override fun findByProviderAndProviderId(
        provider: OAuth2Provider,
        providerId: String
    ): User? {
        return userJpaRepository.findByProviderAndProviderId(provider, providerId)
    }

    override fun getBy(id: Long): User {
        return userJpaRepository.findById(id)
            .orElseThrow { RuntimeException("User with id $id not found") }
    }
}