package com.smartcampus.backend.routes

import com.smartcampus.backend.dto.MessageResponse
import com.smartcampus.backend.service.AdminService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.adminRoutes() {
    val adminService = AdminService()

    authenticate("auth-jwt") {
        route("/admin") {
            // Get all users
            get("/users") {
                try {
                    val role = call.principal<JWTPrincipal>()!!.payload.getClaim("role").asString()
                    if (role != "ADMIN") {
                        call.respond(HttpStatusCode.Forbidden, MessageResponse("Admin access required", false))
                        return@get
                    }
                    val users = adminService.getAllUsers()
                    call.respond(HttpStatusCode.OK, users)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }

            // Get pending recruiters
            get("/pending-recruiters") {
                try {
                    val role = call.principal<JWTPrincipal>()!!.payload.getClaim("role").asString()
                    if (role != "ADMIN") {
                        call.respond(HttpStatusCode.Forbidden, MessageResponse("Admin access required", false))
                        return@get
                    }
                    val recruiters = adminService.getPendingRecruiters()
                    call.respond(HttpStatusCode.OK, recruiters)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }

            // Approve recruiter
            put("/approve/{recruiterId}") {
                try {
                    val role = call.principal<JWTPrincipal>()!!.payload.getClaim("role").asString()
                    if (role != "ADMIN") {
                        call.respond(HttpStatusCode.Forbidden, MessageResponse("Admin access required", false))
                        return@put
                    }
                    val recruiterId = call.parameters["recruiterId"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("Invalid recruiter ID")
                    val response = adminService.approveRecruiter(recruiterId)
                    call.respond(HttpStatusCode.OK, response)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }

            // Toggle user active status
            put("/users/{userId}/toggle") {
                try {
                    val role = call.principal<JWTPrincipal>()!!.payload.getClaim("role").asString()
                    if (role != "ADMIN") {
                        call.respond(HttpStatusCode.Forbidden, MessageResponse("Admin access required", false))
                        return@put
                    }
                    val userId = call.parameters["userId"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("Invalid user ID")
                    val activate = call.request.queryParameters["activate"]?.toBoolean() ?: true
                    val response = adminService.toggleUserStatus(userId, activate)
                    call.respond(HttpStatusCode.OK, response)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }

            // Get system stats
            get("/stats") {
                try {
                    val role = call.principal<JWTPrincipal>()!!.payload.getClaim("role").asString()
                    if (role != "ADMIN") {
                        call.respond(HttpStatusCode.Forbidden, MessageResponse("Admin access required", false))
                        return@get
                    }
                    val stats = adminService.getSystemStats()
                    call.respond(HttpStatusCode.OK, stats)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }
        }
    }
}
