package com.draw.it.api.completedproject.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "실사화 이미지 생성 응답")
data class GenerateRealisticImageResponse(
    @Schema(description = "생성된 실사화 이미지 URL", example = "https://example.com/realistic-image.jpg")
    val realisticImageUrl: String,

    @Schema(description = "원본 완성된 프로젝트 ID", example = "1")
    val completedProjectId: Long
)