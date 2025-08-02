package com.draw.it.api.auth

import com.draw.it.api.user.domain.OAuth2Provider

data class TokenPairDto(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresAt: Long,
    val refreshTokenExpiresAt: Long
)

interface TokenService {
    fun issue(userId: Long, provider: OAuth2Provider): TokenPairDto
    fun validateAndGetUserId(accessToken: String): Long?
    fun refreshTokens(accessToken: String, refreshToken: String): TokenPairDto?
}