package com.draw.it.api.project.infra

import com.draw.it.api.project.domain.Project
import com.draw.it.api.project.domain.ProjectRepository
import org.springframework.stereotype.Repository

@Repository
class ProjectRepositoryImpl(
    private val projectJpaRepository: ProjectJpaRepository
) : ProjectRepository {

    override fun save(project: Project): Project {
        return projectJpaRepository.save(project)
    }

    override fun findById(id: Long): Project? {
        return projectJpaRepository.findById(id).orElse(null)
    }

    override fun findByUuid(uuid: String): Project? {
        return projectJpaRepository.findByUuid(uuid)
    }
}