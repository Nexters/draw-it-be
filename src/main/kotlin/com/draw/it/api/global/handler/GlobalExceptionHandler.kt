package com.draw.it.api.global.handler

import com.draw.it.api.common.dto.DefaultResponse
import com.draw.it.api.common.exception.BizException
import com.draw.it.api.common.extension.alertMessage
import com.draw.it.api.common.extension.getRequestId
import com.draw.it.api.common.extension.getRequestTime
import com.draw.it.api.common.extension.getRequestUri
import com.draw.it.api.external.notification.client.BizNotificationClient
import com.draw.it.api.external.notification.dto.BizNotificationType
import com.draw.it.api.external.notification.dto.DefaultNotificationMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.lang.Nullable
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice(basePackages = ["com.nyang8ja.api"])
class GlobalExceptionHandler(
    val bizNotificationClient: BizNotificationClient,
) : ResponseEntityExceptionHandler() {
    private val log = KotlinLogging.logger {}

    /**
     * Validation 예외
     */
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val errorMessage = ex.bindingResult
            .fieldErrors.joinToString("\n") {
                "${it.field} 필드 : ${it.defaultMessage}"
            }
        return ResponseEntity
            .status(400)
            .body(DefaultResponse(errorMessage))
    }

    /**
     * JSON 파싱 예외(ex. enum 값이 잘못 들어온 경우)
     */
    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val errorMessage = ex.message ?: "잘못된 요청입니다."
        return ResponseEntity
            .status(400)
            .body(DefaultResponse(errorMessage))
    }

    /**
     * 기타 Spring이 내부적으로 지원하는 예외
     * - 데이터 액세스, 빈 등과 관련된 예외
     */
    override fun handleExceptionInternal(
        ex: Exception,
        @Nullable body: Any?,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val errorMessage = ex.message ?: "잘못된 요청입니다."
        bizNotificationClient.sendError(createNotificationMessage(errorMessage))

        return ResponseEntity
            .status(500)
            .body(DefaultResponse(errorMessage))
    }

    /**
     * Biz 예외
     */
    @ExceptionHandler(BizException::class) // TODO 왜 이거 안잡히나 체크
    fun handleBizException(e: BizException, request: HttpServletRequest): ResponseEntity<DefaultResponse> {
        val errorMessage = e.alertMessage(e.errorCode.code, e.log)

        log.warn { "BizException occurred: ${e.errorCode.code} - $errorMessage" }
        bizNotificationClient.sendUsual(createNotificationMessage(errorMessage), BizNotificationType.INFO)

        return ResponseEntity
            .status(e.errorCode.code)
            .body(DefaultResponse(e.errorCode.message))
    }

    /**
     * 그 외 잡지 못한 예외
     */
    @ExceptionHandler(Exception::class)
    fun handleUncaughtException(e: Exception, request: HttpServletRequest): ResponseEntity<DefaultResponse> {
        val errorMessage = e.alertMessage(500)

        log.error { "Exception occurred: $errorMessage" }
        bizNotificationClient.sendError(createNotificationMessage(errorMessage))

        return ResponseEntity
            .status(500)
            .body(DefaultResponse(errorMessage))
    }

    private fun createNotificationMessage(message: String): DefaultNotificationMessage {
        return DefaultNotificationMessage(
            message = message,
            requestId = getRequestId(),
            requestTime = getRequestTime(),
            requestUri = getRequestUri()
        )
    }
}
