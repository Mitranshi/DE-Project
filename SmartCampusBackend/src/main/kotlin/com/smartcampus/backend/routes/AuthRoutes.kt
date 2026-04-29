package com.smartcampus.backend.routes

import com.smartcampus.backend.dto.*
import com.smartcampus.backend.service.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes() {
    val authService = AuthService(
        jwtSecret = application.environment.config.property("jwt.secret").getString(),
        jwtIssuer = application.environment.config.property("jwt.issuer").getString(),
        jwtAudience = application.environment.config.property("jwt.audience").getString(),
        jwtExpirationMs = application.environment.config.property("jwt.expirationMs").getString().toLong()
    )

    route("/auth") {
        post("/register") {
            try {
                val request = call.receive<RegisterRequest>()
                val response = authService.register(request)
                call.respond(HttpStatusCode.Created, response)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Registration failed", false))
            }
        }

        post("/login") {
            try {
                val request = call.receive<LoginRequest>()
                val response = authService.login(request)
                call.respond(HttpStatusCode.OK, response)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.Unauthorized, MessageResponse(e.message ?: "Login failed", false))
            }
        }
    }
}
