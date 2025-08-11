package com.draw.it.api.common.exception

enum class ErrorCode(
    val code: Int,
    val message: String
) {
    UNAUTHORIZED(401, "인증되지 않은 사용자입니다"),
    FORBIDDEN(403, "권한이 없는 사용자입니다"),
    NOT_FOUND(404, "요청한 리소스를 찾을 수 없습니다"),
    BAD_REQUEST(400, "잘못된 요청입니다"),
    INTERNAL_SERVER_ERROR(500, "서버 에러입니다"),
    EXTERNAL_SERVER_ERROR(500, "외부 서버 에러입니다"),
}
