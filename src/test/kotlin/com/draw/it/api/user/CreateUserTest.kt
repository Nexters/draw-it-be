package com.draw.it.api.user

import com.draw.it.api.user.domain.OAuth2Provider
import com.draw.it.api.user.domain.User
import com.draw.it.api.user.domain.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class CreateUserTest {

    private val userRepository: UserRepository = mockk()
    private val createUser = CreateUser(userRepository)

    @Test
    fun `기존 사용자가 있으면 기존 사용자 ID를 반환한다`() {
        // given
        val name = "홍길동"
        val provider = OAuth2Provider.KAKAO
        val providerId = "12345"
        val existingUser = User(
            id = 1L,
            name = name,
            provider = provider,
            providerId = providerId
        )

        every { userRepository.findByProviderAndProviderId(provider, providerId) } returns existingUser

        // when
        val result = createUser.getOrCreateUser(name, provider, providerId)

        // then
        assertEquals(1L, result)
        verify { userRepository.findByProviderAndProviderId(provider, providerId) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `새로운 사용자를 생성하고 저장한다`() {
        // given
        val name = "홍길동"
        val provider = OAuth2Provider.KAKAO
        val providerId = "12345"
        User(
            name = name,
            provider = provider,
            providerId = providerId
        )
        val savedUser = User(
            id = 2L,
            name = name,
            provider = provider,
            providerId = providerId
        )

        every { userRepository.findByProviderAndProviderId(provider, providerId) } returns null
        every { userRepository.save(any()) } returns savedUser

        // when
        val result = createUser.getOrCreateUser(name, provider, providerId)

        // then
        assertEquals(2L, result)
        verify { userRepository.findByProviderAndProviderId(provider, providerId) }
        verify { userRepository.save(any()) }
    }

    @Test
    fun `기존 사용자 ID가 null이면 예외가 발생한다`() {
        // given
        val name = "홍길동"
        val provider = OAuth2Provider.KAKAO
        val providerId = "12345"
        val existingUser = User(
            id = null,
            name = name,
            provider = provider,
            providerId = providerId
        )

        every { userRepository.findByProviderAndProviderId(provider, providerId) } returns existingUser

        // when & then
        assertThrows<NullPointerException> {
            createUser.getOrCreateUser(name, provider, providerId)
        }
    }

    @Test
    fun `새로 생성된 사용자 ID가 null이면 예외가 발생한다`() {
        // given
        val name = "홍길동"
        val provider = OAuth2Provider.KAKAO
        val providerId = "12345"
        val savedUser = User(
            id = null,
            name = name,
            provider = provider,
            providerId = providerId
        )

        every { userRepository.findByProviderAndProviderId(provider, providerId) } returns null
        every { userRepository.save(any()) } returns savedUser

        // when & then
        assertThrows<NullPointerException> {
            createUser.getOrCreateUser(name, provider, providerId)
        }
    }
}