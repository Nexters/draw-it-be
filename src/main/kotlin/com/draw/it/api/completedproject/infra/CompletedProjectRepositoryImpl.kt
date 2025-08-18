package com.draw.it.api.completedproject.infra

import com.draw.it.api.completedproject.domain.CompletedProject
import com.draw.it.api.completedproject.domain.CompletedProjectRepository
import com.draw.it.api.completedproject.domain.CompletedProjectWithName
import org.springframework.stereotype.Repository

@Repository
class CompletedProjectRepositoryImpl(
    private val jpaRepository: CompletedProjectJpaRepository
) : CompletedProjectRepository {

    override fun save(completedProject: CompletedProject): CompletedProject {
        return jpaRepository.save(completedProject)
    }

    override fun findById(id: Long): CompletedProject? {
        return jpaRepository.findById(id).orElse(null)
    }

    override fun findByUserId(userId: Long): List<CompletedProject> {
        return jpaRepository.findByUserId(userId)
    }

    override fun findWithProjectNameByUserId(userId: Long): List<CompletedProjectWithName> {
        return jpaRepository.findWithProjectNameByUserId(userId)
    }

    override fun deleteByProjectId(projectId: Long) {
        jpaRepository.deleteByProjectId(projectId)
    }
}