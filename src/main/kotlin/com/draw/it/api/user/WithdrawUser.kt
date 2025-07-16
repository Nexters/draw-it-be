package com.draw.it.api.user

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class WithdrawUser {

    @DeleteMapping("/user/withdraw")
    fun withdrawUser() {
    }
}