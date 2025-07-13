package com.draw.it.api.auth.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/auth")
class AuthController {

    @GetMapping("/login/success")
    fun loginSuccess(@RequestParam token: String, model: Model): String {
        model.addAttribute("token", token)
        return "login-success"
    }

    @GetMapping("/login/failure")
    fun loginFailure(@RequestParam(required = false) error: String?, model: Model): String {
        model.addAttribute("error", error ?: "로그인에 실패했습니다.")
        return "login-failure"
    }
}