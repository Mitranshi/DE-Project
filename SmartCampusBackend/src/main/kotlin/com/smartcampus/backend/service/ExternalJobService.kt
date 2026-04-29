package com.smartcampus.backend.service

import com.smartcampus.backend.dto.ExternalJobDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.statement.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class JSearchResponse(
    val status: String,
    val request_id: String,
    val data: List<JSearchJob>
)

@Serializable
data class JSearchJob(
    val job_id: String?,
    val employer_name: String?,
    val employer_logo: String?,
    val job_publisher: String?,
    val job_employment_type: String?,
    val job_title: String?,
    val job_apply_link: String?,
    val job_description: String?,
    val job_is_remote: Boolean?,
    val job_posted_at_timestamp: Long?,
    val job_posted_at_datetime_utc: String?,
    val job_city: String?,
    val job_state: String?,
    val job_country: String?,
    val job_google_link: String?,
    val job_offer_expiration_datetime_utc: String?,
    val job_offer_expiration_timestamp: Long?,
    val job_required_experience: JobRequiredExperience?,
    val job_required_skills: List<String>?,
    val job_required_education: JobRequiredEducation?,
    val job_experience_in_place_of_education: Boolean?,
    val job_min_salary: Double?,
    val job_max_salary: Double?,
    val job_salary_currency: String?,
    val job_salary_period: String?,
    val job_highlights: JobHighlights?,
    val job_job_title: String?,
    val job_posting_language: String?,
    val job_onet_soc: String?,
    val job_onet_job_zone: String?
)

@Serializable
data class JobRequiredExperience(
    val no_experience_required: Boolean,
    val required_experience_in_months: Int?,
    val experience_mentioned: Boolean,
    val experience_preferred: Boolean
)

@Serializable
data class JobRequiredEducation(
    val postgraduate_degree: Boolean,
    val professional_certification: Boolean,
    val high_school: Boolean,
    val associates_degree: Boolean,
    val bachelors_degree: Boolean,
    val degree_mentioned: Boolean,
    val degree_preferred: Boolean,
    val professional_certification_mentioned: Boolean
)

@Serializable
data class JobHighlights(
    val Qualifications: List<String>?,
    val Responsibilities: List<String>?,
    val Benefits: List<String>?
)

class ExternalJobService {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend fun searchJobs(query: String, numPages: String = "1", page: String = "1", datePosted: String = "all", host: String, key: String): List<ExternalJobDto> {
        println("ExternalJobService: Searching for '$query'...")
        return try {
            val responseObj: HttpResponse = client.get("https://jsearch.p.rapidapi.com/search") {
                parameter("query", query)
                parameter("page", page)
                parameter("num_pages", numPages)
                parameter("date_posted", datePosted)
                header("X-RapidAPI-Key", key)
                header("X-RapidAPI-Host", host)
            }
            
            val responseBody = responseObj.bodyAsText()
            println("ExternalJobService: Raw API Response: $responseBody")

            val response = Json { ignoreUnknownKeys = true }.decodeFromString<JSearchResponse>(responseBody)

            println("ExternalJobService: API Response received. Found ${response.data.size} jobs.")
            
            response.data.map { job ->
                ExternalJobDto(
                    jobId = job.job_id ?: "",
                    title = job.job_title ?: "Unknown Role",
                    companyName = job.employer_name ?: "Unknown Company",
                    companyLogo = job.employer_logo,
                    location = listOfNotNull(job.job_city, job.job_state, job.job_country).joinToString(", "),
                    description = job.job_description ?: "",
                    skills = job.job_required_skills ?: emptyList(),
                    apiSource = "JSearch (Verified)",
                    applyLink = job.job_apply_link,
                    isRemote = job.job_is_remote ?: false,
                    postedAt = job.job_posted_at_datetime_utc,
                    minSalary = job.job_min_salary,
                    maxSalary = job.job_max_salary,
                    salaryCurrency = job.job_salary_currency,
                    jobType = job.job_employment_type
                )
            }
        } catch (e: Exception) {
            println("ExternalJobService: Error fetching jobs: ${e.message}")
            println("ExternalJobService: Returning MOCK data for demonstration.")
            return getMockJobs()
        }
    }

    private fun getMockJobs(): List<ExternalJobDto> {
        return listOf(
            ExternalJobDto(
                jobId = "mock-1",
                title = "Android Developer",
                companyName = "Google",
                companyLogo = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/53/Google_%22G%22_Logo.svg/1200px-Google_%22G%22_Logo.svg.png",
                location = "Bangalore, India",
                description = "We are looking for an experienced Android Developer to join our team.",
                skills = listOf("Kotlin", "Java", "Android SDK"),
                apiSource = "Mock Data (API Quota Exceeded)",
                applyLink = "https://careers.google.com",
                isRemote = false,
                postedAt = "2024-02-19T10:00:00Z",
                minSalary = 2000000.0,
                maxSalary = 3500000.0,
                salaryCurrency = "INR",
                jobType = "FULL_TIME"
            ),
             ExternalJobDto(
                jobId = "mock-2",
                title = "Backend Engineer",
                companyName = "Microsoft",
                companyLogo = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/Microsoft_logo.svg/2048px-Microsoft_logo.svg.png",
                location = "Hyderabad, India",
                description = "Build scalable backend systems using Kotlin and Azure.",
                skills = listOf("Kotlin", "Azure", "Microservices"),
                apiSource = "Mock Data (API Quota Exceeded)",
                applyLink = "https://careers.microsoft.com",
                isRemote = true,
                postedAt = "2024-02-18T14:30:00Z",
                minSalary = 1800000.0,
                maxSalary = 3000000.0,
                salaryCurrency = "INR",
                jobType = "FULL_TIME"
            ),
            ExternalJobDto(
                jobId = "mock-3",
                title = "Frontend Intern",
                companyName = "Netflix",
                companyLogo = "https://upload.wikimedia.org/wikipedia/commons/0/08/Netflix_2015_logo.svg",
                location = "Remote",
                description = "Join our UI team to build the next generation of streaming interfaces.",
                skills = listOf("React", "TypeScript", "CSS"),
                apiSource = "Mock Data (API Quota Exceeded)",
                applyLink = "https://jobs.netflix.com",
                isRemote = true,
                postedAt = "2024-02-19T09:15:00Z",
                minSalary = 50000.0,
                maxSalary = 80000.0,
                salaryCurrency = "INR",
                jobType = "INTERNSHIP"
            )
        )
    }
}
