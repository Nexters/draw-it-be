package com.draw.it.api.project.domain

interface ProjectRepository {
    fun save(project: Project): Project
    fun findById(id: Long): Project?
    fun findByUuid(uuid: String): Project?
    fun findAllByUserId(userId: Long): List<Project>
    fun countByUserId(userId: Long): Long
    fun delete(project: Project)
}