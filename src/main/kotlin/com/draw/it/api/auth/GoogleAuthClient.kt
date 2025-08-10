package com.draw.it.api.auth

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class GoogleAuthClient(
    @Value("\${oauth.google.client-id}")
    private val clientId: String,
    @Value("\${oauth.google.client-secret}")
    private val clientSecret: String,
    @Value("\${oauth.google.callback-uri}")
    private val redirectUri: String,
    private val restTemplate: RestTemplate,
) {

    fun exchangeCodeForToken(code: String): String {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val params: MultiValueMap<String, String> = LinkedMultiValueMap()
        params.add(GRANT_TYPE_KEY, AUTHORIZATION_CODE_GRANT_TYPE)
        params.add(CLIENT_ID_KEY, clientId)
        params.add(CLIENT_SECRET_KEY, clientSecret)
        params.add(REDIRECT_URI_KEY, redirectUri)
        params.add(CODE_KEY, code)

        val request = HttpEntity(params, headers)

        val response = restTemplate.postForObject(GOOGLE_TOKEN_URL, request, Map::class.java)
            ?: throw RuntimeException("Failed to exchange code for token")

        return response["access_token"] as? String
            ?: throw RuntimeException("Access token not found in response")
    }

    fun getUserInfo(accessToken: String): GoogleUserInfo {
        val headers = HttpHeaders()
        headers.setBearerAuth(accessToken)

        val request = HttpEntity<String>(headers)

        val response = restTemplate.exchange(
            GOOGLE_USER_INFO_URL,
            HttpMethod.GET,
            request,
            Map::class.java
        )

        val googleUserResponse = response.body
            ?: throw RuntimeException("Failed to fetch user info")

        return GoogleUserInfo(
            id = googleUserResponse["id"] as? String ?: throw RuntimeException("User ID not found"),
            name = googleUserResponse["name"] as? String ?: throw RuntimeException("User name not found")
        )
    }

    companion object {
        private const val GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token"
        private const val GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo"
        private const val AUTHORIZATION_CODE_GRANT_TYPE = "authorization_code"
        private const val GRANT_TYPE_KEY = "grant_type"
        private const val CLIENT_ID_KEY = "client_id"
        private const val CLIENT_SECRET_KEY = "client_secret"
        private const val REDIRECT_URI_KEY = "redirect_uri"
        private const val CODE_KEY = "code"
    }
}