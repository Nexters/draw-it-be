package com.draw.it.api.auth

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface TokenJpaRepository : JpaRepository<Token, Long> {
    fun deleteByAccessToken(accessToken: String)
    fun deleteByUserId(userId: Long)
    fun findByAccessToken(accessToken: String): Token?
}

@Repository
class TokenRepositoryImpl(
    private val tokenJpaRepository: TokenJpaRepository
) : TokenRepository {

    override fun save(token: Token): Token {
        return tokenJpaRepository.save(token)
    }

    override fun deleteByAccessToken(accessToken: String) {
        tokenJpaRepository.deleteByAccessToken(accessToken)
    }

    override fun deleteByUserId(userId: Long) {
        tokenJpaRepository.deleteByUserId(userId)
    }

    override fun findByAccessToken(accessToken: String): Token? {
        return tokenJpaRepository.findByAccessToken(accessToken)
    }
}