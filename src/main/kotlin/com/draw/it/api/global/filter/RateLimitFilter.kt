package com.draw.it.api.global.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.benmanes.caffeine.cache.Cache
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Duration

@Component
class RateLimitFilter(
    private val cache: Cache<String, Bucket>,
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {
    private val log = KotlinLogging.logger {}

    companion object {
        private const val REQUESTS_PER_SECOND = 100L
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val clientIp = getClientIpAddress(request)
        val bucket = resolveBucket(clientIp)

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response)
        } else {
            log.error { "Exceeded bucket client ip: $clientIp" }
            sendRateLimitResponse(response)
        }
    }

    private fun resolveBucket(key: String): Bucket {
        return cache.get(key) { createNewBucket() }
    }

    private fun createNewBucket(): Bucket {
        val limit = Bandwidth.builder()
            .capacity(REQUESTS_PER_SECOND)
            .refillIntervally(REQUESTS_PER_SECOND, Duration.ofSeconds(1))
            .build()
        return Bucket.builder()
            .addLimit(limit)
            .build()
    }

    private fun getClientIpAddress(request: HttpServletRequest): String {
        val xForwardedForHeader = request.getHeader("X-Forwarded-For")
        if (!xForwardedForHeader.isNullOrBlank()) {
            return xForwardedForHeader.split(",")[0].trim()
        }

        val xRealIpHeader = request.getHeader("X-Real-IP")
        if (!xRealIpHeader.isNullOrBlank()) {
            return xRealIpHeader.trim()
        }

        return request.remoteAddr
    }

    private fun sendRateLimitResponse(response: HttpServletResponse) {
        response.status = HttpStatus.TOO_MANY_REQUESTS.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE

        val errorResponse = mapOf(
            "error" to "Rate limit exceeded",
            "message" to "Too many requests from this IP address. Please try again later."
        )

        response.writer.write(objectMapper.writeValueAsString(errorResponse))
        response.writer.flush()
    }
}
