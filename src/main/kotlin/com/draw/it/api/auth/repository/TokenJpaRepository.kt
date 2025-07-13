package com.draw.it.api.auth.repository

import com.draw.it.api.auth.Token
import org.springframework.data.jpa.repository.JpaRepository

interface TokenJpaRepository : JpaRepository<Token, Long> {
    fun deleteByUserId(userId: Long)
    fun findByAccessToken(accessToken: String): Token?
}