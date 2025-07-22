package com.menuerh.api.dashboard

import com.menuerh.api.leads.LeadService
import com.menuerh.api.plans.PlanService
import com.menuerh.middleware.AuthMiddleware
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*

fun Application.DashboardRoutes() {
    val leadService = LeadService(com.menuerh.api.leads.LeadRepository())
    val planService = PlanService()
    val dashboardService = DashboardService(leadService, planService)
    val dashboardController = DashboardController(dashboardService)
    val authMiddleware = AuthMiddleware()
    
    routing {
        route("/dashboard") {
            // Aplicar middleware de autenticação nas rotas de dashboard
            intercept(ApplicationCallPipeline.Call) {
                authMiddleware.intercept(this)
            }
            
            get("/stats") {
                dashboardController.getStats(call)
            }
        }
    }
} 