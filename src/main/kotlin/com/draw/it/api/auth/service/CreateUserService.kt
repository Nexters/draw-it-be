package com.draw.it.api.auth.service

import com.draw.it.api.user.OAuth2Provider
import com.draw.it.api.user.User
import com.draw.it.api.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateUserService(
    private val userRepository: UserRepository,
) {
    fun getOrCreate(
        provider: OAuth2Provider,
        providerId: String,
        name: String
    ): Long {
        val user = userRepository.findByProviderAndProviderId(provider, providerId)
            ?: userRepository.save(
                User(
                    name = name,
                    provider = provider,
                    providerId = providerId
                )
            )
        val userId = user.id!!
        return userId
    }
}