package com.draw.it.api.doodle

import com.draw.it.api.common.exception.BizException
import com.draw.it.api.doodle.domain.Doodle
import com.draw.it.api.doodle.domain.DoodleRepository
import com.draw.it.api.doodle.service.ImageStorageService
import com.draw.it.api.project.domain.Project
import com.draw.it.api.project.domain.ProjectRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import io.mockk.every
import io.mockk.mockk
import org.springframework.mock.web.MockMultipartFile

class CreateDoodleTest {
    private val doodleRepository = mockk<DoodleRepository>()
    private val projectRepository = mockk<ProjectRepository>()
    private val imageStorageService = mockk<ImageStorageService>()
    
    private lateinit var createDoodle: CreateDoodle
    
    @BeforeEach
    fun setUp() {
        createDoodle = CreateDoodle(
            doodleRepository = doodleRepository,
            projectRepository = projectRepository,
            imageStorageService = imageStorageService
        )
    }

    @Test
    fun `새로운 두들을 생성하고 저장한다`() {
        // Given
        val mockImage = MockMultipartFile(
            "image",
            "test-image.jpg",
            "image/jpeg",
            "test image content".toByteArray()
        )
        
        val project = Project(
            id = 1L,
            userId = 1L,
            topic = "테스트 주제",
            message = "테스트 메시지",
            backgroundColor = "#FFFFFF",
            uuid = "test-uuid"
        )
        
        val savedDoodle = Doodle(
            id = 1L,
            projectId = 1L,
            projectUuid = "test-uuid",
            nickname = "테스트 닉네임",
            letter = "테스트 편지 내용",
            imageUrl = "https://example.com/mock-image-url/test-image.jpg"
        )
        
        every { projectRepository.findByUuid("test-uuid") } returns project
        every { imageStorageService.uploadImage(mockImage) } returns "https://example.com/mock-image-url/test-image.jpg"
        every { doodleRepository.save(any<Doodle>()) } returns savedDoodle
        every { doodleRepository.findByProjectId(1L) } returns listOf(savedDoodle)

        // When
        val response = createDoodle.createDoodle(
            projectUuid = "test-uuid",
            nickname = "테스트 닉네임",
            letter = "테스트 편지 내용",
            image = mockImage
        )

        // Then
        assertThat(response.doodleId).isEqualTo(1L)
        assertThat(response.projectTopic).isEqualTo("테스트 주제")
        assertThat(response.doodleCount).isEqualTo(1)
        assertThat(response.myDoodleImageUrl).isEqualTo("https://example.com/mock-image-url/test-image.jpg")
        assertThat(response.otherDoodleImageUrls).isEmpty()
    }

    @Test
    fun `편지 내용 없이 두들을 생성한다`() {
        // Given
        val mockImage = MockMultipartFile(
            "image", 
            "test-image.jpg",
            "image/jpeg", 
            "test image content".toByteArray()
        )
        
        val project = Project(
            id = 1L,
            userId = 1L,
            topic = "테스트 주제",
            message = "테스트 메시지",
            backgroundColor = "#FFFFFF",
            uuid = "test-uuid"
        )
        
        val savedDoodle = Doodle(
            id = 1L,
            projectId = 1L,
            projectUuid = "test-uuid",
            nickname = "테스트 닉네임",
            letter = null,
            imageUrl = "https://example.com/mock-image-url/test-image.jpg"
        )
        
        every { projectRepository.findByUuid("test-uuid") } returns project
        every { imageStorageService.uploadImage(mockImage) } returns "https://example.com/mock-image-url/test-image.jpg"
        every { doodleRepository.save(any<Doodle>()) } returns savedDoodle
        every { doodleRepository.findByProjectId(1L) } returns listOf(savedDoodle)

        // When
        val response = createDoodle.createDoodle(
            projectUuid = "test-uuid",
            nickname = "테스트 닉네임",
            letter = null,
            image = mockImage
        )

        // Then
        assertThat(response.doodleId).isEqualTo(1L)
        assertThat(response.projectTopic).isEqualTo("테스트 주제")
        assertThat(response.doodleCount).isEqualTo(1)
        assertThat(response.myDoodleImageUrl).isEqualTo("https://example.com/mock-image-url/test-image.jpg")
        assertThat(response.otherDoodleImageUrls).isEmpty()
    }

    @Test
    fun `기존 두들이 있는 프로젝트에 새로운 두들을 생성한다`() {
        // Given
        val mockImage = MockMultipartFile(
            "image",
            "test-image.jpg",
            "image/jpeg",
            "test image content".toByteArray()
        )
        
        val project = Project(
            id = 1L,
            userId = 1L,
            topic = "테스트 주제",
            message = "테스트 메시지",
            backgroundColor = "#FFFFFF",
            uuid = "test-uuid"
        )
        
        val existingDoodle = Doodle(
            id = 2L,
            projectId = 1L,
            projectUuid = "test-uuid",
            nickname = "기존 닉네임",
            letter = "기존 편지",
            imageUrl = "https://example.com/existing-image.jpg"
        )
        
        val newDoodle = Doodle(
            id = 1L,
            projectId = 1L,
            projectUuid = "test-uuid",
            nickname = "새로운 닉네임",
            letter = "새로운 편지",
            imageUrl = "https://example.com/new-image.jpg"
        )
        
        every { projectRepository.findByUuid("test-uuid") } returns project
        every { imageStorageService.uploadImage(mockImage) } returns "https://example.com/new-image.jpg"
        every { doodleRepository.save(any<Doodle>()) } returns newDoodle
        every { doodleRepository.findByProjectId(1L) } returns listOf(existingDoodle, newDoodle)

        // When
        val response = createDoodle.createDoodle(
            projectUuid = "test-uuid",
            nickname = "새로운 닉네임",
            letter = "새로운 편지",
            image = mockImage
        )

        // Then
        assertThat(response.doodleId).isEqualTo(1L)
        assertThat(response.projectTopic).isEqualTo("테스트 주제")
        assertThat(response.doodleCount).isEqualTo(2)
        assertThat(response.myDoodleImageUrl).isEqualTo("https://example.com/new-image.jpg")
        assertThat(response.otherDoodleImageUrls).containsExactly("https://example.com/existing-image.jpg")
    }

    @Test
    fun `존재하지 않는 프로젝트로 두들 생성시 예외가 발생한다`() {
        // Given
        val mockImage = MockMultipartFile(
            "image",
            "test-image.jpg", 
            "image/jpeg",
            "test image content".toByteArray()
        )
        
        every { projectRepository.findByUuid("invalid-uuid") } returns null

        // When & Then
        assertThatThrownBy {
            createDoodle.createDoodle(
                projectUuid = "invalid-uuid",
                nickname = "테스트 닉네임",
                letter = "테스트 편지 내용",
                image = mockImage
            )
        }.isInstanceOf(BizException::class.java)
            .hasMessageContaining("존재하지 않는 프로젝트입니다")
    }
}