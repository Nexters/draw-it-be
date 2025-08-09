package com.draw.it.api.doodle

import com.draw.it.api.common.exception.BizException
import com.draw.it.api.common.exception.ErrorCode
import com.draw.it.api.doodle.domain.DoodleRepository
import com.draw.it.api.project.domain.ProjectRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Doodle", description = "두들 API")
@RestController
@RequestMapping("/project/{projectId}/doodle")
class ConfirmDoodle(
    private val doodleRepository: DoodleRepository,
    private val projectRepository: ProjectRepository
) {

    @Operation(summary = "두들 읽음 처리", description = "두들을 읽음 처리합니다.")
    @PutMapping("/{doodleId}/confirm")
    fun confirmDoodle(
        @PathVariable projectId: Long,
        @PathVariable doodleId: Long
    ) {
        projectRepository.findById(projectId)
            ?: throw BizException(ErrorCode.BAD_REQUEST, "존재하지 않는 프로젝트입니다")

        val doodle = doodleRepository.findById(doodleId)
            ?: throw BizException(ErrorCode.BAD_REQUEST, "존재하지 않는 두들입니다")

        if (doodle.projectId != projectId) {
            throw BizException(ErrorCode.BAD_REQUEST, "해당 프로젝트에 속하지 않는 두들입니다")
        }

        doodle.confirmDoodle()

        doodleRepository.save(doodle)
    }
}

