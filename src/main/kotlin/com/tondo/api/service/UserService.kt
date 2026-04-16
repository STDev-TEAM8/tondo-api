package com.tondo.api.service

import com.tondo.api.domain.User
import com.tondo.api.dto.SignupRequest
import com.tondo.api.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun getOrCreateUser(request: SignupRequest): User {
        if (request.password.length != 4) {
            throw IllegalArgumentException("비밀번호는 반드시 4글자여야 합니다.")
        }

        val encodedPassword = passwordEncoder.encode(request.password)

        return userRepository.save(User(username = request.username, password = encodedPassword))
    }
}