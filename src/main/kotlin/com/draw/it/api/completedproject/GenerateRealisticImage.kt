package com.draw.it.api.completedproject

import com.draw.it.api.completedproject.domain.CompletedProjectRepository
import com.draw.it.api.completedproject.infra.ImageGenerator
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Completed project")
@RestController
@RequestMapping("/completed-projects")
class GenerateRealisticImage(
    private val completedProjectRepository: CompletedProjectRepository,
    private val imageGenerator: ImageGenerator
) {

    @Operation(summary = "AI 실사화 이미지 생성")
    @PostMapping("/generate-realistic-image")
    fun generateRealisticImage() {

    }
}