package com.draw.it.api.user

import com.draw.it.api.user.domain.OAuth2Provider
import com.draw.it.common.IntegrationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import java.time.LocalDate

@IntegrationTest
@Sql("/user.sql")
class UpdateUserTest {
    @Autowired
    private lateinit var sut: UpdateUser

    @Autowired
    private lateinit var createUser: CreateUser

    @Autowired
    private lateinit var getUser: GetUser

    @Test
    fun `사용자 이름과 생년월일을 수정한다`() {
        val userId = createUser.getOrCreateUser(
            name = "홍길동",
            provider = OAuth2Provider.KAKAO,
            providerId = "12345"
        )

        val request = UpdateUser.UpdateUserRequest(
            name = "김철수",
            birthDate = LocalDate.of(1990, 5, 15)
        )

        sut.updateUser(userId, request)

        val updatedUser = getUser.getUserById(userId)
        assertEquals("김철수", updatedUser?.name)
        assertEquals(LocalDate.of(1990, 5, 15), updatedUser?.birthDate)
    }

    @Test
    fun `생년월일을 null로 수정한다`() {
        val userId = createUser.getOrCreateUser(
            name = "홍길동",
            provider = OAuth2Provider.KAKAO,
            providerId = "12345"
        )

        val request = UpdateUser.UpdateUserRequest(
            name = "홍길동",
            birthDate = null
        )

        sut.updateUser(userId, request)

        val updatedUser = getUser.getUserById(userId)
        assertEquals("홍길동", updatedUser?.name)
        assertEquals(null, updatedUser?.birthDate)
    }
}