package com.draw.it.api.doodle.infra

import com.draw.it.api.doodle.domain.Doodle
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface DoodleJpaRepository : JpaRepository<Doodle, Long> {
    fun findByIdIn(ids: List<Long>): List<Doodle>
    fun findByProjectId(projectId: Long): List<Doodle>
    fun findByProjectUuid(projectUuid: String): List<Doodle>
    fun countByCreatedAtBetween(startDate: LocalDateTime, endDate: LocalDateTime): Long
}