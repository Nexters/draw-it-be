package com.draw.it.api.metrics

import com.draw.it.api.completedproject.infra.CompletedProjectJpaRepository
import com.draw.it.api.doodle.infra.DoodleJpaRepository
import com.draw.it.api.project.infra.ProjectJpaRepository
import com.draw.it.api.user.infra.UserJpaRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class DailyMetricsService(
    private val userJpaRepository: UserJpaRepository,
    private val projectJpaRepository: ProjectJpaRepository,
    private val doodleJpaRepository: DoodleJpaRepository,
    private val completedProjectJpaRepository: CompletedProjectJpaRepository
) {
    
    fun getDailyMetrics(): DailyMetrics {
        val today = LocalDate.now().minusDays(1)
        val startOfDay = LocalDateTime.of(today, LocalTime.MIN)
        val endOfDay = LocalDateTime.of(today, LocalTime.MAX)
        
        val totalUsers = userJpaRepository.count()
        val totalProjects = projectJpaRepository.count()
        val totalDoodles = doodleJpaRepository.count()
        val totalCompletedProjects = completedProjectJpaRepository.count()
        
        val newUsersToday = userJpaRepository.countByCreatedAtBetween(startOfDay, endOfDay)
        val newProjectsToday = projectJpaRepository.countByCreatedAtBetween(startOfDay, endOfDay)
        val newDoodlesToday = doodleJpaRepository.countByCreatedAtBetween(startOfDay, endOfDay)
        val newCompletedProjectsToday = completedProjectJpaRepository.countByCreatedAtBetween(startOfDay, endOfDay)
        
        return DailyMetrics(
            date = today,
            totalUsers = totalUsers,
            totalProjects = totalProjects,
            totalDoodles = totalDoodles,
            totalCompletedProjects = totalCompletedProjects,
            newUsersToday = newUsersToday,
            newProjectsToday = newProjectsToday,
            newDoodlesToday = newDoodlesToday,
            newCompletedProjectsToday = newCompletedProjectsToday
        )
    }
}

data class DailyMetrics(
    val date: LocalDate,
    val totalUsers: Long,
    val totalProjects: Long,
    val totalDoodles: Long,
    val totalCompletedProjects: Long,
    val newUsersToday: Long,
    val newProjectsToday: Long,
    val newDoodlesToday: Long,
    val newCompletedProjectsToday: Long
)
