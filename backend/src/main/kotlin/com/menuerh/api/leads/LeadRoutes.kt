package com.menuerh.api.leads

import com.menuerh.middleware.AuthMiddleware
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*

fun Application.LeadRoutes() {
    val leadRepository = LeadRepository()
    val leadService = LeadService(leadRepository)
    val leadController = LeadController(leadService)
    val authMiddleware = AuthMiddleware()
    
    routing {
        route("/leads") {
            // Aplicar middleware de autenticação em todas as rotas de leads
            intercept(ApplicationCallPipeline.Call) {
                authMiddleware.intercept(this)
            }
            
            post {
                leadController.createLead(call)
            }
            
            get {
                leadController.getAllLeads(call)
            }
            
            get("/my") {
                leadController.getLeadsByOperator(call)
            }
            
            get("/{id}") {
                leadController.getLeadById(call)
            }
            delete("/{id}") {
                leadController.deleteLead(call)
            }
        }
    }
} 