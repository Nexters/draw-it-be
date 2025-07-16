package com.draw.it.api.global.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class AppConfig(
    val objectMapper: ObjectMapper,
) {

    @PostConstruct
    fun setupObjectMapper() {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}
