package com.menuerh.api.dashboard

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

class DashboardController(private val dashboardService: DashboardService) {
    
    suspend fun getStats(call: ApplicationCall) {
        try {
            val stats = dashboardService.getDashboardStats()
            call.respond(HttpStatusCode.OK, stats)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Erro ao buscar estatísticas"))
        }
    }
}

@Serializable
data class ErrorResponse(val message: String) 