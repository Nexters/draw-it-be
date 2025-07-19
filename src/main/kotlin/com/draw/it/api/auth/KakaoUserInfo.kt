package com.draw.it.api.auth

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoUserInfo(
    val id: String,
    val name: String
)

data class KakaoAccessTokenResponse(
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("token_type")
    val tokenType: String,
    @JsonProperty("refresh_token")
    val refreshToken: String?,
    @JsonProperty("expires_in")
    val expiresIn: Int,
    val scope: String?,
    @JsonProperty("refresh_token_expires_in")
    val refreshTokenExpiresIn: Int?
)

data class KakaoUserInfoResponse(
    val id: Long,
    @JsonProperty("connected_at")
    val connectedAt: String,
    @JsonProperty("kakao_account")
    val kakaoAccount: KakaoAccount
) {
    data class KakaoAccount(
        val profile: KakaoProfile
    )

    data class KakaoProfile(
        val nickname: String,
    )
}
