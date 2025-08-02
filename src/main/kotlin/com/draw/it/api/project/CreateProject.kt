package com.draw.it.api.project

import com.draw.it.api.project.domain.Project
import com.draw.it.api.project.domain.ProjectRepository
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Project", description = "프로젝트 관리 API")
@RestController
@RequestMapping("/project")
class CreateProject(
    private val projectRepository: ProjectRepository
) {

    @PostMapping
    fun createProject(
        @AuthenticationPrincipal userId: Long,
        @RequestBody request: CreateProjectRequest
    ): CreateProjectResponse {
        val project = Project.create(
            userId,
            request.topic,
            request.message,
            request.backgroundColor
        )

        val savedProject = projectRepository.save(project)

        return CreateProjectResponse.from(savedProject)
    }
}

data class CreateProjectRequest(
    val topic: String,
    val message: String,
    val backgroundColor: String,
)

data class CreateProjectResponse(
    val id: Long,
    val userId: Long,
    val topic: String,
    val message: String,
    val backgroundColor: String,
    val uuid: String,
    val editorCoordinationState: String?
) {
    companion object {
        fun from(project: Project): CreateProjectResponse {
            return CreateProjectResponse(
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
