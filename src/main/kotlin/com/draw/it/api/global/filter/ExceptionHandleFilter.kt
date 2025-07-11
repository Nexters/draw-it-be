package com.draw.it.api.global.filter

import com.draw.it.api.external.notification.client.BizNotificationClient
import com.draw.it.api.common.dto.DefaultResponse
import com.draw.it.api.common.dto.GlobalResponse
import com.draw.it.api.common.exception.BizException
import com.draw.it.api.common.extension.alertMessage
import com.draw.it.api.common.extension.getRequestId
import com.draw.it.api.common.extension.getRequestTime
import com.draw.it.api.common.extension.getRequestUri
import com.draw.it.api.external.notification.dto.BizNotificationType
import com.draw.it.api.external.notification.dto.DefaultNotificationMessage
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter

class ExceptionHandleFilter(
    private val objectMapper: ObjectMapper,
    private val bizNotificationClient: BizNotificationClient
): OncePerRequestFilter() {
    private val log = KotlinLogging.logger {}

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: BizException) {
            val message = e.alertMessage(e.errorCode.code, e.log)
            val globalResponse = createGlobalResponse(e.message)

            log.warn { "BizException occurred: ${e.errorCode.code} - ${e.message}" }
            bizNotificationClient.sendUsual(
                createNotificationMessage(message),
                BizNotificationType.INFO
            )

            response.status = e.errorCode.code
            response.writer.write(objectMapper.writeValueAsString(globalResponse))
        } catch (e: Exception) {
            val errorMessage = e.alertMessage(500)
            val globalResponse = createGlobalResponse(e.message)

            log.error { "Exception occurred: ${e.message}" }
            bizNotificationClient.sendError(
                createNotificationMessage(errorMessage)
            )

            response.status = 500
            response.writer.write(objectMapper.writeValueAsString(globalResponse))
        }
    }

    private fun createGlobalResponse(message: String?): GlobalResponse {
        return GlobalResponse(
            requestId = getRequestId(),
            requestTime = getRequestTime(),
            success = false,
            data = DefaultResponse(message ?: "메세지를 알 수 없는 에러가 발생했습니다.")
        )
    }

    private fun createNotificationMessage(message: String): DefaultNotificationMessage {
        return DefaultNotificationMessage(
            message = message,
            requestId = getRequestId(),
            requestTime = getRequestTime(),
            requestUri = getRequestUri(),
        )
    }
}
