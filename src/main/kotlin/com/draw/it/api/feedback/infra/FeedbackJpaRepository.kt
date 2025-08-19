package com.draw.it.api.feedback.infra

import com.draw.it.api.feedback.domain.Feedback
import org.springframework.data.jpa.repository.JpaRepository

interface FeedbackJpaRepository : JpaRepository<Feedback, Long>