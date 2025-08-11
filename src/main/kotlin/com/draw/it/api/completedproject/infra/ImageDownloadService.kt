package com.draw.it.api.completedproject.infra

import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

@Service
class ImageDownloadService(
    private val restClient: RestClient
) {

    fun downloadImage(imageUrl: String): File {
        val tempFile = Files.createTempFile("downloaded_image", ".png").toFile()

        val inputStream = restClient
            .get()
            .uri(imageUrl)
            .retrieve()
            .body(ByteArray::class.java)
            ?.inputStream()
            ?: throw RuntimeException("이미지 다운로드 실패: $imageUrl")

        inputStream.use { input ->
            Files.copy(input, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }

        return tempFile
    }
}