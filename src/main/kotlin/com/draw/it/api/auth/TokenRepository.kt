package com.draw.it.api.auth

interface TokenRepository {
    fun save(token: Token): Token
    fun deleteByAccessToken(accessToken: String)
    fun deleteByUserId(userId: Long)
    fun findByAccessToken(accessToken: String): Token?
}