package com.tondo.api.controller

import com.tondo.api.dto.SignupRequest
import com.tondo.api.dto.SignupResponse
import com.tondo.api.service.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController (
    private val userService: UserService
) {
    @PostMapping("/signup")
    fun signup(@RequestBody request: SignupRequest): SignupResponse {
        val user = userService.getOrCreateUser(request)
        return SignupResponse(user.id, user.username)
    }
}