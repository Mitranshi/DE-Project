package com.smartcampus.backend.service

import com.smartcampus.backend.dto.*
import com.smartcampus.backend.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class ResumeService {

    fun saveResumeData(studentId: Int, request: ResumeDataRequest): MessageResponse {
        transaction {
            val existing = ResumeData.select { ResumeData.studentId eq studentId }.singleOrNull()

            if (existing != null) {
                ResumeData.update({ ResumeData.studentId eq studentId }) {
                    request.objective?.let { v -> it[objective] = v }
                    request.education?.let { v -> it[education] = v }
                    request.skills?.let { v -> it[skills] = v }
                    request.experience?.let { v -> it[experience] = v }
                    request.projects?.let { v -> it[projects] = v }
                    request.certifications?.let { v -> it[certifications] = v }
                    request.achievements?.let { v -> it[achievements] = v }
                    it[templateId] = request.templateId
                    it[updatedAt] = LocalDateTime.now()
                }
            } else {
                ResumeData.insert {
                    it[ResumeData.studentId] = studentId
                    it[objective] = request.objective
                    it[education] = request.education
                    it[skills] = request.skills
                    it[experience] = request.experience
                    it[projects] = request.projects
                    it[certifications] = request.certifications
                    it[achievements] = request.achievements
                    it[templateId] = request.templateId
                    it[updatedAt] = LocalDateTime.now()
                }
            }
        }
        return MessageResponse("Resume data saved successfully")
    }

    fun getResumeData(studentId: Int): ResumeDataRequest? {
        return transaction {
            ResumeData.select { ResumeData.studentId eq studentId }.singleOrNull()
                ?.let { row ->
                    ResumeDataRequest(
                        objective = row[ResumeData.objective],
                        education = row[ResumeData.education],
                        skills = row[ResumeData.skills],
                        experience = row[ResumeData.experience],
                        projects = row[ResumeData.projects],
                        certifications = row[ResumeData.certifications],
                        achievements = row[ResumeData.achievements],
                        templateId = row[ResumeData.templateId]
                    )
                }
        }
    }

    fun calculateAtsScore(studentId: Int, jobDescription: String): AtsScoreResponse {
        val resumeData = getResumeData(studentId)
            ?: throw IllegalArgumentException("No resume data found. Please create your resume first.")

        val resumeText = listOfNotNull(
            resumeData.objective,
            resumeData.education,
            resumeData.skills,
            resumeData.experience,
            resumeData.projects,
            resumeData.certifications,
            resumeData.achievements
        ).joinToString(" ").lowercase()

        val stopWords = setOf("and", "or", "the", "a", "an", "in", "on", "at", "to", "for", "of",
            "with", "is", "are", "was", "were", "be", "been", "being", "have", "has",
            "had", "do", "does", "did", "will", "would", "could", "should", "may", "might",
            "shall", "can", "need", "must", "we", "you", "they", "it", "he", "she",
            "this", "that", "these", "those", "i", "me", "my", "our", "your")

        val jobKeywords = jobDescription.lowercase()
            .replace(Regex("[^a-z0-9\\s+#.]"), " ")
            .split(Regex("\\s+"))
            .filter { w -> w.length > 2 && w !in stopWords }
            .distinct()

        val matchedKeywords = jobKeywords.filter { keyword ->
            resumeText.contains(keyword)
        }

        val missingKeywords = jobKeywords.filter { keyword ->
            !resumeText.contains(keyword)
        }

        val score = if (jobKeywords.isNotEmpty()) {
            ((matchedKeywords.size.toFloat() / jobKeywords.size) * 100).toInt()
        } else 0

        val suggestions = mutableListOf<String>()
        if (score < 50) suggestions.add("Add more relevant keywords from the job description")
        if (resumeData.skills.isNullOrBlank()) suggestions.add("Add a skills section to your resume")
        if (resumeData.experience.isNullOrBlank()) suggestions.add("Add work experience details")
        if (resumeData.projects.isNullOrBlank()) suggestions.add("Include relevant project descriptions")
        if (missingKeywords.size > 5) suggestions.add("Consider adding: ${missingKeywords.take(5).joinToString(", ")}")

        return AtsScoreResponse(
            score = score.coerceIn(0, 100),
            matchedKeywords = matchedKeywords,
            missingKeywords = missingKeywords.take(15),
            suggestions = suggestions
        )
    }
}
