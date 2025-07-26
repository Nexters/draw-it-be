package com.draw.it.api.doodle

import com.draw.it.api.common.exception.BizException
import com.draw.it.common.IntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.jdbc.Sql

@IntegrationTest
@Sql("/doodle.sql")
class CreateDoodleTest {
    @Autowired
    private lateinit var createDoodle: CreateDoodle

    @Test
    fun `새로운 두들을 생성하고 저장한다`() {
        val mockImage = MockMultipartFile(
            "image",
            "test-image.jpg",
            "image/jpeg",
            "test image content".toByteArray()
        )

        val response = createDoodle.createDoodle(
            projectId = 1L,
            nickname = "테스트 닉네임",
            letter = "테스트 편지 내용",
            image = mockImage
        )

        assertThat(response.id).isEqualTo(1L)
        assertThat(response.projectId).isEqualTo(1L)
        assertThat(response.nickname).isEqualTo("테스트 닉네임")
        assertThat(response.letter).isEqualTo("테스트 편지 내용")
        assertThat(response.imageUrl).isEqualTo("https://example.com/mock-image-url/test-image.jpg")
        assertThat(response.isNewDoodleConfirmed).isFalse()
    }

    @Test
    fun `편지 내용 없이 두들을 생성한다`() {
        val mockImage = MockMultipartFile(
            "image", 
            "test-image.jpg",
            "image/jpeg", 
            "test image content".toByteArray()
        )

        val response = createDoodle.createDoodle(
            projectId = 1L,
            nickname = "테스트 닉네임",
            letter = null,
            image = mockImage
        )

        assertThat(response.id).isEqualTo(1L)
        assertThat(response.projectId).isEqualTo(1L)
        assertThat(response.nickname).isEqualTo("테스트 닉네임")
        assertThat(response.letter).isNull()
        assertThat(response.imageUrl).isEqualTo("https://example.com/mock-image-url/test-image.jpg")
        assertThat(response.isNewDoodleConfirmed).isFalse()
    }

    @Test
    fun `존재하지 않는 프로젝트로 두들 생성시 예외가 발생한다`() {
        val mockImage = MockMultipartFile(
            "image",
            "test-image.jpg", 
            "image/jpeg",
            "test image content".toByteArray()
        )

        assertThatThrownBy {
            createDoodle.createDoodle(
                projectId = 999L,
                nickname = "테스트 닉네임",
                letter = "테스트 편지 내용",
                image = mockImage
            )
        }.isInstanceOf(BizException::class.java)
            .hasMessageContaining("존재하지 않는 프로젝트입니다")
    }
}
