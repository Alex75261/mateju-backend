package com.mateju.routes

import com.mateju.model.ApiError
import com.mateju.model.LoginRequest
import com.mateju.service.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes(authService: AuthService) {
    route("/api/v1/auth") {
        post("/login") {
            val request = try {
                call.receive<LoginRequest>()
            } catch (_: ContentTransformationException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiError("El cuerpo debe contener un JSON válido."),
                )
                return@post
            }

            if (request.username.isBlank() || request.password.isBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiError("username y password son obligatorios."),
                )
                return@post
            }

            val response = authService.login(request)
            if (response == null) {
                call.respond(HttpStatusCode.Unauthorized, ApiError("Credenciales inválidas."))
                return@post
            }

            call.respond(HttpStatusCode.OK, response)
        }
    }
}

