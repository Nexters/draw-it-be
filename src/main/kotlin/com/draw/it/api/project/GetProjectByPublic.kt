package com.draw.it.api.project

import com.draw.it.api.project.domain.Project
import com.draw.it.api.project.domain.ProjectRepository
import com.draw.it.api.user.GetBasicUserInfo
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Project", description = "프로젝트 관리 API")
@RestController
@RequestMapping("/anonymous/project")
class GetProjectByPublic(
    private val projectRepository: ProjectRepository,
    private val getBasicUserInfo: GetBasicUserInfo
) {
    @Operation(summary = "게스트 프로젝트 조회", description = "게스트 유저가 UUID로 프로젝트를 조회합니다")
    @GetMapping("/{uuid}")
    fun getProjectByUuid(
        @PathVariable uuid: String
    ): GetAnonymousProjectResponse {
        val project = projectRepository.findByUuid(uuid)
            ?: throw RuntimeException("Project not found with uuid: $uuid")

        return project.toResponse()
    }

    private fun Project.toResponse(): GetAnonymousProjectResponse {
        val userName = getBasicUserInfo.getUserName(this.userId)

        return GetAnonymousProjectResponse(
            id = this.id!!,
            userId = this.userId,
            userName = userName,
            topic = this.topic,
            message = this.message,
            backgroundColor = this.backgroundColor,
            uuid = this.uuid,
            editorCoordinationState = this.editorCoordinationState,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    data class GetAnonymousProjectResponse(
        val id: Long,
        val userId: Long,
        val userName: String?,
        val topic: String,
        val message: String,
        val backgroundColor: String,
        val uuid: String,
        val editorCoordinationState: String?,
        val createdAt: java.time.LocalDateTime?,
        val updatedAt: java.time.LocalDateTime?,
    )
}
