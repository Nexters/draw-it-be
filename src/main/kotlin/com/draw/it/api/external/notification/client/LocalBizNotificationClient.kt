package com.draw.it.api.external.notification.client

import com.draw.it.api.external.notification.dto.BizNotificationType
import com.draw.it.api.external.notification.dto.DefaultNotificationMessage
import com.draw.it.api.metrics.DailyMetrics
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("local")
class LocalBizNotificationClient : BizNotificationClient {
    override fun sendUsual(
        message: DefaultNotificationMessage,
        type: BizNotificationType
    ) {
        println("Sending usual notification: $message with type $type")
    }

    override fun sendError(message: DefaultNotificationMessage) {
        println("Sending error notification: $message")
    }

    override fun sendDailyMetrics(metrics: DailyMetrics) {
        println("Sending daily metrics notification: $metrics")
    }
}
