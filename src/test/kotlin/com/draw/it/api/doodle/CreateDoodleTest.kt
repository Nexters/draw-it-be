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
            nickname = "테스트 닉네임",
            letter = "테스트 편지 내용",
            imageUrl = "https://example.com/mock-image-url/test-image.jpg"
        )
        
        every { projectRepository.findById(1L) } returns project
        every { imageStorageService.uploadImage(mockImage) } returns "https://example.com/mock-image-url/test-image.jpg"
        every { doodleRepository.save(any<Doodle>()) } returns savedDoodle

        // When
        val response = createDoodle.createDoodle(
            projectId = 1L,
            nickname = "테스트 닉네임",
            letter = "테스트 편지 내용",
            image = mockImage
        )

        // Then
        assertThat(response.id).isEqualTo(1L)
        assertThat(response.projectId).isEqualTo(1L)
        assertThat(response.nickname).isEqualTo("테스트 닉네임")
        assertThat(response.letter).isEqualTo("테스트 편지 내용")
        assertThat(response.imageUrl).isEqualTo("https://example.com/mock-image-url/test-image.jpg")
        assertThat(response.isNewDoodleConfirmed).isFalse()
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
            nickname = "테스트 닉네임",
            letter = null,
            imageUrl = "https://example.com/mock-image-url/test-image.jpg"
        )
        
        every { projectRepository.findById(1L) } returns project
        every { imageStorageService.uploadImage(mockImage) } returns "https://example.com/mock-image-url/test-image.jpg"
        every { doodleRepository.save(any<Doodle>()) } returns savedDoodle

        // When
        val response = createDoodle.createDoodle(
            projectId = 1L,
            nickname = "테스트 닉네임",
            letter = null,
            image = mockImage
        )

        // Then
        assertThat(response.id).isEqualTo(1L)
        assertThat(response.projectId).isEqualTo(1L)
        assertThat(response.nickname).isEqualTo("테스트 닉네임")
        assertThat(response.letter).isNull()
        assertThat(response.imageUrl).isEqualTo("https://example.com/mock-image-url/test-image.jpg")
        assertThat(response.isNewDoodleConfirmed).isFalse()
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
        
        every { projectRepository.findById(999L) } returns null

        // When & Then
        assertThatThrownBy {
            createDoodle.createDoodle(
                projectId = 999L,
                nickname = "테스트 닉네임",
                letter = "테스트 편지 내용",
                image = mockImage
            )
        }.isInstanceOf(BizException::class.java)
            .hasMessageContaining("존재하지 않는 프로젝트입니다")
    }
}