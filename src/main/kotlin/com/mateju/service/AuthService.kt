package com.mateju.service

import com.mateju.model.LoginRequest
import com.mateju.model.LoginResponse
import com.mateju.repository.UserRepository

class AuthService(
    private val userRepository: UserRepository,
) {
    fun login(request: LoginRequest): LoginResponse? {
        val user = userRepository.findByUsername(request.username.trim())
            ?.takeIf { it.password == request.password }
            ?: return null

        return LoginResponse(
            id = user.id,
            displayName = user.displayName,
            role = user.role,
        )
    }
}

