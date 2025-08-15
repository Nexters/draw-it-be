package com.draw.it.api.doodle.infra

import com.draw.it.api.doodle.domain.Doodle
import org.springframework.data.jpa.repository.JpaRepository

interface DoodleJpaRepository : JpaRepository<Doodle, Long> {
    fun findByIdIn(ids: List<Long>): List<Doodle>
    fun findByProjectId(projectId: Long): List<Doodle>
}