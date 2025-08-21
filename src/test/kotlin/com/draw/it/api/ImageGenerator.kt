package com.draw.it.api

import com.draw.it.api.completedproject.infra.ImageGenerator
import com.draw.it.common.IntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.io.File
import java.nio.file.Files
import java.util.*

@IntegrationTest
class ImageGeneratorTest {

    @Autowired
    private lateinit var imageGenerator: ImageGenerator

    @Test
    fun `그림 파일을 실사화 이미지로 변환한다`() {
        // Given - 테스트용 그림 파일 생성
        val drawingFile = createTestDrawingFile()

        // When - 그림을 실사화 이미지로 변환
        val base64ImageData = imageGenerator.convertDrawingToRealistic(drawingFile)

        // Then - Base64 데이터를 파일로 저장
        assert(base64ImageData.isNotEmpty())
        val savedImagePath = saveBase64ImageToFile(base64ImageData)
        assert(File(savedImagePath).exists())
        println("변환된 실사화 이미지 경로: $savedImagePath")
    }

    private fun createTestDrawingFile(): File {
        val imageResource = this::class.java.classLoader.getResource("image.png")
            ?: throw IllegalStateException("image.png 파일을 찾을 수 없습니다")

        return File(imageResource.toURI())
    }

    private fun saveBase64ImageToFile(base64Data: String): String {
        val imageBytes = Base64.getDecoder().decode(base64Data)
        val fileName = "realistic_image_${System.currentTimeMillis()}.png"
        val imageFile = File(fileName)

        Files.write(imageFile.toPath(), imageBytes)

        return imageFile.absolutePath
    }
}