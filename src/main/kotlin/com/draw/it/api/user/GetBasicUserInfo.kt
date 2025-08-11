package com.draw.it.api.user

import com.draw.it.api.user.domain.UserRepository
import org.springframework.stereotype.Service

@Service
class GetBasicUserInfo(
    private val userRepository: UserRepository
) {

    fun getUserName(userId: Long): String? {
        return userRepository.findById(userId)?.name
    }
}
