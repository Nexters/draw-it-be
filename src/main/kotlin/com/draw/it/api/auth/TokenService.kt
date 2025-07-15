package com.draw.it.api.auth

import com.draw.it.api.user.domain.OAuth2Provider

interface TokenService {
    fun issue(userId: Long, provider: OAuth2Provider): String
    fun validateAndGetUserId(accessToken: String): Long?
}