package com.draw.it.api.user.domain

import java.time.LocalDate

interface UserRepository {
    fun save(user: User): User
    fun findByProviderAndProviderId(provider: OAuth2Provider, providerId: String): User?
    fun findById(id: Long): User?
    fun getBy(id: Long): User
    fun deleteById(id: Long)
    fun updateNameAndBirthDate(id: Long, name: String, birthDate: LocalDate?)
}