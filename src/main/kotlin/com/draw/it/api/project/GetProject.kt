package com.draw.it.api.project

import com.draw.it.api.doodle.domain.Doodle
import com.draw.it.api.doodle.domain.DoodleRepository
import com.draw.it.api.project.domain.Project
import com.draw.it.api.project.domain.ProjectRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
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

    @Operation(summary = "프로젝트 조회", description = "UUID로 프로젝트를 조회합니다")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "프로젝트 조회 성공",
                content = [Content(schema = Schema(implementation = GetProjectResponse::class))]
            ),
            ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없습니다")
        ]
    )
    @GetMapping("/{uuid}")
    fun getProjectByUuid(
        @PathVariable uuid: String
    ): GetProjectResponse {
        val project = projectRepository.findByUuid(uuid)
            ?: throw RuntimeException("Project not found with uuid: $uuid")

        return project.toResponse()
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