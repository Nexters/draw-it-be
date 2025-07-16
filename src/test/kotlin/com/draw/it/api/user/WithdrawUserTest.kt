package com.draw.it.api.user

import com.draw.it.common.IntegrationTest
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
    }
}