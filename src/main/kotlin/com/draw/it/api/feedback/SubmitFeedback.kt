package com.draw.it.api.feedback

import com.draw.it.api.common.dto.DefaultResponse
import com.draw.it.api.feedback.domain.Feedback
import com.draw.it.api.feedback.domain.FeedbackRepository
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Feedback")
@RestController
class SubmitFeedback(
    private val feedbackRepository: FeedbackRepository
) {

    @PostMapping("/anonymous/feedback")
    fun submitFeedback(
        @Valid @RequestBody request: SubmitFeedbackRequest,
    ): DefaultResponse {
        val feedback = Feedback(
            rating = request.rating,
            comment = request.comment,
        )
        
        feedbackRepository.save(feedback)
        
        return DefaultResponse(
            message = "Feedback submitted successfully"
        )
    }
}

data class SubmitFeedbackRequest(
    @field:Min(1, message = "Rating must be at least 1")
    @field:Max(5, message = "Rating must be at most 5")
    val rating: Int,
    
    @field:Size(max = 1000, message = "Comment must not exceed 1000 characters")
    val comment: String? = null
)

data class SubmitFeedbackResponse(
    val feedbackId: Long
)
