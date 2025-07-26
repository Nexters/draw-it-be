package com.draw.it.api.doodle.infra

import com.draw.it.api.doodle.domain.Doodle
import com.draw.it.api.doodle.domain.DoodleRepository
import org.springframework.stereotype.Repository

@Repository
class DoodleRepositoryImpl(
    private val doodleJpaRepository: DoodleJpaRepository,
) : DoodleRepository {

    override fun save(doodle: Doodle): Doodle {
        return doodleJpaRepository.save(doodle)
    }

    override fun findById(id: Long): Doodle? {
        return doodleJpaRepository.findById(id).orElse(null)
    }

    override fun findByProjectId(projectId: Long): List<Doodle> {
        return doodleJpaRepository.findByProjectId(projectId)
    }

    override fun delete(doodle: Doodle) {
        doodleJpaRepository.delete(doodle)
    }
}
