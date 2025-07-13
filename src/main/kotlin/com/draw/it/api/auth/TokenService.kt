package com.draw.it.api.auth

import com.draw.it.api.user.OAuth2Provider

interface TokenService {
    fun issueToken(userId: Long, provider: OAuth2Provider): String
    fun validateAndGetUserId(accessToken: String): Long?
    fun revokeToken(accessToken: String)
    fun revokeAllUserTokens(userId: Long)
}