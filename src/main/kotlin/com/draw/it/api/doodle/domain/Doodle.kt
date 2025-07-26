package com.draw.it.api.doodle.domain

import com.draw.it.api.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Comment("두들 정보 테이블")
@Entity
@Table(name = "doodles")
@SQLDelete(sql = "UPDATE doodles SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
class Doodle(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Comment("프로젝트 ID")
    @Column(name = "project_id", nullable = false)
    val projectId: Long,

    @Comment("두들 작성자 닉네임")
    @Column(name = "nickname", nullable = false)
    val nickname: String,

    @Comment("두들 편지 내용")
    @Column(name = "letter", columnDefinition = "TEXT")
    val letter: String?,

    @Comment("두들 이미지 URL")
    @Column(name = "image_url", nullable = false)
    val imageUrl: String,

    @Comment("사용자 확인 여부")
    @Column(name = "is_new_doodle_confirmed", nullable = false)
    val isNewDoodleConfirmed: Boolean = false,

    @Comment("두들 삭제 여부")
    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    var isDeleted: Boolean = false,
) : BaseEntity() {

    fun delete() {
        this.isDeleted = true
    }
}
