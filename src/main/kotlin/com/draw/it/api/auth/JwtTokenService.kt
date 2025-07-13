package com.draw.it.api.auth

import com.draw.it.api.user.OAuth2Provider
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.SecretKey

@Service
@Transactional
class JwtTokenService(
    @Value("\${jwt.secret:drawit-secret-key-for-jwt-token-generation-and-validation}")
    private val secretKey: String,
    @Value("\${jwt.expiration.access:86400}")
    private val accessTokenExpiration: Long,
    private val tokenRepository: TokenRepository
) : TokenService {

    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(secretKey.toByteArray())
    }

    override fun issueToken(userId: Long, provider: OAuth2Provider): String {
        val accessToken = createAccessToken(userId, provider)

        val token = Token(
            userId = userId,
            accessToken = accessToken
        )

        tokenRepository.save(token)

        return accessToken
    }

    override fun validateAndGetUserId(accessToken: String): Long? {
        if (!validateToken(accessToken)) {
            return null
        }

        if (isTokenExpired(accessToken)) {
            return null
        }

        return getUserIdFromToken(accessToken)
    }

    override fun revokeToken(accessToken: String) {
        tokenRepository.deleteByAccessToken(accessToken)
    }

    override fun revokeAllUserTokens(userId: Long) {
        tokenRepository.deleteByUserId(userId)
    }

    private fun createAccessToken(userId: Long, provider: OAuth2Provider): String {
        val now = Instant.now()
        val expiration = now.plus(accessTokenExpiration, ChronoUnit.SECONDS)

        return Jwts.builder()
            .subject(userId.toString())
            .claim("provider", provider.name)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .signWith(key)
            .compact()
    }

    private fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getUserIdFromToken(token: String): Long? {
        return try {
            val claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
            claims.subject.toLong()
        } catch (e: Exception) {
            null
        }
    }

    private fun getProviderFromToken(token: String): OAuth2Provider? {
        return try {
            val claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
            OAuth2Provider.valueOf(claims["provider"] as String)
        } catch (e: Exception) {
            null
        }
    }

    private fun isTokenExpired(token: String): Boolean {
        return try {
            val claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
            claims.expiration.before(Date())
        } catch (e: Exception) {
            true
        }
    }
}