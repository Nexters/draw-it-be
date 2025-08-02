package com.draw.it.api.completedproject.domain

interface CompletedProjectRepository {
    fun save(completedProject: CompletedProject): CompletedProject
    fun findByUserId(userId: Long): List<CompletedProject>
}