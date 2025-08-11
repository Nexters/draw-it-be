package com.draw.it.api.completedproject.infra

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import java.io.File

@Service
class ImageGenerator(
    private val restClient: RestClient,
    @Value("\${llm.openai.api-key}")
    private val apiKey: String,
) {

    fun convertDrawingToRealistic(drawingFile: File): String {
        val multipartData = LinkedMultiValueMap<String, Any>().apply {
            add("model", "gpt-image-1")
            add(
                "prompt",
                "Convert this drawing into a photorealistic image. Create a realistic version of what this drawing represents."
            )
            add("n", "1")
            add("size", "1024x1024")
            add("image", FileSystemResource(drawingFile))
        }

        val response = restClient
            .post()
            .uri("https://api.openai.com/v1/images/edits")
            .header("Authorization", "Bearer $apiKey")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(multipartData)
            .retrieve()
            .body(JsonNode::class.java)
            ?: throw RuntimeException("OpenAI API 응답이 비어있습니다")

        return response.get("data").get(0).get("url").asText()
    }

}