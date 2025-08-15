package com.draw.it.api.doodle

import com.draw.it.api.common.exception.BizException
import com.draw.it.api.common.exception.ErrorCode
import com.draw.it.api.doodle.domain.DoodleRepository
import com.draw.it.api.project.domain.ProjectRepository
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class DeleteDoodlesRequest(
    val doodleIds: List<Long>
)

@Tag(name = "Doodle", description = "두들 API")
@RestController
@RequestMapping("/project/{projectId}/doodle")
class DeleteDoodle(
    private val doodleRepository: DoodleRepository,
    private val projectRepository: ProjectRepository,
) {
    @DeleteMapping
    fun deleteDoodles(
        @Parameter(description = "프로젝트 ID", required = true)
        @PathVariable projectId: Long,
        @RequestBody request: DeleteDoodlesRequest,
        @AuthenticationPrincipal userId: Long,
    ) {
        // 프로젝트 존재 여부 및 소유자 확인
        val project = projectRepository.findById(projectId)
            ?: throw BizException(ErrorCode.BAD_REQUEST, "존재하지 않는 프로젝트입니다")

        if (project.userId != userId) {
            throw BizException(ErrorCode.FORBIDDEN, "프로젝트 소유자만 두들을 삭제할 수 있습니다")
        }

        // 삭제할 두들들 조회 및 검증
        val doodles = doodleRepository.findByIdIn(request.doodleIds)
        
        // 요청된 두들 ID와 실제 존재하는 두들 ID 비교
        val foundDoodleIds = doodles.map { it.id }.toSet()
        val notFoundIds = request.doodleIds.toSet() - foundDoodleIds
        if (notFoundIds.isNotEmpty()) {
            throw BizException(ErrorCode.BAD_REQUEST, "존재하지 않는 두들입니다: ${notFoundIds.joinToString()}")
        }

        // 모든 두들이 해당 프로젝트에 속하는지 확인
        val invalidDoodles = doodles.filter { it.projectId != projectId }
        if (invalidDoodles.isNotEmpty()) {
            throw BizException(ErrorCode.BAD_REQUEST, "해당 프로젝트의 두들이 아닙니다: ${invalidDoodles.map { it.id }.joinToString()}")
        }

        // 소프트 삭제 수행
        doodles.forEach { doodle ->
            doodle.delete()
        }
        doodleRepository.saveAll(doodles)
    }
}
