package com.draw.it.api.project

import com.draw.it.api.project.domain.Project
import com.draw.it.api.project.domain.ProjectRepository
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

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
        val project = Project(
            userId = userId,
            topic = request.topic,
            message = request.message,
            backgroundColor = request.backgroundColor,
            uuid = UUID.randomUUID().toString(),
        )

        val savedProject = projectRepository.save(project)

        return CreateProjectResponse(
            id = savedProject.id!!,
            userId = savedProject.userId,
            topic = savedProject.topic,
            message = savedProject.message,
            backgroundColor = savedProject.backgroundColor,
            uuid = savedProject.uuid,
            editorCoordinationState = savedProject.editorCoordinationState
        )
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
)