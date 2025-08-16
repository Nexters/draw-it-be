package com.draw.it.api.external.notification.client

import com.draw.it.api.external.notification.dto.BizNotificationType
import com.draw.it.api.external.notification.dto.DefaultNotificationMessage
import com.draw.it.api.metrics.DailyMetrics


interface BizNotificationClient {
    fun sendUsual(message: DefaultNotificationMessage, type: BizNotificationType)

    fun sendError(message: DefaultNotificationMessage)
    
    fun sendDailyMetrics(metrics: DailyMetrics)
}
