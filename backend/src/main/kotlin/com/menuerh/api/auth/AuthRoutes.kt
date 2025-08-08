package com.menuerh.api.auth

import com.menuerh.database.AuthService
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.AuthRoutes() {
    val authService = AuthService()
    val authController = AuthController(authService)
    
    routing {
        route("/api/auth") {
            post("/login") {
                authController.login(call)
            }
            
            get("/validate") {
                authController.validateToken(call)
            }
        }
    }
} 