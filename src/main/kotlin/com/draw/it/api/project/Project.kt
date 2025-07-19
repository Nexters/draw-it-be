package com.draw.it.api.project

import com.draw.it.api.common.entity.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.Comment

@Comment("프로젝트")
@Entity
@Table(name = "projects")
class Project(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Comment("프로젝트 소유자 ID")
    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Comment("프로젝트 주제")
    @Column(name = "topic", nullable = false)
    val topic: String,

    @Comment("프로젝트 멘트")
    @Column(name = "message", nullable = false)
    val message: String,

    @Comment("배경 색상")
    @Column(name = "background_color", nullable = false)
    val backgroundColor: String,

    @Comment("삭제 여부")
    @Column(name = "is_deleted", nullable = false)
    val isDeleted: Boolean = false,

    @Comment("에디터 배치 상태 (JSON 형태 텍스트)")
    @Column(name = "editor_state", columnDefinition = "TEXT")
    val editorState: String? = null,

    @Comment("프로젝트 식별자 UUID")
    @Column(name = "uuid", nullable = false)
    val uuid: String
) : BaseEntity()