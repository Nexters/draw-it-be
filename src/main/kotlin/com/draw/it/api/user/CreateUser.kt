package com.draw.it.api.user

import com.draw.it.api.user.domain.OAuth2Provider
import com.draw.it.api.user.domain.User
import com.draw.it.api.user.domain.UserRepository
import org.springframework.stereotype.Service

@Service
class CreateUser(
    private val userRepository: UserRepository
) {

    fun getOrCreateUser(
        name: String,
        provider: OAuth2Provider,
        providerId: String,
    ): Long {
        val existingUser = userRepository.findByProviderAndProviderId(provider, providerId)
        if (existingUser != null) return existingUser.id!!

        val user = User(
            name = name,
            provider = provider,
            providerId = providerId,
        )

        val savedUser = userRepository.save(user)
        return savedUser.id!!
    }
}