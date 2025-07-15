package com.draw.it.api.user

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/user")
class ReadUserService(
    private val userRepository: UserRepository
) {

    @GetMapping("/me")
    fun getUserById(userId: Long): UserResponse? {
        val user = userRepository.getBy(userId)

        return UserResponse(
            id = user.id!!,
            name = user.name,
            birthDate = user.birthDate,
            provider = user.provider
        )
    }

    data class UserResponse(
        val id: Long,
        val name: String,
        val birthDate: LocalDate?,
        val provider: OAuth2Provider
    )
}
