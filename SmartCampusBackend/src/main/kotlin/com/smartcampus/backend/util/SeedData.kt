package com.smartcampus.backend.util

import com.smartcampus.backend.models.*
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDateTime

object SeedData {

    fun seed() {
        transaction {
            // Only seed if no users exist
            if (Users.selectAll().count() > 0) return@transaction

            val now = LocalDateTime.now()

            // Create admin user
            Users.insert {
                it[name] = "Admin"
                it[email] = "admin@campus.edu"
                it[password] = BCrypt.hashpw("admin123", BCrypt.gensalt())
                it[enrollmentId] = "ADM001"
                it[role] = "ADMIN"
                it[isActive] = true
                it[createdAt] = now
                it[updatedAt] = now
            }

            // Create a placement officer
            Users.insert {
                it[name] = "Dr. Sharma"
                it[email] = "officer@campus.edu"
                it[password] = BCrypt.hashpw("officer123", BCrypt.gensalt())
                it[enrollmentId] = "OFF001"
                it[role] = "PLACEMENT_OFFICER"
                it[isActive] = true
                it[createdAt] = now
                it[updatedAt] = now
            }

            // Create sample student
            val studentId = Users.insert {
                it[name] = "Arun Student"
                it[email] = "student@campus.edu"
                it[password] = BCrypt.hashpw("student123", BCrypt.gensalt())
                it[enrollmentId] = "STU001"
                it[role] = "STUDENT"
                it[isActive] = true
                it[createdAt] = now
                it[updatedAt] = now
            } get Users.id

            // Student profile
            val profileId = StudentProfiles.insert {
                it[userId] = studentId
                it[semester] = 5
                it[branch] = "Computer Science"
                it[cgpa] = 8.5f
                it[preferredRegion] = "Bangalore"
                it[about] = "Passionate about software development"
                it[createdAt] = now
                it[updatedAt] = now
            } get StudentProfiles.id

            // Skills
            val skills = listOf(
                "Kotlin" to "Programming", "Java" to "Programming", "Python" to "Programming",
                "SQL" to "Database", "Android" to "Mobile", "Git" to "Tools"
            )
            skills.forEach { (name, category) ->
                val skillId = Skills.insert {
                    it[Skills.name] = name
                    it[Skills.category] = category
                } get Skills.id

                StudentSkills.insert {
                    it[studentProfileId] = profileId
                    it[StudentSkills.skillId] = skillId
                    it[proficiencyLevel] = "INTERMEDIATE"
                }
            }

            // Create recruiter
            Users.insert {
                it[name] = "TechCorp Recruiter"
                it[email] = "recruiter@techcorp.com"
                it[password] = BCrypt.hashpw("recruiter123", BCrypt.gensalt())
                it[role] = "RECRUITER"
                it[isActive] = true
                it[createdAt] = now
                it[updatedAt] = now
            }

            // Trending skills — curated fallback dataset (42 skills across categories)
            val trendingData = listOf(
                // AI/ML (High demand)
                Triple("TensorFlow", 95f, "Bangalore"),
                Triple("PyTorch", 93f, "Bangalore"),
                Triple("Machine Learning", 92f, "Hyderabad"),
                Triple("Deep Learning", 90f, "Bangalore"),
                Triple("LLMs", 89f, "Global"),
                Triple("LangChain", 87f, "Global"),
                Triple("Computer Vision", 85f, "Hyderabad"),
                Triple("NLP", 84f, "Pune"),
                // Web Frontend
                Triple("React", 96f, "Bangalore"),
                Triple("Next.js", 91f, "Mumbai"),
                Triple("TypeScript", 94f, "Pune"),
                Triple("Vue.js", 82f, "Delhi NCR"),
                Triple("Angular", 80f, "Chennai"),
                Triple("Svelte", 75f, "Global"),
                // Web Backend
                Triple("Node.js", 90f, "Mumbai"),
                Triple("Spring Boot", 86f, "Hyderabad"),
                Triple("FastAPI", 83f, "Pune"),
                Triple("Django", 79f, "Delhi NCR"),
                Triple("GraphQL", 78f, "Bangalore"),
                // Cloud / DevOps
                Triple("AWS", 95f, "Mumbai"),
                Triple("Docker", 93f, "Bangalore"),
                Triple("Kubernetes", 91f, "Pune"),
                Triple("Terraform", 85f, "Hyderabad"),
                Triple("Azure", 84f, "Delhi NCR"),
                Triple("GCP", 82f, "Chennai"),
                Triple("CI/CD", 80f, "Global"),
                // Mobile
                Triple("Flutter", 88f, "Bangalore"),
                Triple("Kotlin", 90f, "Pune"),
                Triple("React Native", 83f, "Mumbai"),
                Triple("Swift", 78f, "Global"),
                // Data Engineering
                Triple("Apache Spark", 86f, "Hyderabad"),
                Triple("Apache Kafka", 84f, "Mumbai"),
                Triple("PostgreSQL", 82f, "Pune"),
                Triple("MongoDB", 80f, "Delhi NCR"),
                Triple("Redis", 77f, "Chennai"),
                Triple("Airflow", 76f, "Bangalore"),
                // Security / Misc
                Triple("Cybersecurity", 88f, "Delhi NCR"),
                Triple("Blockchain", 72f, "Global"),
                Triple("Rust", 81f, "Global"),
                Triple("Go", 85f, "Bangalore"),
                Triple("Elasticsearch", 74f, "Chennai")
            )
            trendingData.forEach { (skill, score, region) ->
                TrendingSkills.insert {
                    it[skillName] = skill
                    it[demandScore] = score
                    it[TrendingSkills.region] = region
                    it[semester] = 5
                    it[branch] = "Computer Science"
                    it[dataSource] = "Job Portal Analysis"
                    it[scrapedAt] = now
                }
            }

            // Sample jobs
            val jobData = listOf(
                Triple("Android Developer Intern", "Google", "INTERNSHIP"),
                Triple("Backend Developer", "Microsoft", "FULL_TIME"),
                Triple("Full Stack Developer", "Amazon", "FULL_TIME"),
                Triple("ML Engineer Intern", "NVIDIA", "INTERNSHIP"),
                Triple("DevOps Engineer", "Flipkart", "FULL_TIME")
            )
            jobData.forEach { (title, company, type) ->
                Jobs.insert {
                    it[Jobs.title] = title
                    it[companyName] = company
                    it[location] = "Bangalore"
                    it[description] = "$title position at $company. Great opportunity for growth."
                    it[requiredSkills] = "Kotlin, Java, Android, Git"
                    it[salaryPackage] = if (type == "INTERNSHIP") "25,000/month" else "12-18 LPA"
                    it[jobType] = type
                    it[applicationDeadline] = now.plusDays(30)
                    it[postedBy] = 1
                    it[isActive] = true
                    it[createdAt] = now
                }
            }
        }
    }

