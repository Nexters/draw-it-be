package com.draw.it.api.auth

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class FacebookAuthClient(
    @Value("\${oauth.facebook.client-id}")
    private val appId: String,
    @Value("\${oauth.facebook.client-secret}")
    private val appSecret: String,
    @Value("\${oauth.facebook.callback-uri}")
    private val callbackUrl: String,
    private val restTemplate: RestTemplate,
) {

    fun exchangeCodeForToken(code: String): String {
        val tokenUrl = "https://graph.facebook.com/v18.0/oauth/access_token"
        val params = mapOf(
            "client_id" to appId,
            "client_secret" to appSecret,
            "redirect_uri" to callbackUrl,
            "code" to code
        )

        val response = restTemplate.postForObject(tokenUrl, params, Map::class.java)
            ?: throw RuntimeException("Failed to exchange code for token")

        return response["access_token"] as? String
            ?: throw RuntimeException("Access token not found in response")
    }

    fun getUserInfo(accessToken: String): FacebookUserInfo {
        val userInfoUrl = "https://graph.facebook.com/me?fields=id,name&access_token=$accessToken"
        val response = restTemplate.getForObject(userInfoUrl, Map::class.java)
            ?: throw RuntimeException("Failed to fetch user info")

        return FacebookUserInfo(
            id = response["id"] as? String ?: throw RuntimeException("User ID not found"),
            name = response["name"] as? String ?: throw RuntimeException("User name not found")
        )
    }
}
