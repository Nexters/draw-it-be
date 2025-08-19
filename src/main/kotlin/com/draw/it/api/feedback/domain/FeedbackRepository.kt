package com.draw.it.api.feedback.domain

interface FeedbackRepository {
    fun save(feedback: Feedback): Feedback
}