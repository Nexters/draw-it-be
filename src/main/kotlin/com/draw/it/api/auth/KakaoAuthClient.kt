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
class KakaoAuthClient(
    @Value("\${oauth.kakao.client-id}")
    private val clientId: String,
    @Value("\${oauth.kakao.callback-uri}")
    private val redirectUri: String,
    private val restTemplate: RestTemplate,
) {

    fun exchangeCodeForToken(code: String): KakaoAccessTokenResponse {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val params: MultiValueMap<String, String> = LinkedMultiValueMap()
        params.add(GRANT_TYPE_KEY, AUTHORIZATION_CODE_GRANT_TYPE)
        params.add(CLIENT_ID_KEY, clientId)
        params.add(REDIRECT_URI_KEY, redirectUri)
        params.add(CODE_KEY, code)

        val request = HttpEntity(params, headers)

        val response = restTemplate.postForObject(KAKAO_TOKEN_URL, request, KakaoAccessTokenResponse::class.java)
            ?: throw RuntimeException("Failed to exchange code for token")

        return response
    }

    fun getUserInfo(accessToken: String): KakaoUserInfo {
        val headers = HttpHeaders()
        headers.setBearerAuth(accessToken)
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val request = HttpEntity<String>(headers)

        val response = restTemplate.exchange(
            KAKAO_USER_INFO_URL,
            HttpMethod.GET,
            request,
            KakaoUserInfoResponse::class.java
        )

        val kakaoUserResponse = response.body
            ?: throw RuntimeException("Failed to fetch user info")

        return KakaoUserInfo(
            id = kakaoUserResponse.id.toString(),
            name = kakaoUserResponse.kakaoAccount.profile.nickname
        )
    }

    companion object {
        private const val KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token"
        private const val KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me"
        private const val AUTHORIZATION_CODE_GRANT_TYPE = "authorization_code"
        private const val GRANT_TYPE_KEY = "grant_type"
        private const val CLIENT_ID_KEY = "client_id"
        private const val REDIRECT_URI_KEY = "redirect_uri"
        private const val CODE_KEY = "code"
    }
}
