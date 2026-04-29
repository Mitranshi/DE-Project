# SmartCampusApp

Welcome to **SmartCampusApp**, an intelligent placement and career acceleration platform designed specifically to guide students through their university journey into successful industry placements.

## Table of Contents
- [Features](#features)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Contributing](#contributing)

## Features
- **Dynamic Career Roadmaps:** Interactive semester-by-semester roadmaps detailing essential skills, certifications, and project benchmarks to prepare for the industry.
- **Skill Tracking:** Visually track completed skills and view real-time market demand metrics directly on your dashboard.
- **Personalized Recommendations:** Intelligent guidance funneling students towards high-value, trending technologies like React, AWS, Docker, and Python.
- **Resume Builder:** An integrated resume generation tool right inside the Android application.
- **Career Hub:** Centralized hub highlighting external open jobs and campus placement listings.

## Architecture
The platform is broken into two isolated codebases existing within this repository:

1. **SmartCampusApp (Frontend)**: 
   - A Native Android client built securely using **Kotlin**.
   - Traditional XML layout system with modern **Material UI** components.
   - Leverages **Retrofit** and **Gson** for network requests to the backend API.

2. **SmartCampusBackend (Backend)**: 
   - A blazing-fast async Kotlin server engineered using the **Ktor** framework over Netty.
   - Secures traffic via **JWT Authentication** pipelines.
   - Connects to a relational **PostgreSQL** database using the **Jetbrains Exposed** ORM via HikariCP. 

## Getting Started

### Prerequisites
- JDK 17
- Android Studio 
- PostgreSQL

### 🖥️ Backend Setup (Ktor)
1. Navigate into `SmartCampusBackend/`
2. Review and update `src/main/resources/application.conf` with your PostgreSQL database credentials and JWT Secret.
3. Run the backend directly using the Gradle wrapper:
   ```bash
   ./gradlew run
   ```

### 📱 Frontend Setup (Android)
1. Open the `SmartCampusApp` folder in Android Studio.
2. Locate the network configurations string inside your Android application (usually inside `build.gradle` or API client configuration). Make sure it correctly flags the internal IP of the machine running your Ktor backend (e.g. `http://192.168.0.x:8080/api/`).
3. Click **Sync Project with Gradle Files**. 
4. Select your emulator/device and click **Run**.
