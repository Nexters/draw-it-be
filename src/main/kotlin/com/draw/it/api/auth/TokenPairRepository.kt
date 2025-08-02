package com.draw.it.api.auth

import com.draw.it.api.auth.entity.TokenPair

interface TokenPairRepository {
    fun save(tokenPair: TokenPair): TokenPair
    fun findByAccessTokenAndRefreshToken(accessToken: String, refreshToken: String): TokenPair?
    fun findByAccessToken(accessToken: String): TokenPair?
    fun findByUserId(userId: Long): TokenPair?
    fun deleteByUserId(userId: Long)
    fun flush()
}
