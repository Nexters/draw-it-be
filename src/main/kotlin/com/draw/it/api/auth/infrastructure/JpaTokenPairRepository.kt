package com.draw.it.api.auth.infrastructure

import com.draw.it.api.auth.TokenPairRepository
import com.draw.it.api.auth.entity.TokenPair
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface TokenPairJpaRepository : JpaRepository<TokenPair, Long> {
    fun findByAccessTokenAndRefreshToken(accessToken: String, refreshToken: String): TokenPair?
    fun findByAccessToken(accessToken: String): TokenPair?
    fun findByUserId(userId: Long): TokenPair?
    fun deleteByUserId(userId: Long)
}

@Repository
class JpaTokenPairRepositoryAdapter(
    private val jpaRepository: TokenPairJpaRepository
) : TokenPairRepository {

    override fun save(tokenPair: TokenPair): TokenPair {
        return jpaRepository.save(tokenPair)
    }

    override fun findByAccessTokenAndRefreshToken(accessToken: String, refreshToken: String): TokenPair? {
        return jpaRepository.findByAccessTokenAndRefreshToken(accessToken, refreshToken)
    }

    override fun findByAccessToken(accessToken: String): TokenPair? {
        return jpaRepository.findByAccessToken(accessToken)
    }

    override fun findByUserId(userId: Long): TokenPair? {
        return jpaRepository.findByUserId(userId)
    }

    override fun deleteByUserId(userId: Long) {
        jpaRepository.deleteByUserId(userId)
    }

    override fun flush() {
        jpaRepository.flush()
    }
}
