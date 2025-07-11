package com.draw.it.api.external.notification.client

import com.draw.it.api.external.notification.dto.BizNotificationType
import com.draw.it.api.external.notification.dto.DefaultNotificationMessage


interface BizNotificationClient {
    fun sendUsual(message: DefaultNotificationMessage, type: BizNotificationType)

    fun sendError(message: DefaultNotificationMessage)
}
