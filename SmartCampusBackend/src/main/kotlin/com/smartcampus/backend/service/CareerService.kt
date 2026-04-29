package com.smartcampus.backend.service

import com.smartcampus.backend.dto.*
import com.smartcampus.backend.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class CareerService {

    fun getRoadmap(userId: Int): List<CareerRoadmap> {
        val profile = transaction {
            StudentProfiles.select { StudentProfiles.userId eq userId }.singleOrNull()
        }

        val currentSemester = profile?.get(StudentProfiles.semester) ?: 1
        val branch = profile?.get(StudentProfiles.branch) ?: "Computer Science"

        return (currentSemester..8).map { sem ->
            CareerRoadmap(
                semester = sem,
                requiredSkills = getSkillsForSemester(sem, branch),
                suggestedInternships = getInternshipsForSemester(sem),
                suggestedCertifications = getCertificationsForSemester(sem, branch)
            )
        }
    }

    private fun getSkillsForSemester(semester: Int, branch: String): List<String> {
        return when (semester) {
            1, 2 -> listOf("C Programming", "Data Structures", "HTML/CSS", "Git")
            3, 4 -> listOf("Java", "DBMS", "Python", "OOP", "SQL")
            5, 6 -> listOf("Web Development", "Android/iOS", "Cloud Computing", "Machine Learning Basics")
            7, 8 -> listOf("System Design", "DevOps", "AI/ML Advanced", "Competitive Programming")
            else -> listOf("Continuous Learning")
        }
    }

    private fun getInternshipsForSemester(semester: Int): List<String> {
        return when (semester) {
            1, 2 -> listOf("Campus coding clubs", "Open source contributions")
            3, 4 -> listOf("Summer internship at startups", "Freelance projects")
            5, 6 -> listOf("Industry internship", "Research internship")
            7, 8 -> listOf("Pre-placement internship", "Full-time job preparation")
            else -> emptyList()
        }
    }

    private fun getCertificationsForSemester(semester: Int, branch: String): List<String> {
        return when (semester) {
            1, 2 -> listOf("Google IT Support", "HackerRank Problem Solving")
            3, 4 -> listOf("AWS Cloud Practitioner", "Oracle Java Certification")
            5, 6 -> listOf("Google Cloud Professional", "TensorFlow Developer Certificate")
            7, 8 -> listOf("AWS Solutions Architect", "Kubernetes Administrator")
            else -> emptyList()
        }
    }

    fun getProgress(userId: Int): ProgressResponse {
        return transaction {
            val profile = StudentProfiles.select { StudentProfiles.userId eq userId }.singleOrNull()

            val completedSkills = if (profile != null) {
                val profileId = profile[StudentProfiles.id]
                StudentSkills.select { StudentSkills.studentProfileId eq profileId }
                    .count().toInt()
            } else 0

            val appliedInternships = JobApplications.select { JobApplications.studentId eq userId }
                .count().toInt()

            val hasResume = ResumeData.select { ResumeData.studentId eq userId }.count() > 0

            val totalRecommended = 20
            val readiness = calculateReadiness(completedSkills, appliedInternships, hasResume)

            ProgressResponse(
                completedSkills = completedSkills,
                totalRecommendedSkills = totalRecommended,
                appliedInternships = appliedInternships,
                resumeSubmissions = if (hasResume) 1 else 0,
                placementReadinessPercentage = readiness,
                skillGrowth = listOf(completedSkills.toFloat())
            )
        }
    }

    private fun calculateReadiness(skills: Int, applications: Int, hasResume: Boolean): Float {
        var score = 0f
        score += (skills.coerceAtMost(10) / 10f) * 40
        score += (applications.coerceAtMost(5) / 5f) * 30
        if (hasResume) score += 20f
        score += 10f
        return score.coerceIn(0f, 100f)
    }

    fun getDashboardAnalytics(): DashboardAnalytics {
        return transaction {
            val totalStudents = Users.select { Users.role eq "STUDENT" }.count().toInt()
            val totalJobs = Jobs.select { Jobs.isActive eq true }.count().toInt()
            val totalApplications = JobApplications.selectAll().count().toInt()
            val placedStudents = JobApplications.select { JobApplications.status eq "SELECTED" }.count().toInt()

            val skillDemand = TrendingSkills.selectAll()
                .orderBy(TrendingSkills.demandScore to SortOrder.DESC)
                .limit(10)
                .associate { row -> row[TrendingSkills.skillName] to row[TrendingSkills.demandScore] }

            val successRate = if (totalApplications > 0) {
                (placedStudents.toFloat() / totalApplications) * 100
            } else 0f

            DashboardAnalytics(
                totalStudents = totalStudents,
                totalJobs = totalJobs,
                totalApplications = totalApplications,
                placedStudents = placedStudents,
                skillDemandData = skillDemand,
                applicationSuccessRate = successRate
            )
        }
    }
}
