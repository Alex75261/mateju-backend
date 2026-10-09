package com.mateju

import com.mateju.model.HealthResponse
import com.mateju.model.ApiError
import com.mateju.repository.JsonUserRepository
import com.mateju.repository.UserRepository
import com.mateju.routes.authRoutes
import com.mateju.service.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

private const val DEFAULT_PORT = 8080

fun main() {
    embeddedServer(
        factory = Netty,
        host = "0.0.0.0",
        port = System.getenv("PORT")?.toIntOrNull() ?: DEFAULT_PORT,
        module = { module() },
    ).start(wait = true)
}

/** Configures the Mateju API. */
fun Application.module(userRepository: UserRepository = JsonUserRepository()) {
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            },
        )
    }

    install(StatusPages) {
        exception<BadRequestException> { call, _ ->
            call.respond(
                HttpStatusCode.BadRequest,
                ApiError("El cuerpo debe contener un JSON válido."),
            )
        }
    }

    val authService = AuthService(userRepository)

    routing {
        get("/health") {
            call.respond(HttpStatusCode.OK, HealthResponse())
        }

        authRoutes(authService)
    }
}
