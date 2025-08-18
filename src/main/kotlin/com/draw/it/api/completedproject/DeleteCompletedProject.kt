package com.draw.it.api.completedproject

import com.draw.it.api.completedproject.domain.CompletedProjectRepository
import com.draw.it.api.project.domain.ProjectRepository
import com.draw.it.api.common.exception.BizException
import com.draw.it.api.common.exception.ErrorCode
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.transaction.annotation.Transactional

@RestController
class DeleteCompletedProject(
    private val completedProjectRepository: CompletedProjectRepository,
    private val projectRepository: ProjectRepository
) {

    @DeleteMapping("/api/completed-projects/project/{projectId}")
    @Transactional
    fun deleteCompletedProject(
        @PathVariable projectId: Long,
        @AuthenticationPrincipal userId: Long
    ) {
        val project = projectRepository.findById(projectId)
            ?: throw BizException(ErrorCode.BAD_REQUEST, "존재하지 않는 프로젝트입니다")

        if (project.userId != userId) {
            throw BizException(ErrorCode.FORBIDDEN, "프로젝트 소유자만 완성된 프로젝트를 삭제할 수 있습니다")
        }

        completedProjectRepository.deleteByProjectId(projectId)
    }
}