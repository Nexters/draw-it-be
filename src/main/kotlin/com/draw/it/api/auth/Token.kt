package com.draw.it.api.auth

import com.draw.it.api.common.entity.BaseEntity
import com.draw.it.api.user.User
import jakarta.persistence.*
import org.hibernate.annotations.Comment

@Comment("사용자 토큰 정보 테이블")
@Entity
@Table(name = "tokens")
class Token(
    @Comment("토큰 고유 ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Comment("사용자 ID (users 테이블 참조)")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Comment("액세스 토큰")
    @Column(name = "access_token", nullable = false)
    val accessToken: String
) : BaseEntity()
