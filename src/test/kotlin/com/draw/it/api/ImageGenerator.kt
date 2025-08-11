package com.draw.it.api

import com.draw.it.api.completedproject.infra.ImageGenerator
import com.draw.it.common.IntegrationTest
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import java.io.File

@Disabled
@IntegrationTest
@TestPropertySource
class ImageGeneratorTest {

    @Autowired
    private lateinit var imageGenerator: ImageGenerator

    @Test
    fun `그림 파일을 실사화 이미지로 변환한다`() {
        // Given - 테스트용 그림 파일 생성
        val drawingFile = createTestDrawingFile()

        // When - 그림을 실사화 이미지로 변환
        val realisticImageUrl = imageGenerator.convertDrawingToRealistic(drawingFile)

        // Then - 변환된 이미지 URL이 반환되어야 함
        assert(realisticImageUrl.isNotEmpty())
        assert(realisticImageUrl.startsWith("https://"))
        println("변환된 실사화 이미지 URL: $realisticImageUrl")
    }

    private fun createTestDrawingFile(): File {
        val imageResource = this::class.java.classLoader.getResource("image.png")
            ?: throw IllegalStateException("image.png 파일을 찾을 수 없습니다")

        return File(imageResource.toURI())
    }
}