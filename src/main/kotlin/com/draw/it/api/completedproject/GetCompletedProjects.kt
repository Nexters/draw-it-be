package com.draw.it.api.completedproject

import com.draw.it.api.completedproject.domain.CompletedProjectRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@Tag(name = "Completed project")
@RestController
@RequestMapping("/completed-projects")
class GetCompletedProjects(
    private val completedProjectRepository: CompletedProjectRepository,
) {

    @Operation(summary = "사용자의 Completed project 목록 조회")
    @GetMapping
    fun getCompletedProjects(
        @AuthenticationPrincipal userId: Long
    ): List<CompletedProjectResponse> {
        val completedProjectsWithName = completedProjectRepository.findWithProjectNameByUserId(userId)
        val responses = completedProjectsWithName.map { completedProjectWithName ->
            CompletedProjectResponse(
                projectId = completedProjectWithName.projectId,
                projectName = completedProjectWithName.projectName,
                createdAt = completedProjectWithName.createdAt,
                imageUrl = completedProjectWithName.imageUrl
            )
        }
        
        return responses
    }
}

data class CompletedProjectResponse(
    val projectId: Long,
    val projectName: String,
    val createdAt: LocalDateTime,
    val imageUrl: String
)
