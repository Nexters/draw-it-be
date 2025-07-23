package com.draw.it.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Health {

    @GetMapping("/health")
    fun health() {
        return
    }
}