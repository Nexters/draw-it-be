package com.draw.it.api.user

import com.draw.it.api.user.domain.UserRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@Tag(name = "User", description = "사용자 관리 API")
@Transactional
@RestController
@RequestMapping("/user")
class UpdateUser(
    private val userRepository: UserRepository,
) {

    @Operation(summary = "내 정보 수정", description = "사용자 자신의 정보를 수정합니다")
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
