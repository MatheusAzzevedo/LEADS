package com.menuerh.api.plans

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

class PlanController(private val planService: PlanService) {
    
    suspend fun getAllPlans(call: ApplicationCall) {
        try {
            println("DEBUG: PlanController.getAllPlans() chamado")
            val plans = planService.getAllPlans()
            println("DEBUG: Planos obtidos com sucesso: ${plans.size}")
            call.respond(HttpStatusCode.OK, plans)
        } catch (e: Exception) {
            println("DEBUG: Erro no PlanController: ${e.message}")
            e.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Erro ao buscar planos"))
        }
    }
}

@Serializable
data class ErrorResponse(val message: String) 