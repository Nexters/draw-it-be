package com.draw.it.api.user.infra

import com.draw.it.api.user.domain.OAuth2Provider
import com.draw.it.api.user.domain.User
import com.draw.it.api.user.domain.UserRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class AdminUserInitializer(
    private val userRepository: UserRepository
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        val adminUserId = 1L
        val existingUser = userRepository.findById(adminUserId)

        if (existingUser == null) {
            val adminUser = User(
                name = "admin",
                provider = OAuth2Provider.ADMIN,
                providerId = "admin"
            )
            userRepository.save(adminUser)
        }
    }
}