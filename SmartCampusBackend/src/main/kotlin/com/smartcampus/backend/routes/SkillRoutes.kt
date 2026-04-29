package com.smartcampus.backend.routes

import com.smartcampus.backend.dto.MessageResponse
import com.smartcampus.backend.models.RoadmapProgress
import com.smartcampus.backend.service.RoadmapService
import com.smartcampus.backend.service.SkillService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class ProgressRequest(val completedSteps: List<String>)

fun Route.skillRoutes() {
    val skillService = SkillService()

    authenticate("auth-jwt") {
        route("/skills") {
            // Get personalized recommendations (Algorithm)
            get("/recommend/personalized") {
                try {
                    val userId = call.principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
                        ?: throw IllegalArgumentException("Invalid token")
                    val recommendations = com.smartcampus.backend.service.RecommendationService().getPersonalizedRecommendations(userId)
                    call.respond(HttpStatusCode.OK, recommendations)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }

            // Get personalized recommendations (Legacy)
            get("/recommend") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
                    val recommendations = skillService.getRecommendations(userId)
                    call.respond(HttpStatusCode.OK, recommendations)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }

            // Get region-wise recommendations
            get("/recommend/region/{region}") {
                try {
                    val region = call.parameters["region"]
                        ?: throw IllegalArgumentException("Region required")
                    val recommendations = skillService.getRegionRecommendations(region)
                    call.respond(HttpStatusCode.OK, recommendations)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }

            // Get skill gap analysis
            get("/gap-analysis") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
                    val report = skillService.getSkillGapAnalysis(userId)
                    call.respond(HttpStatusCode.OK, report)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }

            // Get trending skills (live from GitHub + fallback)
            get("/trending") {
                try {
                    val trending = skillService.getTrendingSkills()
                    call.respond(HttpStatusCode.OK, trending)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }

            // Get region list with coordinates for map
            get("/regions") {
                try {
                    val regions = skillService.getRegionList()
                    call.respond(HttpStatusCode.OK, regions)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }

            // Get roadmap for a specific skill
            get("/roadmap/{skill}") {
                try {
                    val skill = call.parameters["skill"]
                        ?: throw IllegalArgumentException("Skill name required")
                    val roadmap = RoadmapService.getRoadmap(skill)
                    if (roadmap != null) {
                        call.respond(HttpStatusCode.OK, roadmap)
                    } else {
                        call.respond(HttpStatusCode.NotFound, MessageResponse("No roadmap found for '$skill'", false))
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }

            // Get user's progress for a skill roadmap
            get("/roadmap/{skill}/progress") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
                    val skill = call.parameters["skill"]
                        ?: throw IllegalArgumentException("Skill name required")

                    val completed = transaction {
                        RoadmapProgress.select {
                            (RoadmapProgress.userId eq userId) and (RoadmapProgress.skillName eq skill.lowercase())
                        }.map { it[RoadmapProgress.stepName] }
                    }
                    call.respond(HttpStatusCode.OK, completed)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }

            // Save/update progress for a skill roadmap
            post("/roadmap/{skill}/progress") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
                    val skill = call.parameters["skill"]
                        ?: throw IllegalArgumentException("Skill name required")
                    val request = call.receive<ProgressRequest>()

                    transaction {
                        // Clear old progress for this skill
                        RoadmapProgress.deleteWhere {
                            (RoadmapProgress.userId eq userId) and (RoadmapProgress.skillName eq skill.lowercase())
                        }
                        // Insert new progress
                        request.completedSteps.forEach { step ->
                            RoadmapProgress.insert {
                                it[RoadmapProgress.userId] = userId
                                it[RoadmapProgress.skillName] = skill.lowercase()
                                it[RoadmapProgress.stepName] = step
                            }
                        }
                    }
                    call.respond(HttpStatusCode.OK, MessageResponse("Progress saved", true))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }

            // Get all completed roadmaps for user
            get("/completed-roadmaps") {
                try {
                    val userId = call.principal<JWTPrincipal>()!!.payload.getClaim("userId").asInt()
                    val completedSkills = mutableListOf<String>()
                    
                    transaction {
                        val userProgress = RoadmapProgress.select { RoadmapProgress.userId eq userId }
                            .map { it[RoadmapProgress.skillName] to it[RoadmapProgress.stepName] }
                            .groupBy({ it.first }, { it.second })
                            
                        for ((skill, completedSteps) in userProgress) {
                            val roadmap = RoadmapService.getRoadmap(skill)
                            if (roadmap != null) {
                                val totalSteps = roadmap.phases.flatMap { it.steps }.size
                                if (completedSteps.size >= totalSteps) {
                                    completedSkills.add(skill)
                                }
                            }
                        }
                    }
                    call.respond(HttpStatusCode.OK, completedSkills)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, MessageResponse(e.message ?: "Error", false))
                }
            }
        }
    }
}
