package com.draw.it.api.user

import com.draw.it.api.user.domain.OAuth2Provider
import com.draw.it.common.IntegrationTest
import org.approvaltests.Approvals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals

@IntegrationTest
@Sql("/user.sql")
class CreateUserTest {
    @Autowired
    private lateinit var createUser: CreateUser
    @Autowired
    private lateinit var getUser: ReadUser

    @Test
    fun `새로운 사용자를 생성하고 저장한다`() {
        val createdUserId = createUser.getOrCreateUser(
            name = "홍길동",
            provider = OAuth2Provider.KAKAO,
            providerId = "12345"
        )

        val savedUser = getUser.getUserById(createdUserId)
        Approvals.verify(savedUser)
    }

    @Test
    fun `기존 사용자가 있으면 기존 사용자 ID를 반환한다`() {
        // given
        val name = "홍길동"
        val provider = OAuth2Provider.KAKAO
        val providerId = "12345"

        // when
        val firstResult = createUser.getOrCreateUser(name, provider, providerId)
        val secondResult = createUser.getOrCreateUser(name, provider, providerId)

        // then
        assertEquals(firstResult, secondResult)
    }
}