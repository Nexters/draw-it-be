package com.draw.it.api.auth

import com.draw.it.api.auth.entity.TokenPair
import com.draw.it.api.user.domain.OAuth2Provider
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
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
    @Value("\${jwt.expiration.refresh:604800}")
    private val refreshTokenExpiration: Long,
    private val tokenPairRepository: TokenPairRepository,
) : TokenService {

    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(secretKey.toByteArray())
    }

    @Transactional
    override fun issue(userId: Long, provider: OAuth2Provider): TokenPairDto {
        val accessToken = createAccessToken(userId, provider)
        val refreshToken = createRefreshToken(userId, provider)
        
        val accessTokenExpiresAt = Instant.now().plus(accessTokenExpiration, ChronoUnit.SECONDS)
        val refreshTokenExpiresAt = Instant.now().plus(refreshTokenExpiration, ChronoUnit.SECONDS)
        
        val tokenPair = TokenPair(
            userId = userId,
            accessToken = accessToken,
            refreshToken = refreshToken,
            accessTokenExpiresAt = LocalDateTime.ofInstant(accessTokenExpiresAt, ZoneId.systemDefault()),
            refreshTokenExpiresAt = LocalDateTime.ofInstant(refreshTokenExpiresAt, ZoneId.systemDefault())
        )
        
        tokenPairRepository.deleteByUserId(userId)
        tokenPairRepository.save(tokenPair)
        
        return TokenPairDto(
            accessToken = accessToken,
            refreshToken = refreshToken,
            accessTokenExpiresAt = accessTokenExpiresAt.epochSecond,
            refreshTokenExpiresAt = refreshTokenExpiresAt.epochSecond
        )
    }

    override fun validateAndGetUserId(accessToken: String): Long? {
        if (!validateToken(accessToken)) return null
        if (isTokenExpired(accessToken)) return null
        return getUserIdFromToken(accessToken)
    }

    override fun refreshTokens(accessToken: String, refreshToken: String): TokenPairDto? {
        val tokenPair = tokenPairRepository.findByAccessTokenAndRefreshToken(accessToken, refreshToken)
            ?: return null
            
        if (tokenPair.refreshTokenExpiresAt.isBefore(LocalDateTime.now())) {
            return null
        }

        val userIdFromToken = getUserIdFromToken(accessToken) ?: return null
        val providerFromToken = getProviderFromToken(accessToken) ?: return null
        
        return issue(userIdFromToken, providerFromToken)
    }

    private fun createAccessToken(userId: Long, provider: OAuth2Provider): String {
        val now = Instant.now()
        val expiration = now.plus(accessTokenExpiration, ChronoUnit.SECONDS)

        return Jwts.builder()
            .subject(userId.toString())
            .claim("provider", provider.name)
            .claim("type", "access")
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .signWith(key)
            .compact()
    }
    
    private fun createRefreshToken(userId: Long, provider: OAuth2Provider): String {
        val now = Instant.now()
        val expiration = now.plus(refreshTokenExpiration, ChronoUnit.SECONDS)

        return Jwts.builder()
            .subject(userId.toString())
            .claim("provider", provider.name)
            .claim("type", "refresh")
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
