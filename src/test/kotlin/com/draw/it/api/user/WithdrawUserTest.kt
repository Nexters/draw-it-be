package com.draw.it.api.user

import com.draw.it.api.user.domain.OAuth2Provider
import com.draw.it.common.IntegrationTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

@IntegrationTest
@Sql("/user.sql")
class WithdrawUserTest {
    @Autowired
    private lateinit var sut: WithdrawUser

    @Autowired
    private lateinit var createUser: CreateUser

    @Autowired
    private lateinit var getUser: GetUser

    @Test
    fun `회원을 탈퇴한다`() {
        val userId = createUser.getOrCreateUser(
            name = "홍길동",
            provider = OAuth2Provider.KAKAO,
            providerId = "12345"
        )

        sut.withdrawUser(userId)

        Assertions.assertThrows(NoSuchElementException::class.java) {
            getUser.getUser(userId)
        }
    }
}