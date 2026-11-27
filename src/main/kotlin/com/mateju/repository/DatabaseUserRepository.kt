package com.mateju.repository

import com.mateju.model.StoredUser
import com.mateju.model.UserRole
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.net.URI

/** PostgreSQL implementation used automatically when Railway injects DATABASE_URL. */
class DatabaseUserRepository(databaseUrl: String) : UserRepository, AutoCloseable {
    private val database = databaseUrl.toPostgresConfig()
    private val dataSource = HikariDataSource(HikariConfig().apply {
        jdbcUrl = database.jdbcUrl
        username = database.username
        password = database.password
        maximumPoolSize = 5
    })

    init {
        dataSource.connection.use { connection ->
            connection.createStatement().use { statement ->
                statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS users (
                        id VARCHAR(64) PRIMARY KEY,
                        username VARCHAR(128) UNIQUE NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        display_name VARCHAR(255) NOT NULL,
                        role VARCHAR(32) NOT NULL
                    )
                """.trimIndent())
                statement.executeUpdate("""
                    INSERT INTO users (id, username, password, display_name, role) VALUES
                    ('stu-001', 'ana.estudiante', 'Mateju2026!', 'Ana López', 'STUDENT'),
                    ('pro-001', 'carlos.profesor', 'Mateju2026!', 'Carlos Ramírez', 'PROFESSOR'),
                    ('adm-001', 'admin.mateju', 'Mateju2026!', 'Administración Mateju', 'ADMIN')
                    ON CONFLICT (id) DO NOTHING
                """.trimIndent())
            }
        }
    }

    override fun findByUsername(username: String): StoredUser? =
        dataSource.connection.use { connection ->
            connection.prepareStatement(
                "SELECT id, username, password, display_name, role FROM users WHERE username = ?",
            ).use { statement ->
                statement.setString(1, username)
                statement.executeQuery().use { result ->
                    if (!result.next()) return@use null
                    StoredUser(
                        id = result.getString("id"),
                        username = result.getString("username"),
                        password = result.getString("password"),
                        displayName = result.getString("display_name"),
                        role = UserRole.valueOf(result.getString("role")),
                    )
                }
            }
        }

    override fun close() = dataSource.close()
}

private data class PostgresConfig(val jdbcUrl: String, val username: String?, val password: String?)

private fun String.toPostgresConfig(): PostgresConfig {
    if (startsWith("jdbc:")) return PostgresConfig(this, null, null)
    val uri = URI(if (startsWith("postgres://")) replaceFirst("postgres:", "postgresql:") else this)
    require(uri.scheme == "postgresql" && uri.host != null) {
        "DATABASE_URL debe usar el esquema postgres:// o postgresql://"
    }
    val credentials = uri.userInfo?.split(':', limit = 2)
    val port = if (uri.port == -1) 5432 else uri.port
    val query = uri.query?.let { "?$it" } ?: ""
    return PostgresConfig(
        jdbcUrl = "jdbc:postgresql://${uri.host}:$port${uri.path}$query",
        username = credentials?.getOrNull(0),
        password = credentials?.getOrNull(1),
    )
}