    fun refreshTrendingSkills() {
        transaction {
            val now = LocalDateTime.now()

            TrendingSkills.deleteAll()

            // Curated dataset — 90+ skills across 15 IT regions
            val trendingData = listOf(
                // ===== BANGALORE (Silicon Valley of India) =====
                Triple("React", 96f, "Bangalore"),
                Triple("TensorFlow", 95f, "Bangalore"),
                Triple("Docker", 93f, "Bangalore"),
                Triple("Deep Learning", 90f, "Bangalore"),
                Triple("Flutter", 88f, "Bangalore"),
                Triple("Go", 85f, "Bangalore"),
                Triple("GraphQL", 78f, "Bangalore"),
                Triple("Airflow", 76f, "Bangalore"),
                // ===== MUMBAI (Financial Tech Hub) =====
                Triple("AWS", 95f, "Mumbai"),
                Triple("Next.js", 91f, "Mumbai"),
                Triple("Node.js", 90f, "Mumbai"),
                Triple("Apache Kafka", 84f, "Mumbai"),
                Triple("React Native", 83f, "Mumbai"),
                Triple("Blockchain", 80f, "Mumbai"),
                // ===== PUNE (IT Services Capital) =====
                Triple("TypeScript", 94f, "Pune"),
                Triple("Kubernetes", 91f, "Pune"),
                Triple("Kotlin", 90f, "Pune"),
                Triple("FastAPI", 83f, "Pune"),
                Triple("PostgreSQL", 82f, "Pune"),
                Triple("NLP", 84f, "Pune"),
                // ===== HYDERABAD (Emerging AI Hub) =====
                Triple("Machine Learning", 92f, "Hyderabad"),
                Triple("Spring Boot", 86f, "Hyderabad"),
                Triple("Apache Spark", 86f, "Hyderabad"),
                Triple("Terraform", 85f, "Hyderabad"),
                Triple("Computer Vision", 85f, "Hyderabad"),
                Triple("Python", 94f, "Hyderabad"),
                // ===== DELHI NCR (Enterprise & Govt Tech) =====
                Triple("Cybersecurity", 88f, "Delhi NCR"),
                Triple("Azure", 84f, "Delhi NCR"),
                Triple("Vue.js", 82f, "Delhi NCR"),
                Triple("MongoDB", 80f, "Delhi NCR"),
                Triple("Django", 79f, "Delhi NCR"),
                Triple("SAP", 77f, "Delhi NCR"),
                // ===== CHENNAI (Hardware + Software) =====
                Triple("Angular", 80f, "Chennai"),
                Triple("GCP", 82f, "Chennai"),
                Triple("Redis", 77f, "Chennai"),
                Triple("Elasticsearch", 74f, "Chennai"),
                Triple("Embedded C", 78f, "Chennai"),
                Triple("VLSI Design", 72f, "Chennai"),
                // ===== KOLKATA (Emerging IT City) =====
                Triple("Java", 88f, "Kolkata"),
                Triple("Data Analytics", 82f, "Kolkata"),
                Triple("Cloud Computing", 80f, "Kolkata"),
                Triple("PHP", 74f, "Kolkata"),
                Triple(".NET", 76f, "Kolkata"),
                // ===== AHMEDABAD (Growing Startup Scene) =====
                Triple(".NET", 80f, "Ahmedabad"),
                Triple("SAP", 78f, "Ahmedabad"),
                Triple("PHP", 76f, "Ahmedabad"),
                Triple("React", 84f, "Ahmedabad"),
                Triple("WordPress", 68f, "Ahmedabad"),
                // ===== JAIPUR (Outsourcing Hub) =====
                Triple("Python", 82f, "Jaipur"),
                Triple("WordPress", 75f, "Jaipur"),
                Triple("SEO", 70f, "Jaipur"),
                Triple("React", 80f, "Jaipur"),
                Triple("Node.js", 78f, "Jaipur"),
                // ===== KOCHI (Technopark Hub) =====
                Triple("Embedded Systems", 80f, "Kochi"),
                Triple("IoT", 78f, "Kochi"),
                Triple("Python", 84f, "Kochi"),
                Triple("Flutter", 76f, "Kochi"),
                Triple("Cloud Computing", 74f, "Kochi"),
                // ===== INDORE (Tier-2 Rising Star) =====
                Triple("Java", 84f, "Indore"),
                Triple("PHP", 78f, "Indore"),
                Triple("DevOps", 80f, "Indore"),
                Triple("React", 82f, "Indore"),
                Triple("Python", 80f, "Indore"),
                // ===== NOIDA (NCR IT Corridor) =====
                Triple("React", 90f, "Noida"),
                Triple("Java", 88f, "Noida"),
                Triple("SAP", 82f, "Noida"),
                Triple("AWS", 86f, "Noida"),
                Triple("Angular", 80f, "Noida"),
                // ===== COIMBATORE (Manufacturing + IT) =====
                Triple("Embedded Systems", 82f, "Coimbatore"),
                Triple("VLSI Design", 78f, "Coimbatore"),
                Triple("Python", 80f, "Coimbatore"),
                Triple("IoT", 76f, "Coimbatore"),
                Triple("Java", 78f, "Coimbatore"),
                // ===== THIRUVANANTHAPURAM (Technopark) =====
                Triple("Python", 86f, "Thiruvananthapuram"),
                Triple("Data Science", 84f, "Thiruvananthapuram"),
                Triple("Java", 82f, "Thiruvananthapuram"),
                Triple("Cloud Computing", 78f, "Thiruvananthapuram"),
                Triple("React", 80f, "Thiruvananthapuram"),
                // ===== CHANDIGARH (North India IT Hub) =====
                Triple(".NET", 80f, "Chandigarh"),
                Triple("Angular", 78f, "Chandigarh"),
                Triple("Cloud Computing", 76f, "Chandigarh"),
                Triple("Python", 82f, "Chandigarh"),
                Triple("PHP", 74f, "Chandigarh"),
                // ===== GLOBAL =====
                Triple("LLMs", 89f, "Global"),
                Triple("LangChain", 87f, "Global"),
                Triple("Svelte", 75f, "Global"),
                Triple("CI/CD", 80f, "Global"),
                Triple("Swift", 78f, "Global"),
                Triple("Rust", 81f, "Global"),
                Triple("PyTorch", 93f, "Global")
            )
            // Domain mapping for skills
            val domainMap = mapOf(
                "React" to "Web Development", "Angular" to "Web Development", "Vue.js" to "Web Development",
                "Node.js" to "Web Development", "Django" to "Web Development", "Spring Boot" to "Web Development",
                "TensorFlow" to "AI/ML", "PyTorch" to "AI/ML", "Machine Learning" to "AI/ML", "Deep Learning" to "AI/ML",
                "AWS" to "Cloud/DevOps", "Docker" to "Cloud/DevOps", "Kubernetes" to "Cloud/DevOps", "Azure" to "Cloud/DevOps",
                "Kotlin" to "Mobile App Dev", "Flutter" to "Mobile App Dev", "React Native" to "Mobile App Dev", "Swift" to "Mobile App Dev",
                "Python" to "Data Science", "SQL" to "Data Science", "PostgreSQL" to "Data Science", "Apache Spark" to "Data Science",
                "Java" to "Software Engineering", "C++" to "Software Engineering", "Go" to "Software Engineering", "Rust" to "Software Engineering"
            )

            trendingData.forEach { (skill, score, region) ->
                TrendingSkills.insert {
                    it[TrendingSkills.skillName] = skill
                    it[TrendingSkills.demandScore] = score
                    it[TrendingSkills.region] = region
                    it[TrendingSkills.semester] = (3..7).random() // Random ideal semester between 3 and 7
                    it[TrendingSkills.branch] = "Computer Science"
                    it[TrendingSkills.growthRate] = (5..30).random().toFloat() // Random growth rate 5-30%
                    it[TrendingSkills.domainCategory] = domainMap[skill] ?: "General"
                    it[TrendingSkills.dataSource] = "Job Portal Analysis"
                    it[TrendingSkills.scrapedAt] = now
                }
            }
        }
    }
}
