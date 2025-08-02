package com.draw.it.api.completedproject.domain

import com.draw.it.api.common.entity.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.Comment

@Comment("Completed project")
@Entity
@Table(name = "completed_projects")
class CompletedProject(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Comment("프로젝트 ID")
    @Column(name = "project_id", nullable = false)
    val projectId: Long,

    @Comment("이미지 URL")
    @Column(name = "image_url", nullable = false)
    val imageUrl: String
) : BaseEntity() {
    companion object {
        fun create(
            projectId: Long,
            imageUrl: String
        ): CompletedProject {
            return CompletedProject(
                projectId = projectId,
                imageUrl = imageUrl
            )
        }
    }
}
