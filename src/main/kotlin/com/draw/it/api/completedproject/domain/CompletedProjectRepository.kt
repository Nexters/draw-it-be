package com.draw.it.api.completedproject.domain

interface CompletedProjectRepository {
    fun save(completedProject: CompletedProject): CompletedProject
    fun findById(id: Long): CompletedProject?
    fun findByUserId(userId: Long): List<CompletedProject>
    fun findWithProjectNameByUserId(userId: Long): List<CompletedProjectWithName>
    fun deleteByProjectId(projectId: Long)
}