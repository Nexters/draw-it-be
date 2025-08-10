package com.draw.it.api.completedproject.domain

import java.time.LocalDateTime

data class CompletedProjectWithName(
    val projectId: Long,
    val imageUrl: String,
    val projectName: String,
    val createdAt: LocalDateTime
)