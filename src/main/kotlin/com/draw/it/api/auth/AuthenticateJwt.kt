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
class AuthenticateJwt(
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
        if (isPublicPath(request.requestURI)) {
            filterChain.doFilter(request, response)
            return
        }

        val authorizationHeader = request.getHeader(AUTHORIZATION_HEADER)

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header is missing or invalid")
            return
        }

        val token = authorizationHeader.substring(TOKEN_PREFIX_LENGTH)
        val userId = jwtTokenService.validateAndGetUserId(token)

        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is invalid or expired")
            return
        }

        if (!isAlreadyAuthenticated()) {
            val authentication = createAuthentication(userId, request)
            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }

    private fun isPublicPath(uri: String): Boolean {
        val publicPaths = listOf("/auth/", "/anonymous/", "/api-docs/", "/docs/", "/swagger-ui/", "/health")
        return publicPaths.any { uri.startsWith(it) }
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