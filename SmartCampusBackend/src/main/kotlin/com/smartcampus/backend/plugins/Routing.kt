package com.smartcampus.backend.plugins

import com.smartcampus.backend.routes.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to (cause.message ?: "Unknown error"))
            )
        }
    }

    routing {
        // Health check
        get("/") {
            call.respond(mapOf("status" to "Smart Campus Backend is running!"))
        }

        // API routes
        route("/api") {
            authRoutes()
            studentRoutes()
            skillRoutes()
            jobRoutes()
            resumeRoutes()
            careerRoutes()
            officerRoutes()
            recruiterRoutes()
            notificationRoutes()
            adminRoutes()
        }
    }
}
