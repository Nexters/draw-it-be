package com.draw.it.api.project

import com.draw.it.api.project.domain.Project
import com.draw.it.api.project.domain.ProjectRepository
import com.draw.it.common.IntegrationTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional

@IntegrationTest
@Sql("/project.sql")
@Transactional
class UpdateProjectEditorCoordinationStateTest {

    @Autowired
    private lateinit var projectRepository: ProjectRepository

    @Autowired
    private lateinit var updateProjectEditorCoordinationState: UpdateProjectEditorCoordinationState

    @Test
    fun `editorCoordinationState를 성공적으로 업데이트한다`() {
        // given
        val project = Project.create(
            userId = 1L,
            topic = "테스트 프로젝트",
            message = "테스트 메시지",
            backgroundColor = "#FFFFFF"
        )
        val savedProject = projectRepository.save(project)
        val newEditorState = """{"elements": [{"type": "text", "x": 100, "y": 200}]}"""

        // when
        val result = updateProjectEditorCoordinationState.execute(
            savedProject.id!!,
            1L,
            newEditorState
        )

        // then
        assertNotNull(result)
        assertEquals(newEditorState, result.editorCoordinationState)

        // 데이터베이스에서 확인
        val updatedProject = projectRepository.findById(savedProject.id!!)
        assertNotNull(updatedProject)
        assertEquals(newEditorState, updatedProject!!.editorCoordinationState)
    }

    @Test
    fun `존재하지 않는 프로젝트 ID로 업데이트 시 예외가 발생한다`() {
        // given
        val nonExistentProjectId = 999L
        val editorState = """{"elements": []}"""

        // when & then
        assertThrows(IllegalArgumentException::class.java) {
            updateProjectEditorCoordinationState.execute(
                nonExistentProjectId,
                1L,
                editorState
            )
        }
    }

    @Test
    fun `다른 사용자의 프로젝트를 업데이트하려고 하면 예외가 발생한다`() {
        // given
        val project = Project.create(
            userId = 1L,
            topic = "테스트 프로젝트",
            message = "테스트 메시지",
            backgroundColor = "#FFFFFF"
        )
        val savedProject = projectRepository.save(project)
        val editorState = """{"elements": []}"""

        // when & then
        assertThrows(IllegalArgumentException::class.java) {
            updateProjectEditorCoordinationState.execute(
                savedProject.id!!,
                2L, // 다른 사용자 ID
                editorState
            )
        }
    }
}