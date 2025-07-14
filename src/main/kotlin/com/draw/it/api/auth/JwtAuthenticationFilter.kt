package com.draw.it.api.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenService: JwtTokenService
) : OncePerRequestFilter() {

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
        private const val TOKEN_PREFIX_LENGTH = 7
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationHeader = request.getHeader(AUTHORIZATION_HEADER)

        authorizationHeader?.let { header ->
            if (header.startsWith(BEARER_PREFIX)) {
                val token = header.substring(TOKEN_PREFIX_LENGTH)
                authenticateUser(token, request)
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun authenticateUser(token: String, request: HttpServletRequest) {
        val userId = jwtTokenService.validateAndGetUserId(token)

        if (userId != null && !isAlreadyAuthenticated()) {
            val authentication = createAuthentication(userId, request)
            SecurityContextHolder.getContext().authentication = authentication
        }
    }

    private fun isAlreadyAuthenticated(): Boolean {
        return SecurityContextHolder.getContext().authentication != null
    }

    private fun createAuthentication(userId: Long, request: HttpServletRequest): UsernamePasswordAuthenticationToken {
        return UsernamePasswordAuthenticationToken(
            userId,
            null,
            emptyList()
        ).apply {
            details = WebAuthenticationDetailsSource().buildDetails(request)
        }
    }
}