package com.draw.it.api.external.notification.client

import com.draw.it.api.external.notification.dto.*
import com.draw.it.api.external.notification.properties.BizNotificationProperties
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

private const val NOTIFICATION_EXECUTOR_NAME = "bizNotificationExecutor"

@Profile("!local")
@Component
class DiscordClient(
    val notificationProperties: BizNotificationProperties
): BizNotificationClient {
    private val restClient = RestClient.builder()
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build()

    @Async(NOTIFICATION_EXECUTOR_NAME)
    override fun sendUsual(message: DefaultNotificationMessage, type: BizNotificationType) {
        restClient.post()
            .uri(notificationProperties.etcMessage)
            .body(createDiscordRequest(message, type))
            .retrieve()
            .body(Any::class.java)
    }

    @Async(NOTIFICATION_EXECUTOR_NAME)
    override fun sendError(message: DefaultNotificationMessage) {
        restClient.post()
            .uri(notificationProperties.errorMessage)
            .body(createDiscordRequest(message, BizNotificationType.ERROR))
            .retrieve()
            .body(Any::class.java)
    }

    private fun createDiscordRequest(message: DefaultNotificationMessage, type: BizNotificationType): DiscordMessage {
        return DiscordMessage(
            embeds = listOf(
                DiscordEmbeddedMessage(
                    title = type.title,
                    color = type.color,
                    fields = listOf(
                        DiscordEmbeddedField(name = "Request Id", value = "◾️ ${message.requestId}"),
                        DiscordEmbeddedField(name = "Request Time", value = "◾️ ${message.requestTime}"),
                        DiscordEmbeddedField(name = "Request URI", value = "◾️ ${message.requestUri}"),
                        DiscordEmbeddedField(name = "Message", value = "◾️ ${message.message}",)
                    )
                )
            )
        )
    }
}
