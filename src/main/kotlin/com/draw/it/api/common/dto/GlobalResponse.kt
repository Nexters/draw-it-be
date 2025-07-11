package com.draw.it.api.common.dto

data class GlobalResponse(
    val requestId: String,
    val requestTime: String,
    val success : Boolean,
    val data: Any? = null,
)
