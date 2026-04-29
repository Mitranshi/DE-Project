package com.smartcampus.backend.routes

import com.smartcampus.backend.dto.MessageResponse
import com.smartcampus.backend.service.CareerService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.careerRoutes() {
    val careerService = CareerService()

    authenticate("auth-jwt") {
        route("/career") {
            // Get career roadmap
            get("/roadmap") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
                    val roadmap = careerService.getRoadmap(userId)
                    call.respond(HttpStatusCode.OK, roadmap)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }

            // Get progress
            get("/progress") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
                    val progress = careerService.getProgress(userId)
                    call.respond(HttpStatusCode.OK, progress)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }

            // Get dashboard analytics
            get("/analytics") {
                try {
                    val analytics = careerService.getDashboardAnalytics()
                    call.respond(HttpStatusCode.OK, analytics)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }
        }
    }
}
