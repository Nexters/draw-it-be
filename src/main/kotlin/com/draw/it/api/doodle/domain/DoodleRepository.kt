package com.draw.it.api.doodle.domain

interface DoodleRepository {
    fun save(doodle: Doodle): Doodle
    fun saveAll(doodles: List<Doodle>): List<Doodle>
    fun findById(id: Long): Doodle?
    fun findByIdIn(ids: List<Long>): List<Doodle>
    fun findByProjectId(projectId: Long): List<Doodle>
    fun findByProjectUuid(projectUuid: String): List<Doodle>
    fun delete(doodle: Doodle)
}