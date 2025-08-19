package com.draw.it.api.feedback.infra

import com.draw.it.api.feedback.domain.Feedback
import com.draw.it.api.feedback.domain.FeedbackRepository
import org.springframework.stereotype.Repository

@Repository
class FeedbackRepositoryImpl(
    private val feedbackJpaRepository: FeedbackJpaRepository
) : FeedbackRepository {

    override fun save(feedback: Feedback): Feedback {
        return feedbackJpaRepository.save(feedback)
    }
}