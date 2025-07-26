package com.draw.it.api.common.exception

open class BizException(
    val errorCode: ErrorCode,
    val log: String
) : RuntimeException() {
    override val message: String?
        get() = "[${errorCode.code}/${errorCode.message}] - $log"
}
