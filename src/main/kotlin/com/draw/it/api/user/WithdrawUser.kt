package com.draw.it.api.user

import com.draw.it.api.user.domain.UserRepository
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RestController

@Transactional
@RestController
class WithdrawUser(
    private val userRepository: UserRepository
) {

    @DeleteMapping("/user/withdraw")
    fun withdrawUser(@AuthenticationPrincipal userId: Long) {
        userRepository.deleteById(userId)
    }
}