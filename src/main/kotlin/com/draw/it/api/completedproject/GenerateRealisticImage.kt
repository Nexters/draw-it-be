package com.draw.it.api.completedproject

import com.draw.it.api.common.exception.BizException
import com.draw.it.api.common.exception.ErrorCode
import com.draw.it.api.completedproject.domain.CompletedProjectRepository
import com.draw.it.api.completedproject.dto.GenerateRealisticImageResponse
import com.draw.it.api.completedproject.infra.ImageDownloadService
import com.draw.it.api.completedproject.infra.ImageGenerator
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File

@Tag(name = "Completed project")
@RestController
@RequestMapping("/completed-projects")
class GenerateRealisticImage(
    private val completedProjectRepository: CompletedProjectRepository,
    private val imageGenerator: ImageGenerator,
    private val imageDownloadService: ImageDownloadService
) {

    @Operation(summary = "AI 실사화 이미지 생성. (아직 테스트 안함)")
    @PostMapping("/{completedProjectId}/generate-realistic-image")
    fun generateRealisticImage(
        @Parameter(description = "완성된 프로젝트 ID", example = "1")
        @PathVariable completedProjectId: Long
    ): GenerateRealisticImageResponse {
        val completedProject = completedProjectRepository.findById(completedProjectId)
            ?: throw BizException(ErrorCode.NOT_FOUND, "완성된 프로젝트를 찾을 수 없습니다: $completedProjectId")

        val downloadedImageFile = imageDownloadService.downloadImage(completedProject.imageUrl)
        return convertToRealisticImage(downloadedImageFile, completedProjectId)
    }

    private fun convertToRealisticImage(
        downloadedImageFile: File,
        completedProjectId: Long
    ): GenerateRealisticImageResponse {
        try {
            val realisticImageUrl = imageGenerator.convertDrawingToRealistic(downloadedImageFile)
            return GenerateRealisticImageResponse(
                realisticImageUrl = realisticImageUrl,
                completedProjectId = completedProjectId
            )
        } finally {
            downloadedImageFile.delete()
        }
    }
}