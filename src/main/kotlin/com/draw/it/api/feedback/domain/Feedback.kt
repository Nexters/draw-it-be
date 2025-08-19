package com.draw.it.api.feedback.domain

import com.draw.it.api.common.entity.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.Comment

@Entity
@Table(name = "feedbacks")
@Comment("사용자 피드백 정보")
class Feedback(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("피드백 ID")
    val id: Long = 0,

    @Column(name = "rating", nullable = false)
    @Comment("평점 (1-5)")
    val rating: Int,

    @Column(name = "comment", length = 1000)
    @Comment("피드백 의견")
    val comment: String? = null,
) : BaseEntity() {

    init {
        require(rating in 1..5) { "Rating must be between 1 and 5" }
    }
}
