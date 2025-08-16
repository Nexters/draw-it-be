package com.draw.it.api.project

import com.draw.it.api.project.domain.Project
import com.draw.it.api.project.domain.ProjectRepository
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "Project", description = "프로젝트 관리 API")
@RestController
@RequestMapping("/project")
class UpdateProjectEditorCoordinationState(
    private val projectRepository: ProjectRepository
) {

    @PutMapping("/{projectId}/editor-coordination-state")
    fun updateEditorCoordinationState(
        @PathVariable projectId: Long,
        @AuthenticationPrincipal userId: Long,
        @RequestBody request: UpdateEditorCoordinationStateRequest
    ): UpdateEditorCoordinationStateResponse {
        return execute(projectId, userId, request.editorCoordinationState)
    }

    fun execute(
        projectId: Long,
        userId: Long,
        editorCoordinationState: String
    ): UpdateEditorCoordinationStateResponse {
        val project = projectRepository.findById(projectId)
            ?: throw IllegalArgumentException("존재하지 않는 프로젝트입니다.")

        project.validateOwnership(userId)
        project.updateEditorCoordinationState(editorCoordinationState)

        val updatedProject = projectRepository.save(project)

        return UpdateEditorCoordinationStateResponse.from(updatedProject)
    }
}

data class UpdateEditorCoordinationStateRequest(
    val editorCoordinationState: String
)

data class UpdateEditorCoordinationStateResponse(
    val id: Long,
    val userId: Long,
    val topic: String,
    val message: String,
    val backgroundColor: String,
    val uuid: String,
    val editorCoordinationState: String?
) {
    companion object {
        fun from(project: Project): UpdateEditorCoordinationStateResponse {
            return UpdateEditorCoordinationStateResponse(
                id = project.id!!,
                userId = project.userId,
                topic = project.topic,
                message = project.message,
                backgroundColor = project.backgroundColor,
                uuid = project.uuid,
                editorCoordinationState = project.editorCoordinationState
            )
        }
    }
}