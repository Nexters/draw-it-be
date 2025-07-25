package com.draw.it.api.project

import com.draw.it.api.project.domain.Project
import com.draw.it.api.project.domain.ProjectRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/project")
class GetProject(
    private val projectRepository: ProjectRepository
) {

    @GetMapping("/{uuid}")
    fun getProjectByUuid(@PathVariable uuid: String): GetProjectResponse {
        val project = projectRepository.findByUuid(uuid)
            ?: throw RuntimeException("Project not found with uuid: $uuid")

        return project.toResponse()
    }

    private fun Project.toResponse(): GetProjectResponse {
        return GetProjectResponse(
            id = this.id!!,
            userId = this.userId,
            topic = this.topic,
            message = this.message,
            backgroundColor = this.backgroundColor,
            uuid = this.uuid,
            editorState = this.editorCoordinationState,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
}

data class GetProjectResponse(
    val id: Long,
    val userId: Long,
    val topic: String,
    val message: String,
    val backgroundColor: String,
    val uuid: String,
    val editorState: String?,
    val createdAt: java.time.LocalDateTime?,
    val updatedAt: java.time.LocalDateTime?
)