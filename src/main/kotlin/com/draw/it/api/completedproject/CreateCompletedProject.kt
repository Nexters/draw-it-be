package com.draw.it.api.completedproject

import com.draw.it.api.completedproject.domain.CompletedProject
import com.draw.it.api.completedproject.domain.CompletedProjectRepository
import com.draw.it.api.doodle.service.ImageStorageService
import com.draw.it.api.project.domain.ProjectRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Tag(name = "Completed project")
@RestController
@RequestMapping("/completed-projects")
class CreateCompletedProject(
    private val completedProjectRepository: CompletedProjectRepository,
    private val projectRepository: ProjectRepository,
    private val imageStorageService: ImageStorageService,
) {

    @Operation(summary = "Completed project 생성")
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Transactional
    fun createCompletedProject(
        @AuthenticationPrincipal userId: Long,
        @RequestParam projectId: Long,
        @RequestParam image: MultipartFile,
    ): CreateCompletedProjectResponse {

        val project = projectRepository.findById(projectId)
            ?: throw IllegalArgumentException("Project not found with id: $projectId")

        require(project.userId == userId) { "Project does not belong to user" }

        val imageUrl = imageStorageService.uploadImage(image)

        val completedProject = CompletedProject.create(
            projectId = projectId,
            imageUrl = imageUrl
        )

        val savedCompletedProject = completedProjectRepository.save(completedProject)

        return CreateCompletedProjectResponse(
            id = savedCompletedProject.id!!,
            projectId = savedCompletedProject.projectId,
            imageUrl = savedCompletedProject.imageUrl
        )
    }
}

data class CreateCompletedProjectResponse(
    val id: Long,
    val projectId: Long,
    val imageUrl: String,
)
