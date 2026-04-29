package com.smartcampus.backend.service

import com.smartcampus.backend.dto.SkillRecommendation
import com.smartcampus.backend.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class RecommendationService {

    fun getPersonalizedRecommendations(userId: Int): List<SkillRecommendation> {
        return transaction {
            // Step 1: Fetch Profile
            val profile = StudentProfiles.select { StudentProfiles.userId eq userId }.singleOrNull()
                ?: return@transaction emptyList()

            val studentSemester = profile[StudentProfiles.semester] ?: 1
            val studentLocation = profile[StudentProfiles.preferredRegion] ?: "Bangalore"
            val studentDomain = profile[StudentProfiles.domainInterest] ?: "Computer Science"

            // Step 2: Fetch Student Skills (name -> proficiency)
            val profileId = profile[StudentProfiles.id]
            val studentSkills = (StudentSkills innerJoin Skills)
                .select { StudentSkills.studentProfileId eq profileId }
                .associate { row ->
                    row[Skills.name].lowercase() to row[StudentSkills.proficiencyLevel]
                }

            // Step 3: Fetch Skill Database (TrendingSkills)
            // Group by skill name to get context for each unique skill
            val allTrending = TrendingSkills.selectAll().toList()
            val uniqueSkills = allTrending.groupBy { it[TrendingSkills.skillName] }

            val recommendations = uniqueSkills.map { (skillName, rows) ->
                // Derive metadata from the row matching student's region (if any), else the highest scoring one
                val relevantRow = rows.find { it[TrendingSkills.region] == studentLocation }
                    ?: rows.maxByOrNull { it[TrendingSkills.demandScore] }!!

                val idealSemester = relevantRow[TrendingSkills.semester] ?: 5
                val growthRate = relevantRow[TrendingSkills.growthRate]
                val domainCategory = relevantRow[TrendingSkills.domainCategory]

                // Step 3: RDS (Region Demand Score)
                // If skill demanded in studentLocation -> RDS = 1
                val isDemandedInRegion = rows.any { it[TrendingSkills.region] == studentLocation }
                val rds = if (isDemandedInRegion) 1.0f else 0.0f

                // Step 4: SRS (Semester Relevance Score)
                val srs = when (studentSemester) {
                    idealSemester -> 1.0f
                    idealSemester - 1 -> 0.5f
                    idealSemester + 1 -> 0.8f
                    else -> 0.2f
                }

                // Step 5: DMS (Domain Match Score)
                val dms = if (domainCategory.equals(studentDomain, ignoreCase = true)) 1.0f else 0.0f

                // Step 6: SGS (Skill Gap Score)
                val proficiency = studentSkills[skillName.lowercase()]
                val sgs = when {
                    proficiency == null -> 1.0f // Not in skills
                    proficiency == "BEGINNER" -> 0.7f // < 60%
                    else -> 0.0f // INTERMEDIATE (60-80) or ADVANCED (>80) - treated as no gap for high priority
                }

                // Step 7: IGS (Industry Growth Score)
                val igs = when {
                    growthRate > 20.0f -> 1.0f
                    growthRate > 10.0f -> 0.7f
                    else -> 0.4f
                }

                // Step 8: Compute Final Score
                // Formula: (2 * RDS) + (2 * SRS) + (1.5 * DMS) + (1.5 * SGS) + (1 * IGS)
                val finalScore = (2.0f * rds) + (2.0f * srs) + (1.5f * dms) + (1.5f * sgs) + (1.0f * igs)

                // Explanation
                val reasons = mutableListOf<String>()
                if (rds > 0) reasons.add("High demand in $studentLocation")
                if (srs >= 0.8) reasons.add("Ideal for Sem $studentSemester")
                if (dms > 0) reasons.add("Matches $studentDomain")
                if (igs >= 0.7) reasons.add("High Growth ($growthRate%)")
                if (sgs > 0) reasons.add("Skill Gap Identified")

                Triple(skillName, finalScore, reasons.joinToString(" • "))
            }

            // Step 10: Sort by FinalScore Descending
            recommendations.sortedByDescending { it.second }
                // Step 11: Filter
                // Exclude skills with proficiency > 85% (Advanced)
                .filter { (skill, _, _) ->
                    studentSkills[skill.lowercase()] != "ADVANCED"
                }
                // Step 12: Return Top 5
                .take(5)
                .map { (skill, score, reason) ->
                    SkillRecommendation(
                        skillName = skill,
                        demandScore = (score / 8.0f) * 100.0f, // Scale 0-8 to 0-100%
                        reason = reason,
                        region = studentLocation
                    )
                }
        }
    }
}
