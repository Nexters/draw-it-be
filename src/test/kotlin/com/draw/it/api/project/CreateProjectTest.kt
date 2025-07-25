package com.draw.it.api.project

import com.draw.it.common.IntegrationTest
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockkStatic
import org.approvaltests.Approvals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import java.util.*

@IntegrationTest
@Sql("/project.sql")
class CreateProjectTest {
    @Autowired
    private lateinit var createProject: CreateProject

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `새로운 프로젝트를 생성하고 저장한다`() {
        val request = CreateProjectRequest(
            topic = "테스트 프로젝트",
            message = "테스트 메시지",
            backgroundColor = "#FFFFFF",
        )
        // UUID.randomUUID().toString() should be mocked to return a consistent value for testing
        mockkStatic(UUID::class)
        every { UUID.randomUUID().toString() } returns "test-uuid"

        val response = createProject.createProject(1L, request)

        // JSON으로 변환 후 id 필드 제거하고 검증
        Approvals.verify(response.toJson())
    }

    private fun CreateProjectResponse.toJson(): String {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)
    }
}