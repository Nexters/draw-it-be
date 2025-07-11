package com.draw.it.api.global.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun api(): OpenAPI {
        val api: SecurityScheme =
            SecurityScheme().type(
                SecurityScheme.Type.HTTP,
            ).`in`(SecurityScheme.In.HEADER).scheme(BEARER).bearerFormat(JWT)
        val securityRequirement = SecurityRequirement().addList(BEARER_TOKEN)

        return OpenAPI().components(Components().addSecuritySchemes(BEARER_TOKEN, api)).addSecurityItem(securityRequirement).info(apiInfo())
    }

    private fun apiInfo(): Info {
        return Info()
            .title("DrawIt API")
            .description("DrawIt API Documentation")
            .version("0.0.1")
    }

    companion object {
        private const val BEARER_TOKEN = "Bearer Token"
        private const val BEARER = "Bearer"
        private const val JWT = "JWT"
    }
}
