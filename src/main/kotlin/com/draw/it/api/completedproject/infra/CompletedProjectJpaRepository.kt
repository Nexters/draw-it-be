package com.draw.it.api.completedproject.infra

import com.draw.it.api.completedproject.domain.CompletedProject
import com.draw.it.api.completedproject.domain.CompletedProjectWithName
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CompletedProjectJpaRepository : JpaRepository<CompletedProject, Long> {
    @Query("""
        SELECT cp FROM CompletedProject cp 
        JOIN Project p ON cp.projectId = p.id 
        WHERE p.userId = :userId
        ORDER BY cp.createdAt DESC
    """)
    fun findByUserId(userId: Long): List<CompletedProject>
    
    @Query("""
        SELECT new com.draw.it.api.completedproject.domain.CompletedProjectWithName(
            cp.projectId, 
            cp.imageUrl, 
            p.topic, 
            cp.createdAt
        ) 
        FROM CompletedProject cp 
        JOIN Project p ON cp.projectId = p.id 
        WHERE p.userId = :userId
        ORDER BY cp.createdAt DESC
    """)
    fun findWithProjectNameByUserId(userId: Long): List<CompletedProjectWithName>
}
