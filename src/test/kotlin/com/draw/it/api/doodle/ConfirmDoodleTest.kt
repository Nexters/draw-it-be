package com.draw.it.api.doodle

import com.draw.it.api.common.exception.BizException
import com.draw.it.api.doodle.domain.Doodle
import com.draw.it.api.doodle.domain.DoodleRepository
import com.draw.it.api.project.CreateProject
import com.draw.it.api.project.CreateProjectRequest
import com.draw.it.api.project.CreateProjectResponse
import com.draw.it.common.IntegrationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@IntegrationTest
@Transactional
class ConfirmDoodleTest {

    @Autowired
    private lateinit var confirmDoodle: ConfirmDoodle

    @Autowired
    private lateinit var doodleRepository: DoodleRepository

    @Autowired
    private lateinit var createProject: CreateProject

    @Test
    fun `두들 읽음 처리 성공`() {
        // given
        val project = createProejct()

        val doodle = Doodle(
            projectId = project.id!!,
            projectUuid = project.uuid,
            nickname = "테스터",
            letter = "테스트 편지",
            imageUrl = "https://example.com/image.jpg"
        )
        val savedDoodle = doodleRepository.save(doodle)

        // when
        confirmDoodle.confirmDoodle(project.id!!, savedDoodle.id!!)
    }

    private fun createProejct(): CreateProjectResponse {
        val userId = 1L
        val request = CreateProjectRequest(
            topic = "Test Topic",
            message = "Test Message",
            backgroundColor = "#FFFFFF"
        )
        val project = createProject.createProject(userId, request)
        return project
    }

    @Test
    fun `존재하지 않는 프로젝트로 두들 읽음 처리 시도시 실패`() {
        // given
        val nonExistentProjectId = 999L
        val nonExistentDoodleId = 999L

        // when & then
        val exception = assertThrows<BizException> {
            confirmDoodle.confirmDoodle(nonExistentProjectId, nonExistentDoodleId)
        }

        assertEquals("[400/잘못된 요청입니다] - 존재하지 않는 프로젝트입니다", exception.message)
    }
}