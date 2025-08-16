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
            projectUuid = "test-project-uuid",
            nickname = "테스트 닉네임",
            letter = "테스트 편지",
            imageUrl = "https://example.com/image.jpg"
        )
        val savedDoodle = doodleRepository.save(doodle)

        // When: 프로젝트 소유자(userId=1)가 두들을 삭제
        deleteDoodle.deleteDoodles(
            projectId = 1L,
            request = DeleteDoodlesRequest(listOf(savedDoodle.id!!)),
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
            projectUuid = "test-project-uuid",
            nickname = "테스트 닉네임",
            letter = "테스트 편지",
            imageUrl = "https://example.com/image.jpg"
        )
        val savedDoodle = doodleRepository.save(doodle)

        // When & Then: 다른 사용자(userId=999)가 두들 삭제 시도시 예외 발생
        assertThatThrownBy {
            deleteDoodle.deleteDoodles(
                projectId = 1L,
                request = DeleteDoodlesRequest(listOf(savedDoodle.id!!)),
                userId = 999L
            )
        }.isInstanceOf(BizException::class.java)
            .hasMessageContaining("프로젝트 소유자만 두들을 삭제할 수 있습니다")
    }

    @Test
    fun `존재하지 않는 프로젝트로 두들 삭제시 예외가 발생한다`() {
        // Given: 두들을 먼저 생성
        val doodle = com.draw.it.api.doodle.domain.Doodle(
            projectId = 1L,
            projectUuid = "test-project-uuid",
            nickname = "테스트 닉네임",
            letter = "테스트 편지",
            imageUrl = "https://example.com/image.jpg"
        )
        val savedDoodle = doodleRepository.save(doodle)

        // When & Then: 존재하지 않는 프로젝트로 삭제 시도시 예외 발생
        assertThatThrownBy {
            deleteDoodle.deleteDoodles(
                projectId = 999L,
                request = DeleteDoodlesRequest(listOf(savedDoodle.id!!)),
                userId = 1L
            )
        }.isInstanceOf(BizException::class.java)
            .hasMessageContaining("존재하지 않는 프로젝트입니다")
    }

    @Test
    fun `존재하지 않는 두들 삭제시 예외가 발생한다`() {
        // When & Then: 존재하지 않는 두들 삭제 시도시 예외 발생
        assertThatThrownBy {
            deleteDoodle.deleteDoodles(
                projectId = 1L,
                request = DeleteDoodlesRequest(listOf(999L)),
                userId = 1L
            )
        }.isInstanceOf(BizException::class.java)
            .hasMessageContaining("존재하지 않는 두들입니다")
    }

    @Test
    fun `다른 프로젝트의 두들 삭제시 예외가 발생한다`() {
        // Given: 두들을 먼저 생성
        val doodle = com.draw.it.api.doodle.domain.Doodle(
            projectId = 1L,
            projectUuid = "test-project-uuid",
            nickname = "테스트 닉네임",
            letter = "테스트 편지",
            imageUrl = "https://example.com/image.jpg"
        )
        val savedDoodle = doodleRepository.save(doodle)

        // When & Then: 다른 프로젝트 ID로 삭제 시도시 예외 발생 (실제로는 프로젝트 존재하지 않음)
        assertThatThrownBy {
            deleteDoodle.deleteDoodles(
                projectId = 2L,
                request = DeleteDoodlesRequest(listOf(savedDoodle.id!!)),
                userId = 1L
            )
        }.isInstanceOf(BizException::class.java)
            .hasMessageContaining("존재하지 않는 프로젝트입니다")
    }

    @Test
    fun `여러 두들을 한번에 삭제한다`() {
        // Given: 여러 두들을 생성
        val doodle1 = com.draw.it.api.doodle.domain.Doodle(
            projectId = 1L,
            projectUuid = "test-project-uuid",
            nickname = "테스트 닉네임1",
            letter = "테스트 편지1",
            imageUrl = "https://example.com/image1.jpg"
        )
        val doodle2 = com.draw.it.api.doodle.domain.Doodle(
            projectId = 1L,
            projectUuid = "test-project-uuid",
            nickname = "테스트 닉네임2",
            letter = "테스트 편지2",
            imageUrl = "https://example.com/image2.jpg"
        )
        val savedDoodle1 = doodleRepository.save(doodle1)
        val savedDoodle2 = doodleRepository.save(doodle2)

        // When: 여러 두들을 한번에 삭제
        deleteDoodle.deleteDoodles(
            projectId = 1L,
            request = DeleteDoodlesRequest(listOf(savedDoodle1.id!!, savedDoodle2.id!!)),
            userId = 1L
        )

        // Then: 모든 두들이 소프트 삭제됨
        val deletedDoodle1 = doodleRepository.findById(savedDoodle1.id!!)
        val deletedDoodle2 = doodleRepository.findById(savedDoodle2.id!!)
        assertThat(deletedDoodle1).isNull()
        assertThat(deletedDoodle2).isNull()
    }
}