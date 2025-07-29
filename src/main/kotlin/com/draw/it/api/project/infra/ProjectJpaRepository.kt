package com.draw.it.api.project.infra

import com.draw.it.api.project.domain.Project
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectJpaRepository : JpaRepository<Project, Long> {
    fun findByUuid(uuid: String): Project?
    fun findAllByUserId(userId: Long): List<Project>
}
