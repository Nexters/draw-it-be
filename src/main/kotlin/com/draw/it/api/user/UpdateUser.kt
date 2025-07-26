package com.draw.it.api.user

import com.draw.it.api.user.domain.UserRepository
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@Transactional
@RestController
@RequestMapping("/user")
class UpdateUser(
    private val userRepository: UserRepository,
) {

    @PutMapping("/me")
    fun updateUser(
        @AuthenticationPrincipal userId: Long,
        @RequestBody request: UpdateUserRequest,
    ) {
        userRepository.updateNameAndBirthDate(userId, name = request.name, birthDate = request.birthDate)
    }

    data class UpdateUserRequest(
        val name: String,
        val birthDate: LocalDate?,
    )
}
