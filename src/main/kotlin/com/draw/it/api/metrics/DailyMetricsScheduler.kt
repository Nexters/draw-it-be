package com.draw.it.api.metrics

import com.draw.it.api.external.notification.client.BizNotificationClient
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Profile("!local")
@Component
class DailyMetricsScheduler(
    private val dailyMetricsService: DailyMetricsService,
    private val bizNotificationClient: BizNotificationClient,
) {
    private val log = KotlinLogging.logger {}

    @Scheduled(cron = "0 0 0 * * ?")
    fun sendDailyMetrics() {
        try {
            val metrics = dailyMetricsService.getDailyMetrics()
            bizNotificationClient.sendDailyMetrics(metrics)
        } catch (e: Exception) {
            log.error(e) { "Failed to send daily metrics notification" }
        }
    }
}
