package com.draw.it.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Health", description = "헬스체크 API")
@RestController
class Health {

    @Operation(summary = "헬스체크", description = "서버 상태를 확인합니다")
    @ApiResponse(responseCode = "200", description = "서버 정상 상태")
    @GetMapping("/health")
    fun health() {
        return
    }
}