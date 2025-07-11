package com.draw.it.api.external.notification.dto

enum class BizNotificationType(
    val title: String,
    val color: String
) {
    INFO("🔍 Info", "15871"),
    WARNING("⚠️ Warning", "16761344"),
    ERROR("🔥 Error", "16711680"),
}
