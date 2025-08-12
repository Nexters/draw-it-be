package com.draw.it.api.user

import com.draw.it.api.user.domain.UserRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "User", description = "사용자 관리 API")
@RestController
@RequestMapping("/user")
class CompleteTutorial(
    private val userRepository: UserRepository
) {

    @Operation(summary = "튜토리얼 완료 처리", description = "사용자의 튜토리얼을 완료 처리합니다")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "튜토리얼 완료 처리 성공"),
            ApiResponse(responseCode = "401", description = "인증 실패")
        ]
    )
    @PostMapping("/complete-tutorial")
    fun completeTutorial(@AuthenticationPrincipal userId: Long) {
        val user = userRepository.getBy(userId)
        user.completeTutorial()
        userRepository.save(user)
    }
}