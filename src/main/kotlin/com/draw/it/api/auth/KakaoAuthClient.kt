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
) {
    private val restTemplate: RestTemplate = RestTemplate()

    fun exchangeCodeForToken(code: String): KakaoAccessTokenResponse {
        val tokenUrl = "https://kauth.kakao.com/oauth/token"
        
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        
        val params: MultiValueMap<String, String> = LinkedMultiValueMap()
        params.add("grant_type", "authorization_code")
        params.add("client_id", clientId)
        params.add("redirect_uri", redirectUri)
        params.add("code", code)
        
        val request = HttpEntity(params, headers)
        
        val response = restTemplate.postForObject(tokenUrl, request, KakaoAccessTokenResponse::class.java)
            ?: throw RuntimeException("Failed to exchange code for token")
        
        return response
    }

    fun getUserInfo(accessToken: String): KakaoUserInfo {
        val userInfoUrl = "https://kapi.kakao.com/v2/user/me"
        
        val headers = HttpHeaders()
        headers.setBearerAuth(accessToken)
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        
        val request = HttpEntity<String>(headers)
        
        val response = restTemplate.exchange(
            userInfoUrl,
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
}
