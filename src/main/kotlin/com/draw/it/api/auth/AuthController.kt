package com.draw.it.api.auth

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class FacebookAuthController(
    private val facebookAuthService: FacebookAuthService
) {

    @GetMapping("/facebook/callback")
    fun handleFacebookCallback(
        @RequestParam code: String
    ): ResponseEntity<Map<String, Any>> {
        val accessToken = facebookAuthService.exchangeCodeForToken(code)
        val userInfo = facebookAuthService.getUserInfo(accessToken)

        val jwtToken = "temp"

        return ResponseEntity.ok(
            mapOf(
                "success" to true,
                "token" to jwtToken,
                "user" to userInfo
            )
        )
    }

}
