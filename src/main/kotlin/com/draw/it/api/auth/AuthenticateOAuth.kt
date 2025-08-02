package com.draw.it.api.auth

import com.draw.it.api.user.CreateUser
import com.draw.it.api.user.domain.OAuth2Provider
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "Auth", description = "OAuth 인증 API")
@RestController
@RequestMapping("/auth")
class AuthenticateOAuth(
    private val facebookAuthClient: FacebookAuthClient,
    private val kakaoAuthClient: KakaoAuthClient,
    private val createUser: CreateUser,
    private val tokenService: TokenService,
) {

    @Operation(summary = "Facebook OAuth 콜백", description = "Facebook OAuth 인증 콜백을 처리합니다")
    @GetMapping("/facebook/callback")
    fun handleFacebookCallback(
        @Parameter(description = "Facebook OAuth 코드", required = true) @RequestParam code: String,
    ): AuthTokenResponse {
        val accessToken = facebookAuthClient.exchangeCodeForToken(code)
        val userInfo = facebookAuthClient.getUserInfo(accessToken)

        val userId = createUser.getOrCreateUser(
            name = userInfo.name,
            provider = OAuth2Provider.FACEBOOK,
            providerId = userInfo.id
        )
        val tokenPair = tokenService.issue(userId, OAuth2Provider.FACEBOOK)

        return AuthTokenResponse(
            accessToken = tokenPair.accessToken,
            refreshToken = tokenPair.refreshToken,
            accessTokenExpiresAt = tokenPair.accessTokenExpiresAt,
            refreshTokenExpiresAt = tokenPair.refreshTokenExpiresAt,
            userId = userId
        )
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
        val tokenPair = tokenService.issue(userId, OAuth2Provider.KAKAO)

        return AuthTokenResponse(
            accessToken = tokenPair.accessToken,
            refreshToken = tokenPair.refreshToken,
            accessTokenExpiresAt = tokenPair.accessTokenExpiresAt,
            refreshTokenExpiresAt = tokenPair.refreshTokenExpiresAt,
            userId = userId
        )
    }

    @Operation(summary = "토큰 갱신", description = "액세스 토큰과 리프레시 토큰을 사용하여 새로운 토큰 쌍을 발급받습니다")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "토큰 갱신 성공",
                content = [Content(schema = Schema(implementation = AuthTokenResponse::class))]
            ),
            ApiResponse(responseCode = "401", description = "유효하지 않은 토큰")
        ]
    )
    @PostMapping("/refresh")
    fun refreshTokens(
        @RequestBody request: RefreshTokenRequest
    ): AuthTokenResponse {
        val tokenPair = tokenService.refreshTokens(request.accessToken, request.refreshToken)
            ?: throw IllegalArgumentException("Invalid token pair")

        val userId = tokenService.validateAndGetUserId(tokenPair.accessToken)
            ?: throw IllegalArgumentException("Invalid access token")

        return AuthTokenResponse(
            accessToken = tokenPair.accessToken,
            refreshToken = tokenPair.refreshToken,
            accessTokenExpiresAt = tokenPair.accessTokenExpiresAt,
            refreshTokenExpiresAt = tokenPair.refreshTokenExpiresAt,
            userId = userId
        )
    }

    data class RefreshTokenRequest(
        val accessToken: String,
        val refreshToken: String
    )

    data class AuthTokenResponse(
        val accessToken: String,
        val refreshToken: String,
        val accessTokenExpiresAt: Long,
        val refreshTokenExpiresAt: Long,
        val userId: Long,
    )
}
