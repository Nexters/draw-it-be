package com.draw.it.api.project

import com.draw.it.common.IntegrationTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

@IntegrationTest
@Sql("/project.sql")
class DeleteProjectTest {
    @Autowired
    private lateinit var createProject: CreateProject

    @Autowired
    private lateinit var getProject: GetProject

    @Autowired
    private lateinit var sut: DeleteProject

    @Test
    fun `프로젝트를 삭제하고, 해당 프로젝트를 다시 조회하면 NOT_FOUND 예외가 발생한다`() {
        // given
        val userId = 1L
        val request = CreateProjectRequest(
            topic = "Test Topic",
            message = "Test Message",
            backgroundColor = "#FFFFFF"
        )
        val createdProject = createProject.createProject(userId, request)

        // when
        sut.deleteProject(userId, createdProject.id)

        // then
        assertThrows<RuntimeException> {
            getProject.getProjectByUuid(createdProject.uuid)
        }
    }
}