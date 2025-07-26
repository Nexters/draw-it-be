package com.draw.it.api.doodle.service

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class MockImageStorageService : ImageStorageService {
    
    override fun uploadImage(image: MultipartFile): String {
        return "https://example.com/mock-image-url/${image.originalFilename}"
    }
}