package com.draw.it.api.auth.repository

import com.draw.it.api.auth.Token

interface TokenRepository {
    fun save(token: Token): Token
    fun deleteByUser(userId: Long)
}