package com.draw.it.api.doodle.domain

interface DoodleRepository {
    fun save(doodle: Doodle): Doodle
    fun findById(id: Long): Doodle?
    fun findByProjectId(projectId: Long): List<Doodle>
}