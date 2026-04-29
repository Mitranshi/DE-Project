package com.smartcampus.backend.service

import kotlinx.serialization.Serializable

@Serializable
data class RoadmapPhase(
    val title: String,
    val steps: List<String>
)

@Serializable
data class SkillRoadmap(
    val skill: String,
    val description: String,
    val phases: List<RoadmapPhase>
)

object RoadmapService {

    private val roadmaps = mapOf(
        // ===== LANGUAGES =====
        "react" to SkillRoadmap(
            skill = "React",
            description = "Frontend library for building user interfaces",
            phases = listOf(
                RoadmapPhase("Fundamentals", listOf("HTML & CSS Basics", "JavaScript ES6+", "Git Version Control", "Package Managers (npm/yarn)")),
                RoadmapPhase("Core React", listOf("JSX Syntax", "Components & Props", "State & Lifecycle", "Hooks (useState, useEffect)", "Event Handling", "Conditional Rendering")),
                RoadmapPhase("Advanced Concepts", listOf("Context API", "useReducer & Custom Hooks", "React Router", "Error Boundaries", "React.memo & Performance")),
                RoadmapPhase("Ecosystem", listOf("State Management (Redux/Zustand)", "API Integration (Axios/Fetch)", "Form Libraries (React Hook Form)", "Styling (Styled Components/Tailwind)")),
                RoadmapPhase("Production", listOf("Testing (Jest + RTL)", "Next.js / SSR", "CI/CD Deployment", "Performance Monitoring"))
            )
        ),
        "python" to SkillRoadmap(
            skill = "Python",
            description = "Versatile programming language for web, data, and AI",
            phases = listOf(
                RoadmapPhase("Basics", listOf("Variables & Data Types", "Control Flow (if/for/while)", "Functions & Modules", "String Manipulation", "File I/O")),
                RoadmapPhase("Intermediate", listOf("OOP (Classes & Inheritance)", "Error Handling", "List Comprehensions", "Decorators & Generators", "Virtual Environments")),
                RoadmapPhase("Data & Libraries", listOf("NumPy & Pandas", "Matplotlib & Visualization", "Regular Expressions", "Working with APIs", "Database Access (SQLAlchemy)")),
                RoadmapPhase("Specialization", listOf("Web Dev (Django/FastAPI)", "Data Science & ML", "Automation & Scripting", "Testing (pytest)", "Package Publishing"))
            )
        ),
        "kotlin" to SkillRoadmap(
            skill = "Kotlin",
            description = "Modern language for Android and backend development",
            phases = listOf(
                RoadmapPhase("Basics", listOf("Variables (val/var)", "Data Types & Null Safety", "Control Flow", "Functions & Lambdas", "Collections")),
                RoadmapPhase("OOP & Advanced", listOf("Classes & Inheritance", "Data Classes & Sealed Classes", "Extension Functions", "Delegation", "Generics")),
                RoadmapPhase("Coroutines", listOf("Suspend Functions", "Coroutine Scope & Context", "Flow & Channels", "Error Handling in Coroutines", "Structured Concurrency")),
                RoadmapPhase("Android / Backend", listOf("Android Jetpack", "Compose UI", "Ktor / Spring Boot", "Room & Data Persistence", "Testing & CI/CD"))
            )
        ),
        "java" to SkillRoadmap(
            skill = "Java",
            description = "Enterprise-grade programming language",
            phases = listOf(
                RoadmapPhase("Fundamentals", listOf("Variables & Data Types", "Control Structures", "Arrays & Strings", "Methods & Scope", "OOP Basics")),
                RoadmapPhase("Core Java", listOf("Inheritance & Polymorphism", "Abstract Classes & Interfaces", "Exception Handling", "Collections Framework", "Generics")),
                RoadmapPhase("Advanced", listOf("Multithreading & Concurrency", "Stream API & Lambdas", "File I/O & NIO", "JDBC & Database Access", "Design Patterns")),
                RoadmapPhase("Ecosystem", listOf("Spring Boot", "Maven / Gradle", "RESTful APIs", "JUnit Testing", "Microservices"))
            )
        ),
        "typescript" to SkillRoadmap(
            skill = "TypeScript",
            description = "Typed superset of JavaScript for scalable apps",
            phases = listOf(
                RoadmapPhase("Prerequisites", listOf("JavaScript Fundamentals", "ES6+ Features", "Node.js Basics")),
                RoadmapPhase("Core Types", listOf("Basic Types & Interfaces", "Type Aliases & Unions", "Enums & Literal Types", "Type Assertions & Guards", "Generics")),
                RoadmapPhase("Advanced", listOf("Utility Types", "Mapped & Conditional Types", "Declaration Files (.d.ts)", "Module Systems", "Decorators")),
                RoadmapPhase("In Practice", listOf("TypeScript with React", "TypeScript with Node.js", "tsconfig Configuration", "Testing with Types", "Migration Strategies"))
            )
        ),
        // ===== FRAMEWORKS =====
        "flutter" to SkillRoadmap(
            skill = "Flutter",
            description = "Cross-platform mobile framework by Google",
            phases = listOf(
                RoadmapPhase("Setup & Dart", listOf("Dart Language Basics", "Flutter SDK Setup", "Widget Tree Concept", "Hot Reload & Dev Tools")),
                RoadmapPhase("UI Building", listOf("Stateless & Stateful Widgets", "Layout Widgets (Row/Column/Stack)", "Navigation & Routing", "Forms & Input Handling", "Theming & Custom Widgets")),
                RoadmapPhase("State & Data", listOf("setState & InheritedWidget", "Provider / Riverpod", "HTTP & REST APIs", "Local Storage (Hive/SharedPrefs)", "Firebase Integration")),
                RoadmapPhase("Production", listOf("Platform-specific Code", "Animations & Gestures", "Testing (Unit/Widget/Integration)", "App Store Deployment", "Performance Profiling"))
            )
        ),
        "angular" to SkillRoadmap(
            skill = "Angular",
            description = "Full-featured frontend framework by Google",
            phases = listOf(
                RoadmapPhase("Prerequisites", listOf("HTML/CSS/JavaScript", "TypeScript Basics", "Node.js & npm")),
                RoadmapPhase("Core Angular", listOf("Components & Templates", "Data Binding & Directives", "Services & DI", "Routing & Navigation", "Forms (Template/Reactive)")),
                RoadmapPhase("Advanced", listOf("RxJS & Observables", "HTTP Client & Interceptors", "Pipes & Custom Pipes", "Modules & Lazy Loading", "Guards & Resolvers")),
                RoadmapPhase("Production", listOf("State Management (NgRx)", "Unit Testing (Jasmine/Karma)", "E2E Testing (Cypress)", "Build & Deployment", "Performance Optimization"))
            )
        ),
        "spring boot" to SkillRoadmap(
            skill = "Spring Boot",
            description = "Java framework for enterprise backend applications",
            phases = listOf(
                RoadmapPhase("Prerequisites", listOf("Java Core & OOP", "Maven / Gradle", "SQL & Databases")),
                RoadmapPhase("Core Spring", listOf("Spring IoC & DI", "Spring Boot Auto-config", "REST Controllers", "Request Mapping & DTOs", "Properties & Profiles")),
                RoadmapPhase("Data & Security", listOf("Spring Data JPA", "Repository Pattern", "Spring Security", "JWT Authentication", "Validation & Error Handling")),
                RoadmapPhase("Production", listOf("Actuator & Monitoring", "Caching & Redis", "Messaging (Kafka/RabbitMQ)", "Docker & Kubernetes", "Microservices Patterns"))
            )
        ),
        // ===== CLOUD & DEVOPS =====
        "aws" to SkillRoadmap(
            skill = "AWS",
            description = "Amazon Web Services cloud platform",
            phases = listOf(
                RoadmapPhase("Fundamentals", listOf("Cloud Computing Concepts", "AWS Free Tier Setup", "IAM (Users, Roles, Policies)", "Regions & Availability Zones")),
                RoadmapPhase("Core Services", listOf("EC2 (Virtual Servers)", "S3 (Object Storage)", "RDS (Managed Databases)", "VPC (Networking)", "Lambda (Serverless)")),
                RoadmapPhase("Architecture", listOf("Load Balancing (ALB/NLB)", "Auto Scaling Groups", "CloudFront CDN", "SQS & SNS Messaging", "DynamoDB")),
                RoadmapPhase("DevOps & Advanced", listOf("CloudFormation / CDK", "CI/CD with CodePipeline", "ECS / EKS Containers", "CloudWatch Monitoring", "Cost Optimization"))
            )
        ),
        "docker" to SkillRoadmap(
            skill = "Docker",
            description = "Container platform for application deployment",
            phases = listOf(
                RoadmapPhase("Basics", listOf("What are Containers?", "Docker Installation", "Images vs Containers", "Dockerfile Basics", "Docker CLI Commands")),
                RoadmapPhase("Core Concepts", listOf("Building Custom Images", "Volumes & Persistence", "Networking (bridge/host)", "Environment Variables", "Docker Compose")),
                RoadmapPhase("Advanced", listOf("Multi-stage Builds", "Docker Registry (Hub/ECR)", "Resource Limits & Security", "Docker Swarm Basics", "Health Checks & Logging")),
                RoadmapPhase("Production", listOf("CI/CD with Docker", "Kubernetes Integration", "Monitoring Containers", "Docker in Microservices", "Best Practices & Security"))
            )
        ),
        "kubernetes" to SkillRoadmap(
            skill = "Kubernetes",
            description = "Container orchestration for scalable deployments",
            phases = listOf(
                RoadmapPhase("Prerequisites", listOf("Linux & CLI Basics", "Docker Fundamentals", "Networking Concepts", "YAML Syntax")),
                RoadmapPhase("Core Objects", listOf("Pods & ReplicaSets", "Deployments", "Services (ClusterIP/NodePort/LB)", "ConfigMaps & Secrets", "Namespaces")),
                RoadmapPhase("Advanced", listOf("Ingress Controllers", "Persistent Volumes & Claims", "StatefulSets & DaemonSets", "RBAC & Security", "Helm Charts")),
                RoadmapPhase("Operations", listOf("Monitoring (Prometheus/Grafana)", "Logging (EFK Stack)", "CI/CD Integration", "Cluster Autoscaling", "Disaster Recovery"))
            )
        ),
        // ===== AI/ML =====
        "machine learning" to SkillRoadmap(
            skill = "Machine Learning",
            description = "Building intelligent systems that learn from data",
            phases = listOf(
                RoadmapPhase("Math Foundation", listOf("Linear Algebra", "Probability & Statistics", "Calculus Basics", "Python for Data Science")),
                RoadmapPhase("Core ML", listOf("Supervised Learning", "Regression & Classification", "Decision Trees & Random Forests", "SVM & KNN", "Model Evaluation Metrics")),
                RoadmapPhase("Advanced", listOf("Unsupervised Learning (Clustering)", "Dimensionality Reduction (PCA)", "Ensemble Methods", "Feature Engineering", "Cross Validation & Tuning")),
                RoadmapPhase("Deep Learning", listOf("Neural Networks Basics", "CNNs for Computer Vision", "RNNs & LSTMs for NLP", "Transfer Learning", "Model Deployment (MLflow)"))
            )
        ),
        "tensorflow" to SkillRoadmap(
            skill = "TensorFlow",
            description = "Google's framework for machine learning",
            phases = listOf(
                RoadmapPhase("Prerequisites", listOf("Python & NumPy", "Linear Algebra Basics", "ML Fundamentals")),
                RoadmapPhase("Core TensorFlow", listOf("Tensors & Operations", "tf.data Pipeline", "Keras Sequential API", "Layers & Activation Functions", "Loss Functions & Optimizers")),
                RoadmapPhase("Model Building", listOf("CNNs (Image Classification)", "RNNs & LSTMs (Sequences)", "Transfer Learning (ResNet/VGG)", "Custom Training Loops", "Callbacks & Checkpoints")),
                RoadmapPhase("Production", listOf("TensorFlow Serving", "TF Lite for Mobile", "TensorBoard Visualization", "Model Optimization", "TFX Pipeline"))
            )
        ),
        // ===== DATA ENGINEERING =====
        "node.js" to SkillRoadmap(
            skill = "Node.js",
            description = "JavaScript runtime for server-side applications",
            phases = listOf(
                RoadmapPhase("Fundamentals", listOf("JavaScript Refresher", "Node.js Runtime & Event Loop", "Modules (CommonJS/ESM)", "npm & Package Management", "File System & Streams")),
                RoadmapPhase("Web Development", listOf("HTTP Module", "Express.js Framework", "Middleware & Routing", "Template Engines", "REST API Design")),
                RoadmapPhase("Data & Auth", listOf("MongoDB / PostgreSQL", "ORM (Prisma/Sequelize)", "Authentication (JWT/OAuth)", "Input Validation", "Error Handling Patterns")),
                RoadmapPhase("Advanced", listOf("WebSockets (Socket.io)", "Worker Threads & Clusters", "Caching (Redis)", "Testing (Jest/Mocha)", "Docker & Deployment"))
            )
        ),
        "mongodb" to SkillRoadmap(
            skill = "MongoDB",
            description = "NoSQL document database",
            phases = listOf(
                RoadmapPhase("Basics", listOf("NoSQL vs SQL", "MongoDB Installation", "Documents & Collections", "CRUD Operations", "MongoDB Shell (mongosh)")),
                RoadmapPhase("Querying", listOf("Query Operators", "Projection & Sorting", "Aggregation Pipeline", "Text Search & Indexes", "Regex Queries")),
                RoadmapPhase("Advanced", listOf("Schema Design Patterns", "Transactions & ACID", "Replication & Replica Sets", "Sharding for Scale", "Change Streams")),
                RoadmapPhase("In Practice", listOf("Mongoose ODM (Node.js)", "MongoDB Atlas (Cloud)", "Backup & Restore", "Performance Tuning", "Security & Access Control"))
            )
        ),
        "cybersecurity" to SkillRoadmap(
            skill = "Cybersecurity",
            description = "Protecting systems and networks from threats",
            phases = listOf(
                RoadmapPhase("Fundamentals", listOf("Networking Basics (TCP/IP)", "Operating System Concepts", "CIA Triad & Security Models", "Cryptography Basics")),
                RoadmapPhase("Security Domains", listOf("Network Security", "Web Application Security", "OWASP Top 10", "Identity & Access Management", "Incident Response")),
                RoadmapPhase("Offensive Security", listOf("Vulnerability Scanning", "Penetration Testing", "Social Engineering", "Exploitation Frameworks (Metasploit)", "CTF Challenges")),
                RoadmapPhase("Career Paths", listOf("SOC Analyst", "CompTIA Security+ Prep", "CEH / OSCP Prep", "Cloud Security Basics", "Bug Bounty Programs"))
            )
        )
    )

    fun getRoadmap(skillName: String): SkillRoadmap? {
        return roadmaps[skillName.lowercase()]
    }

    fun getAvailableRoadmaps(): List<String> {
        return roadmaps.values.map { it.skill }
    }
}
