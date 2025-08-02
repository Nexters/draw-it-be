package com.draw.it.api.user

import com.draw.it.api.user.domain.OAuth2Provider
import com.draw.it.api.user.domain.UserRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@Tag(name = "User", description = "사용자 관리 API")
@RestController
@RequestMapping("/user")
class GetUser(
    private val userRepository: UserRepository
) {

    @Operation(summary = "내 정보 조회", description = "사용자 자신의 정보를 조회합니다")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "사용자 정보 조회 성공",
                content = [Content(schema = Schema(implementation = UserResponse::class))]
            ),
            ApiResponse(responseCode = "401", description = "인증 실패")
        ]
    )
    @GetMapping("/me")
    fun getUser(@AuthenticationPrincipal userId: Long): UserResponse? {
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
