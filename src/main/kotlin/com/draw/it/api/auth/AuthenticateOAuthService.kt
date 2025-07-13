package com.draw.it.api.auth

import com.draw.it.api.user.CreateUserService
import com.draw.it.api.user.OAuth2Provider
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/auth")
class AuthenticateOAuthService(
    private val facebookAuthClient: FacebookAuthClient,
    private val createUserService: CreateUserService,
    private val objectMapper: ObjectMapper,
    private val tokenService: TokenService
) {

    @GetMapping("/facebook/callback")
    fun handleFacebookCallback(
        @RequestParam code: String
    ): RedirectView {
        val accessToken = facebookAuthClient.exchangeCodeForToken(code)
        val userInfo = facebookAuthClient.getUserInfo(accessToken)

        val userId = createUserService.getOrCreateUser(
            name = userInfo.name,
            provider = OAuth2Provider.FACEBOOK,
            providerId = userInfo.id
        )
        val jwtToken = tokenService.issue(userId, OAuth2Provider.FACEBOOK)

        return RedirectView("http://localhost:3000?data=${createResponse(jwtToken, userId)}")
    }

    private fun createResponse(jwtToken: String, userId: Long): String? {
        val data = mapOf(
            "success" to true,
            "token" to jwtToken,
            "userId" to userId,
        )
        val jsonString = objectMapper.writeValueAsString(data)
        val encodedData = URLEncoder.encode(jsonString, StandardCharsets.UTF_8)
        return encodedData
    }
}
