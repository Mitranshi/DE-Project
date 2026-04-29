package com.smartcampus.backend.routes

import com.smartcampus.backend.dto.*
import com.smartcampus.backend.service.ResumeService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class AtsRequest(val jobDescription: String)

fun Route.resumeRoutes() {
    val resumeService = ResumeService()

    authenticate("auth-jwt") {
        route("/resume") {
            // Save resume data
            post {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
                    val request = call.receive<ResumeDataRequest>()
                    val response = resumeService.saveResumeData(userId, request)
                    call.respond(HttpStatusCode.OK, response)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }

            // Get resume data
            get {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
                    val data = resumeService.getResumeData(userId)
                    if (data != null) {
                        call.respond(HttpStatusCode.OK, data)
                    } else {
                        call.respond(HttpStatusCode.NotFound, MessageResponse("No resume data found", false))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }

            // Calculate ATS score
            post("/ats-score") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
                    val request = call.receive<AtsRequest>()
                    val score = resumeService.calculateAtsScore(userId, request.jobDescription)
                    call.respond(HttpStatusCode.OK, score)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }
        }
    }
}
