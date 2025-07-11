package com.draw.it.api.common.extension

import com.draw.it.api.common.dto.RequestMetadata
import org.slf4j.MDC
import java.time.OffsetDateTime
import java.util.*

enum class LogType {
    REQUEST_ID,
    REQUEST_URI,
    REQUEST_TIME,
}

fun setRequestMetaData(uri: String) {
    MDC.put(LogType.REQUEST_ID.name, UUID.randomUUID().toString())
    MDC.put(LogType.REQUEST_TIME.name, OffsetDateTime.now().toString())
    MDC.put(LogType.REQUEST_URI.name, uri)
}

fun getRequestMetaData(): RequestMetadata {
    return RequestMetadata(
        requestId = getRequestId(),
        requestUri = getRequestUri(),
        requestTime = getRequestTime()
    )
}

fun getRequestId(): String = MDC.get(LogType.REQUEST_ID.name) ?: "알수없음"

fun getRequestUri(): String = MDC.get(LogType.REQUEST_URI.name) ?: "알수없음"

fun getRequestTime(): String = MDC.get(LogType.REQUEST_TIME.name) ?: "알수없음"
