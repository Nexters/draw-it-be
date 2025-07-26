package com.draw.it.api.doodle

import com.draw.it.api.common.exception.BizException
import com.draw.it.api.doodle.domain.DoodleRepository
import com.draw.it.common.IntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

@IntegrationTest
@Sql("/doodle.sql")
class DeleteDoodleTest {
    @Autowired
    private lateinit var deleteDoodle: DeleteDoodle

    @Autowired
    private lateinit var doodleRepository: DoodleRepository

    @Test
    fun `프로젝트 소유자가 두들을 삭제한다`() {
        // Given: 두들을 먼저 생성
        val doodle = com.draw.it.api.doodle.domain.Doodle(
            projectId = 1L,
            nickname = "테스트 닉네임",
            letter = "테스트 편지",
            imageUrl = "https://example.com/image.jpg"
        )
        val savedDoodle = doodleRepository.save(doodle)

        // When: 프로젝트 소유자(userId=1)가 두들을 삭제
        deleteDoodle.deleteDoodle(
            projectId = 1L,
            doodleId = savedDoodle.id!!,
            userId = 1L
        )

        // Then: 두들이 소프트 삭제됨 (조회되지 않음)
        val deletedDoodle = doodleRepository.findById(savedDoodle.id!!)
        assertThat(deletedDoodle).isNull()
    }

    @Test
    fun `프로젝트 소유자가 아닌 사용자가 두들 삭제시 예외가 발생한다`() {
        // Given: 두들을 먼저 생성
        val doodle = com.draw.it.api.doodle.domain.Doodle(
            projectId = 1L,
            nickname = "테스트 닉네임",
            letter = "테스트 편지",
            imageUrl = "https://example.com/image.jpg"
        )
        val savedDoodle = doodleRepository.save(doodle)

        // When & Then: 다른 사용자(userId=999)가 두들 삭제 시도시 예외 발생
        assertThatThrownBy {
            deleteDoodle.deleteDoodle(
                projectId = 1L,
                doodleId = savedDoodle.id!!,
                userId = 999L
            )
        }.isInstanceOf(BizException::class.java)
            .hasMessageContaining("권한이 없는 사용자입니다")
    }

    @Test
    fun `존재하지 않는 프로젝트로 두들 삭제시 예외가 발생한다`() {
        // Given: 두들을 먼저 생성
        val doodle = com.draw.it.api.doodle.domain.Doodle(
            projectId = 1L,
            nickname = "테스트 닉네임",
            letter = "테스트 편지",
            imageUrl = "https://example.com/image.jpg"
        )
        val savedDoodle = doodleRepository.save(doodle)

        // When & Then: 존재하지 않는 프로젝트로 삭제 시도시 예외 발생
        assertThatThrownBy {
            deleteDoodle.deleteDoodle(
                projectId = 999L,
                doodleId = savedDoodle.id!!,
                userId = 1L
            )
        }.isInstanceOf(BizException::class.java)
            .hasMessageContaining("잘못된 요청입니다")
    }

    @Test
    fun `존재하지 않는 두들 삭제시 예외가 발생한다`() {
        // When & Then: 존재하지 않는 두들 삭제 시도시 예외 발생
        assertThatThrownBy {
            deleteDoodle.deleteDoodle(
                projectId = 1L,
                doodleId = 999L,
                userId = 1L
            )
        }.isInstanceOf(BizException::class.java)
            .hasMessageContaining("잘못된 요청입니다")
    }

    @Test
    fun `다른 프로젝트의 두들 삭제시 예외가 발생한다`() {
        // Given: 두들을 먼저 생성
        val doodle = com.draw.it.api.doodle.domain.Doodle(
            projectId = 1L,
            nickname = "테스트 닉네임",
            letter = "테스트 편지",
            imageUrl = "https://example.com/image.jpg"
        )
        val savedDoodle = doodleRepository.save(doodle)

        // When & Then: 다른 프로젝트 ID로 삭제 시도시 예외 발생 (실제로는 프로젝트 존재하지 않음)
        assertThatThrownBy {
            deleteDoodle.deleteDoodle(
                projectId = 2L,
                doodleId = savedDoodle.id!!,
                userId = 1L
            )
        }.isInstanceOf(BizException::class.java)
            .hasMessageContaining("잘못된 요청입니다")
    }
}