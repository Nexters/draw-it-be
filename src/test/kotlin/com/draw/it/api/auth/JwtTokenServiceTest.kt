package com.draw.it.api.auth

import com.draw.it.api.user.domain.OAuth2Provider
import com.draw.it.common.IntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@IntegrationTest
class JwtTokenServiceTest {

    @Autowired
    private lateinit var tokenService: TokenService

    @Test
    fun `generates valid JWT token for user ID 1`() {
        // given
        val userId = 1L
        val provider = OAuth2Provider.KAKAO

        // when
        val token = tokenService.issue(userId, provider)

        // then
        assertNotNull(token)
        val extractedUserId = tokenService.validateAndGetUserId(token.accessToken)
        assertEquals(userId, extractedUserId)
        println("token = ${token}")
    }
}
