package com.draw.it.api.feedback.infra

import com.draw.it.api.feedback.domain.Feedback
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface FeedbackJpaRepository : JpaRepository<Feedback, Long> {
    fun countByCreatedAtBetween(start: LocalDateTime, end: LocalDateTime): Long
    
    fun findByCreatedAtBetween(start: LocalDateTime, end: LocalDateTime): List<Feedback>
    
    @Query("SELECT AVG(f.rating) FROM Feedback f")
    fun findAverageRating(): Double?
    
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.createdAt BETWEEN :start AND :end")
    fun findAverageRatingByCreatedAtBetween(start: LocalDateTime, end: LocalDateTime): Double?
}