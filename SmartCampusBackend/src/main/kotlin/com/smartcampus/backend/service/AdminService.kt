package com.smartcampus.backend.service

import com.smartcampus.backend.dto.*
import com.smartcampus.backend.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.format.DateTimeFormatter

class AdminService {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun getAllUsers(): List<UserManagementResponse> {
        return transaction {
            Users.selectAll()
                .orderBy(Users.createdAt to SortOrder.DESC)
                .map { row ->
                    UserManagementResponse(
                        id = row[Users.id],
                        name = row[Users.name],
                        email = row[Users.email],
                        role = row[Users.role],
                        isActive = row[Users.isActive],
                        createdAt = row[Users.createdAt].format(formatter)
                    )
                }
        }
    }

    fun getPendingRecruiters(): List<UserManagementResponse> {
        return transaction {
            Users.select { (Users.role eq "RECRUITER") and (Users.isActive eq false) }
                .map { row ->
                    UserManagementResponse(
                        id = row[Users.id],
                        name = row[Users.name],
                        email = row[Users.email],
                        role = row[Users.role],
                        isActive = row[Users.isActive],
                        createdAt = row[Users.createdAt].format(formatter)
                    )
                }
        }
    }

    fun approveRecruiter(recruiterId: Int): MessageResponse {
        transaction {
            Users.update({ Users.id eq recruiterId }) {
                it[isActive] = true
            }
        }
        return MessageResponse("Recruiter approved successfully")
    }

    fun toggleUserStatus(userId: Int, activate: Boolean): MessageResponse {
        transaction {
            Users.update({ Users.id eq userId }) {
                it[isActive] = activate
            }
        }
        val status = if (activate) "activated" else "deactivated"
        return MessageResponse("User $status successfully")
    }

    fun getSystemStats(): Map<String, Any> {
        return transaction {
            mapOf(
                "totalUsers" to Users.selectAll().count(),
                "activeUsers" to Users.select { Users.isActive eq true }.count(),
                "totalStudents" to Users.select { Users.role eq "STUDENT" }.count(),
                "totalRecruiters" to Users.select { Users.role eq "RECRUITER" }.count(),
                "totalJobs" to Jobs.selectAll().count(),
                "totalApplications" to JobApplications.selectAll().count(),
                "totalDrives" to PlacementDrives.selectAll().count(),
                "trendingSkillsCount" to TrendingSkills.selectAll().count()
            )
        }
    }
}
