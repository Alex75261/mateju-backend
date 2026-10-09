package com.mateju.repository

import com.mateju.model.StoredUser
import kotlinx.serialization.json.Json

fun interface UserRepository {
    fun findByUsername(username: String): StoredUser?
}

/**
 * Reads the fixed demonstration accounts from a bundled JSON file. The file is
 * deliberately local-only for this first delivery; it is not a production
 * authentication store.
 */
class JsonUserRepository(
    private val resourceName: String = "users.json",
    private val json: Json = Json { ignoreUnknownKeys = true },
) : UserRepository {
    private val users: List<StoredUser> by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        val contents = checkNotNull(
            JsonUserRepository::class.java.classLoader.getResourceAsStream(resourceName),
        ) { "No se encontró el recurso de usuarios: $resourceName" }
            .bufferedReader()
            .use { it.readText() }

        json.decodeFromString(contents)
    }

    override fun findByUsername(username: String): StoredUser? =
        users.firstOrNull { it.username == username }
}

