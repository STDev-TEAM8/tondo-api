package com.tondo.api.repository

import com.tondo.api.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
     fun findByUsernameAndPassword(username: String, password: String): User?
}