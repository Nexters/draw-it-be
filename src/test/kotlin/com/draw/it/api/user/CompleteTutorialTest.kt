package com.draw.it.api.user

import com.draw.it.api.user.domain.OAuth2Provider
import com.draw.it.common.IntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@IntegrationTest
@Sql("/user.sql")
class CompleteTutorialTest {
    @Autowired
    private lateinit var createUser: CreateUser

    @Autowired
    private lateinit var getUser: GetUser

    @Autowired
    private lateinit var completeTutorial: CompleteTutorial

    @Test
    fun `튜토리얼을 완료하면 needsTutorial이 false로 변경된다`() {
        // given
        val userId = createUser.getOrCreateUser(
            name = "홍길동",
            provider = OAuth2Provider.KAKAO,
            providerId = "12345"
        )

        val userBeforeComplete = getUser.getUser(userId)
        assertTrue(userBeforeComplete!!.needsTutorial)

        // when
        completeTutorial.completeTutorial(userId)

        // then
        val userAfterComplete = getUser.getUser(userId)
        assertFalse(userAfterComplete!!.needsTutorial)
    }

    @Test
    fun `이미 튜토리얼을 완료한 사용자도 다시 완료 처리할 수 있다`() {
        // given
        val userId = createUser.getOrCreateUser(
            name = "홍길동",
            provider = OAuth2Provider.KAKAO,
            providerId = "12345"
        )

        completeTutorial.completeTutorial(userId)
        val userAfterFirstComplete = getUser.getUser(userId)
        assertFalse(userAfterFirstComplete!!.needsTutorial)

        // when
        completeTutorial.completeTutorial(userId)

        // then
        val userAfterSecondComplete = getUser.getUser(userId)
        assertFalse(userAfterSecondComplete!!.needsTutorial)
    }
}