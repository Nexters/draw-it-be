package com.draw.it.api.user

import com.draw.it.api.common.entity.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.time.LocalDate

@Comment("사용자 정보 테이블")
@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Comment("사용자 이름")
    @Column(name = "name", nullable = false)
    val name: String,

    @Comment("사용자 생년월일")
    @Column(name = "birth_date")
    val birthDate: LocalDate? = null,

    @Comment("OAuth 제공자 타입 (KAKAO, FACEBOOK)")
    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    val provider: OAuth2Provider,

    @Comment("OAuth 제공자별 고유 ID")
    @Column(name = "provider_id", nullable = false)
    val providerId: String
) : BaseEntity()
