package com.tondo.api.repository

import com.tondo.api.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
     fun findByUsernameAndPhoneNumber(username: String, phoneNumber: String): User?
}