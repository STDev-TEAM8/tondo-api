package com.tondo.api.service

import com.tondo.api.domain.User
import com.tondo.api.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService (
    private val userRepository: UserRepository
) {
    // 체험 목적에 맞도록 Get-or-Create 방식으로 구현.
    fun getOrCreateUser(username: String, phoneNumber: String): User {
        return userRepository.findByUsernameAndPhoneNumber(username, phoneNumber)
            ?: userRepository.save(User(username = username, phoneNumber = phoneNumber))
    }
}