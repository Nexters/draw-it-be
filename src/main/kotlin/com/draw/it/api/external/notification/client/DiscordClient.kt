package com.draw.it.api.external.notification.client

import com.draw.it.api.external.notification.dto.*
import com.draw.it.api.external.notification.properties.BizNotificationProperties
import com.draw.it.api.metrics.DailyMetrics
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.text.NumberFormat
import java.util.*

private const val NOTIFICATION_EXECUTOR_NAME = "bizNotificationExecutor"

@Profile("!local")
@Component
class DiscordClient(
    val notificationProperties: BizNotificationProperties
): BizNotificationClient {
    private val log = KotlinLogging.logger {}
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

    @Async(NOTIFICATION_EXECUTOR_NAME)
    override fun sendDailyMetrics(metrics: DailyMetrics) {
        val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)
        
        val fields = mutableListOf(
            DiscordEmbeddedField(
                name = "📈 신규 생성",
                value = """
                    👥 사용자: **${numberFormat.format(metrics.newUsersToday)}명**
                    📝 프로젝트: **${numberFormat.format(metrics.newProjectsToday)}개**
                    🎨 낙서: **${numberFormat.format(metrics.newDoodlesToday)}개**
                    🖼️ 완성된 작품: **${numberFormat.format(metrics.newCompletedProjectsToday)}개**
                    💬 피드백: **${numberFormat.format(metrics.newFeedbacksToday)}개**
                    ⭐ 오늘 평균 평점: **${String.format("%.1f", metrics.todayAverageRating)}점**
                    
                """.trimIndent()
            ),
            DiscordEmbeddedField(
                name = "📊 전체 누적",
                value = """
                    👥 총 사용자: **${numberFormat.format(metrics.totalUsers)}명**
                    📝 총 프로젝트: **${numberFormat.format(metrics.totalProjects)}개**
                    🎨 총 낙서: **${numberFormat.format(metrics.totalDoodles)}개**
                    🖼️ 총 완성된 작품: **${numberFormat.format(metrics.totalCompletedProjects)}개**
                    💬 총 피드백: **${numberFormat.format(metrics.totalFeedbacks)}개**
                    ⭐ 전체 평균 평점: **${String.format("%.1f", metrics.averageRating)}점**
                """.trimIndent()
            )
        )
        
        if (metrics.todayFeedbackMessages.isNotEmpty()) {
            val feedbackMessages = metrics.todayFeedbackMessages.take(10).joinToString("\n") { "• $it" }
            fields.add(
                DiscordEmbeddedField(
                    name = "💬 오늘의 피드백 메시지",
                    value = if (metrics.todayFeedbackMessages.size > 10) {
                        "$feedbackMessages\n... 외 ${metrics.todayFeedbackMessages.size - 10}개"
                    } else {
                        feedbackMessages
                    }
                )
            )
        }
        
        val discordMessage = DiscordMessage(
            embeds = listOf(
                DiscordEmbeddedMessage(
                    title = "📊 Draw It 일일 통계 (${metrics.date})",
                    color = BizNotificationType.INFO.color,
                    fields = fields
                )
            )
        )
        
        try {
            restClient.post()
                .uri(notificationProperties.metricsMessage)
                .body(discordMessage)
                .retrieve()
                .body(Any::class.java)
        } catch (e: Exception) {
            log.error { "Failed to send Discord metrics notification: ${e.message}" }

        }
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
                        DiscordEmbeddedField(name = "Message", value = "◾️ ${message.message}")
                    )
                )
            )
        )
    }
}
