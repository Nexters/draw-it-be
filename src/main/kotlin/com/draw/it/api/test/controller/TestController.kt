package com.draw.it.api.test.controller

import com.draw.it.api.common.exception.BizException
import com.draw.it.api.common.exception.ErrorCode
import com.draw.it.api.test.dto.TestResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/test")
class TestController {
    @GetMapping("/hello")
    fun hello(): TestResponse {
        return TestResponse(
            message = "Hello, World!"
        )
    }

    @GetMapping("/error")
    fun error(): TestResponse {
        throw RuntimeException("Test error")
    }

    @GetMapping("/biz-error")
    fun bizError(): TestResponse {
        throw BizException(ErrorCode.EXTERNAL_SERVER_ERROR, "Test business error")
    }
}
