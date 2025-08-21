package com.draw.it.api.completedproject

import com.draw.it.api.common.exception.BizException
import com.draw.it.api.common.exception.ErrorCode
import com.draw.it.api.completedproject.domain.CompletedProject
import com.draw.it.api.completedproject.domain.CompletedProjectRepository
import com.draw.it.api.completedproject.infra.ImageDownloadService
import com.draw.it.api.completedproject.infra.ImageGenerator
import com.draw.it.api.doodle.service.ImageStorageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.File
import java.util.*
import java.util.concurrent.CompletableFuture

@Tag(name = "Completed project")
@RestController
@RequestMapping("/completed-projects")
class GenerateRealisticImage(
    private val completedProjectRepository: CompletedProjectRepository,
    private val imageGenerator: ImageGenerator,
    private val imageDownloadService: ImageDownloadService,
    private val imageStorageService: ImageStorageService
) {

    @Operation(summary = "AI 실사화 이미지 생성")
    @PostMapping("/{completedProjectId}/generate-realistic-image")
    fun generateRealisticImage(
        @Parameter(description = "완성된 프로젝트 ID", example = "1")
        @PathVariable completedProjectId: Long
    ) {
        val completedProject = completedProjectRepository.findById(completedProjectId)
            ?: throw BizException(ErrorCode.NOT_FOUND, "완성된 프로젝트를 찾을 수 없습니다: $completedProjectId")
        generateRealisticImageAsync(completedProject)
    }

    @Async
    fun generateRealisticImageAsync(completedProject: CompletedProject): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            val downloadedImageFile = imageDownloadService.downloadImage(completedProject.imageUrl)
            try {
                // 1. AI로 실사화 이미지 생성 (base64 반환)
                val base64ImageData = imageGenerator.convertDrawingToRealistic(downloadedImageFile)

                // 2. Base64를 MultipartFile로 변환하여 S3에 업로드
                val multipartFile = createMultipartFileFromBase64(base64ImageData)
                val realisticImageUrl = imageStorageService.uploadImage(multipartFile)

                // 3. 새로운 CompletedProject 생성
                val newCompletedProject = CompletedProject.create(
                    projectId = completedProject.projectId,
                    imageUrl = realisticImageUrl
                )

                completedProjectRepository.save(newCompletedProject)
            } finally {
                downloadedImageFile.delete()
            }
        }
    }

    private fun createMultipartFileFromBase64(base64Data: String): MultipartFile {
        val imageBytes = Base64.getDecoder().decode(base64Data)
        return object : MultipartFile {
            override fun getName(): String = "realistic_image"
            override fun getOriginalFilename(): String = "realistic_image_${System.currentTimeMillis()}.png"
            override fun getContentType(): String = "image/png"
            override fun isEmpty(): Boolean = imageBytes.isEmpty()
            override fun getSize(): Long = imageBytes.size.toLong()
            override fun getBytes(): ByteArray = imageBytes
            override fun getInputStream() = ByteArrayInputStream(imageBytes)
            override fun transferTo(dest: File) {
                dest.writeBytes(imageBytes)
            }
        }
    }
}