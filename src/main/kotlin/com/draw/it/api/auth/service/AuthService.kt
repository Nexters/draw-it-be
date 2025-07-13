package com.draw.it.api.auth.service

import com.draw.it.api.auth.Token
import com.draw.it.api.auth.repository.TokenRepository
import com.draw.it.api.user.OAuth2Provider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    private val createUserService: CreateUserService,
    private val tokenRepository: TokenRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {


    fun processOAuth2Login(providerId: String, name: String, provider: OAuth2Provider): String {
        val userId = createUserService.getOrCreate(provider, providerId, name)
        val accessToken = jwtTokenProvider.generateAccessToken(userId)
        val token = Token(userId = userId, accessToken = accessToken)

        tokenRepository.deleteByUser(userId)
        tokenRepository.save(token)
        return accessToken
    }

}