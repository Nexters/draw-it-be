package com.draw.it.api.auth

import com.draw.it.api.user.CreateUser
import com.draw.it.api.user.domain.OAuth2Provider
import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Tag(name = "Auth", description = "OAuth 인증 API")
@RestController
@RequestMapping("/auth")
class AuthenticateOAuth(
    private val facebookAuthClient: FacebookAuthClient,
    private val kakaoAuthClient: KakaoAuthClient,
    private val createUser: CreateUser,
    private val objectMapper: ObjectMapper,
    private val tokenService: TokenService,
) {

    @Operation(summary = "Facebook OAuth 콜백", description = "Facebook OAuth 인증 콜백을 처리합니다")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "302", description = "인증 성공 후 리다이렉트"),
            ApiResponse(responseCode = "400", description = "인증 실패")
        ]
    )
    @GetMapping("/facebook/callback")
    fun handleFacebookCallback(
        @Parameter(description = "Facebook OAuth 코드", required = true) @RequestParam code: String,
    ): RedirectView {
        val accessToken = facebookAuthClient.exchangeCodeForToken(code)
        val userInfo = facebookAuthClient.getUserInfo(accessToken)

        val userId = createUser.getOrCreateUser(
            name = userInfo.name,
            provider = OAuth2Provider.FACEBOOK,
            providerId = userInfo.id
        )
        val jwtToken = tokenService.issue(userId, OAuth2Provider.FACEBOOK)

        return RedirectView("http://localhost:3000?data=${createResponse(jwtToken, userId)}")
    }

    @Operation(summary = "Kakao OAuth 콜백", description = "Kakao OAuth 인증 콜백을 처리합니다")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "인증 성공",
                content = [Content(schema = Schema(implementation = AuthTokenResponse::class))]
            ),
            ApiResponse(responseCode = "400", description = "인증 실패")
        ]
    )
    @GetMapping("/kakao/callback")
    fun handleKakaoCallback(
        @Parameter(description = "Kakao OAuth 코드", required = true) @RequestParam code: String,
    ): AuthTokenResponse {
        val tokenResponse = kakaoAuthClient.exchangeCodeForToken(code)
        val userInfo = kakaoAuthClient.getUserInfo(tokenResponse.accessToken)

        val userId = createUser.getOrCreateUser(
            name = userInfo.name,
            provider = OAuth2Provider.KAKAO,
            providerId = userInfo.id
        )
        val jwtToken = tokenService.issue(userId, OAuth2Provider.KAKAO)

        return AuthTokenResponse(
            token = jwtToken,
            userId = userId
        )
    }

    private fun createResponse(jwtToken: String, userId: Long): String? {
        val data = mapOf(
            "success" to true,
            "token" to jwtToken,
            "userId" to userId,
        )
        val jsonString = objectMapper.writeValueAsString(data)
        val encodedData = URLEncoder.encode(jsonString, StandardCharsets.UTF_8)
        return encodedData
    }

    data class AuthTokenResponse(
        val token: String,
        val userId: Long,
    )
}
