package com.draw.it.api.user

import com.draw.it.api.user.domain.UserRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "User", description = "사용자 관리 API")
@Transactional
@RestController
class WithdrawUser(
    private val userRepository: UserRepository
) {

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 처리합니다")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            ApiResponse(responseCode = "401", description = "인증 실패")
        ]
    )
    @DeleteMapping("/user/withdraw")
    fun withdrawUser(@AuthenticationPrincipal userId: Long) {
        userRepository.deleteById(userId)
    }
}