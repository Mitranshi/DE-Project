package com.smartcampus.backend

import com.smartcampus.backend.plugins.*
import com.smartcampus.backend.util.SeedData
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    configureSerialization()
    configureDatabase()
    SeedData.seed()
    SeedData.refreshTrendingSkills()
    configureSecurity()
    configureRouting()
}
