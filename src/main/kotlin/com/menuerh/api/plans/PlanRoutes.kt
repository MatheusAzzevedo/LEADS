package com.menuerh.api.plans

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.PlanRoutes() {
    val planService = PlanService()
    val planController = PlanController(planService)
    
    routing {
        route("/plans") {
            get {
                planController.getAllPlans(call)
            }
        }
    }
} 