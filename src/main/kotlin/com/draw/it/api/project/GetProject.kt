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
import java.time.LocalDateTime

@Tag(name = "Project", description = "프로젝트 관리 API")
@RestController
@RequestMapping("/project")
class GetProject(
    private val projectRepository: ProjectRepository,
    private val doodleRepository: DoodleRepository
) {

    @Operation(summary = "내 프로젝트 상세 조회", description = "프로젝트를 조회합니다")
    @GetMapping("/{projectId}")
    fun getProjectBy(
        @PathVariable projectId: Long
    ): GetProjectResponse {
        val project = projectRepository.findById(projectId)
            ?: throw RuntimeException("Project not found with uuid: $projectId")
        val doodles = getDoodlesByProjectId(projectId)
        
        // 프로젝트 조회 시 모든 doodles를 confirm 상태로 변경하여 저장
        doodles.forEach { doodle ->
            if (!doodle.isNewDoodleConfirmed) {
                doodle.confirmDoodle()
            }
        }
        doodleRepository.saveAll(doodles)
        
        return project.toResponse(doodles, limitDoodles = false)
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

    private fun getDoodlesByProjectId(projectId: Long) = doodleRepository.findByProjectId(projectId)

    private fun Project.toResponse(doodles: List<Doodle>? = null, limitDoodles: Boolean = true): GetProjectResponse {
        val projectDoodles = doodles ?: getDoodlesByProjectId(this.id!!)
        val doodleResponses = projectDoodles.map { it.toResponse() }
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
            doodleList = if (limitDoodles) doodleResponses.take(5) else doodleResponses,
            isNewDoodleConfirmed = projectDoodles.any { it.isNewDoodleConfirmed },
            doodleCount = projectDoodles.size.toLong()
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
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
    val doodleList: List<DoodleResponse>,
    val isNewDoodleConfirmed: Boolean,
    val doodleCount: Long,
)

data class DoodleResponse(
    val id: Long,
    val projectId: Long,
    val nickname: String,
    val letter: String?,
    val imageUrl: String,
    val isNewDoodleConfirmed: Boolean,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
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
