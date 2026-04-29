package com.smartcampus.backend.routes

import com.smartcampus.backend.dto.MessageResponse
import com.smartcampus.backend.service.NotificationService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.notificationRoutes() {
    val notificationService = NotificationService()

    authenticate("auth-jwt") {
        route("/notifications") {
            // Get all notifications
            get {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
                    val notifications = notificationService.getNotifications(userId)
                    call.respond(HttpStatusCode.OK, notifications)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }

            // Get unread count
            get("/unread-count") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
                    val count = notificationService.getUnreadCount(userId)
                    call.respond(HttpStatusCode.OK, mapOf("count" to count))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }

            // Mark notification as read
            put("/{notificationId}/read") {
                try {
                    val notificationId = call.parameters["notificationId"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("Invalid notification ID")
                    val response = notificationService.markAsRead(notificationId)
                    call.respond(HttpStatusCode.OK, response)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }

            // Mark all as read
            put("/read-all") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
                    val response = notificationService.markAllAsRead(userId)
                    call.respond(HttpStatusCode.OK, response)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }
        }
    }
}
