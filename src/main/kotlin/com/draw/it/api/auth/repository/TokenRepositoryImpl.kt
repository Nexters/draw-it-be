package com.draw.it.api.auth.repository

import com.draw.it.api.auth.Token
import org.springframework.stereotype.Repository

@Repository
class TokenRepositoryImpl(
    private val tokenJpaRepository: TokenJpaRepository
) : TokenRepository {

    override fun save(token: Token): Token {
        return tokenJpaRepository.save(token)
    }

    override fun deleteByUser(userId: Long) {
        tokenJpaRepository.deleteByUserId(userId)
    }
}