package com.draw.it.api.project

import com.draw.it.api.doodle.domain.Doodle
import com.draw.it.api.doodle.domain.DoodleRepository
import com.draw.it.api.project.domain.Project
import com.draw.it.api.project.domain.ProjectRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Project", description = "프로젝트 관리 API")
@RestController
@RequestMapping("/project")
class GetProject(
    private val projectRepository: ProjectRepository,
    private val doodleRepository: DoodleRepository
) {

    @Operation(summary = "내 프로젝트 상세 조회", description = "프로젝트를 조회합니다")
    @GetMapping("/{uuid}")
    fun getProjectByUuid(
        @PathVariable uuid: String
    ): GetProjectResponse {
        val project = projectRepository.findByUuid(uuid)
            ?: throw RuntimeException("Project not found with uuid: $uuid")

        return project.toResponse()
    }

    @Operation(summary = "내 프로젝트 목록 조회", description = "현재 인증된 사용자의 모든 프로젝트를 조회합니다")
    @GetMapping("/my")
    fun getMyProjects(
        @AuthenticationPrincipal userId: Long
    ): List<GetProjectResponse> {
        val projects = projectRepository.findAllByUserId(userId)
        return projects.map { it.toResponse() }
    }

    @Operation(summary = "내 프로젝트 개수 조회", description = "현재 인증된 사용자의 전체 프로젝트 개수를 조회합니다")
    @GetMapping("/my/count")
    fun getMyProjectCount(
        @AuthenticationPrincipal userId: Long
    ): GetProjectCountResponse {
        val count = projectRepository.countByUserId(userId)
        return GetProjectCountResponse(count)
    }

    private fun Project.toResponse(): GetProjectResponse {
        val doodles = doodleRepository.findByProjectId(this.id!!)
        return GetProjectResponse(
            id = this.id!!,
            userId = this.userId,
            topic = this.topic,
            message = this.message,
            backgroundColor = this.backgroundColor,
            uuid = this.uuid,
            editorCoordinationState = this.editorCoordinationState,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            doodleList = doodles.map { it.toResponse() }
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
    val editorCoordinationState: String?,
    val createdAt: java.time.LocalDateTime?,
    val updatedAt: java.time.LocalDateTime?,
    val doodleList: List<DoodleResponse>
)

data class DoodleResponse(
    val id: Long,
    val projectId: Long,
    val nickname: String,
    val letter: String?,
    val imageUrl: String,
    val isNewDoodleConfirmed: Boolean,
    val createdAt: java.time.LocalDateTime?,
    val updatedAt: java.time.LocalDateTime?
)

data class GetProjectCountResponse(
    val count: Long
)

private fun Doodle.toResponse(): DoodleResponse {
    return DoodleResponse(
        id = this.id!!,
        projectId = this.projectId,
        nickname = this.nickname,
        letter = this.letter,
        imageUrl = this.imageUrl,
        isNewDoodleConfirmed = this.isNewDoodleConfirmed,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
