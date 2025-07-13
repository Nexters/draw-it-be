package com.draw.it.api.auth.handler

import com.draw.it.api.auth.service.AuthService
import com.draw.it.api.user.OAuth2Provider
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class OAuth2SuccessHandler(
    private val authService: AuthService
) : SimpleUrlAuthenticationSuccessHandler() {

    private val DEFAULT_NAME = "Artist"

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val oAuth2User = authentication.principal as OAuth2User

        try {
            val providerId = oAuth2User.getAttribute<String>("id") ?: throw RuntimeException("Provider ID not found")
            val name = oAuth2User.getAttribute<String>("name") ?: DEFAULT_NAME
            val accessToken = authService.processOAuth2Login(providerId, name, OAuth2Provider.META)
            val redirectUrl = "/auth/login/success?token=${URLEncoder.encode(accessToken, StandardCharsets.UTF_8)}"
            redirectStrategy.sendRedirect(request, response, redirectUrl)
        } catch (e: Exception) {
            val errorMessage = URLEncoder.encode(e.message ?: "로그인 처리 중 오류가 발생했습니다.", StandardCharsets.UTF_8)
            redirectStrategy.sendRedirect(request, response, "/auth/login/failure?error=$errorMessage")
        }
    }
}