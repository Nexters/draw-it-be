package com.draw.it.api.doodle.service

import org.springframework.web.multipart.MultipartFile

fun interface ImageStorageService {
    fun uploadImage(image: MultipartFile): String
}
