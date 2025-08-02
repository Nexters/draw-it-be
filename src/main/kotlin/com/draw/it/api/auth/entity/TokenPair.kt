package com.draw.it.api.auth.entity

import com.draw.it.api.common.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "token_pairs",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_token_pairs_user_id", columnNames = ["user_id"])
    ]
)
class TokenPair(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "access_token", nullable = false, length = 1000)
    val accessToken: String,

    @Column(name = "refresh_token", nullable = false, length = 1000)
    val refreshToken: String,
) : BaseEntity()
