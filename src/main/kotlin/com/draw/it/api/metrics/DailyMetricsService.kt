package com.draw.it.api.metrics

import com.draw.it.api.completedproject.infra.CompletedProjectJpaRepository
import com.draw.it.api.doodle.infra.DoodleJpaRepository
import com.draw.it.api.feedback.infra.FeedbackJpaRepository
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
    private val completedProjectJpaRepository: CompletedProjectJpaRepository,
    private val feedbackJpaRepository: FeedbackJpaRepository
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
        
        val totalFeedbacks = feedbackJpaRepository.count()
        val newFeedbacksToday = feedbackJpaRepository.countByCreatedAtBetween(startOfDay, endOfDay)
        val averageRating = feedbackJpaRepository.findAverageRating() ?: 0.0
        val todayAverageRating = feedbackJpaRepository.findAverageRatingByCreatedAtBetween(startOfDay, endOfDay) ?: 0.0
        val todayFeedbacks = feedbackJpaRepository.findByCreatedAtBetween(startOfDay, endOfDay)
        val todayFeedbackMessages = todayFeedbacks.mapNotNull { it.comment }.filter { it.isNotBlank() }
        
        return DailyMetrics(
            date = today,
            totalUsers = totalUsers,
            totalProjects = totalProjects,
            totalDoodles = totalDoodles,
            totalCompletedProjects = totalCompletedProjects,
            newUsersToday = newUsersToday,
            newProjectsToday = newProjectsToday,
            newDoodlesToday = newDoodlesToday,
            newCompletedProjectsToday = newCompletedProjectsToday,
            totalFeedbacks = totalFeedbacks,
            newFeedbacksToday = newFeedbacksToday,
            averageRating = averageRating,
            todayAverageRating = todayAverageRating,
            todayFeedbackMessages = todayFeedbackMessages
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
    val newCompletedProjectsToday: Long,
    val totalFeedbacks: Long,
    val newFeedbacksToday: Long,
    val averageRating: Double,
    val todayAverageRating: Double,
    val todayFeedbackMessages: List<String>
)
