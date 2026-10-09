package com.mateju.model

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    STUDENT,
    PROFESSOR,
    ADMIN,
}

/** Credentials supplied by the application login screen. */
@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
)

/** User record persisted in the local prototype data file. */
@Serializable
data class StoredUser(
    val id: String,
    val username: String,
    val password: String,
    val displayName: String,
    val role: UserRole,
)

/** Safe representation returned to the client after a successful login. */
@Serializable
data class LoginResponse(
    val id: String,
    val displayName: String,
    val role: UserRole,
)

@Serializable
data class ApiError(
    val message: String,
)

@Serializable
data class HealthResponse(
    val status: String = "ok",
)

