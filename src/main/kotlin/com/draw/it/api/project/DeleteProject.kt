package com.draw.it.api.project

import com.draw.it.api.project.domain.ProjectRepository
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/project")
class DeleteProject(
    private val projectRepository: ProjectRepository
) {

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