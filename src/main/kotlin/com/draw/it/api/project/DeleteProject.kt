package com.draw.it.api.project

import com.draw.it.api.project.domain.ProjectRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@Tag(name = "Project", description = "프로젝트 관리 API")
@RestController
@RequestMapping("/project")
class DeleteProject(
    private val projectRepository: ProjectRepository
) {

    @Operation(summary = "프로젝트 삭제", description = "프로젝트를 삭제합니다")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "프로젝트 삭제 성공"),
            ApiResponse(responseCode = "403", description = "권한이 없습니다"),
            ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없습니다")
        ]
    )
    @DeleteMapping("/{id}")
    fun deleteProject(
        @AuthenticationPrincipal userId: Long,
        @PathVariable id: Long
    ) {
        val project = projectRepository.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "프로젝트를 찾을 수 없습니다.")

        if (project.userId != userId)
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.")

        projectRepository.delete(project)
    }
}