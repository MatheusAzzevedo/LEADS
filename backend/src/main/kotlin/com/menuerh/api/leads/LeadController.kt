package com.menuerh.api.leads

import com.menuerh.middleware.getCurrentOperatorId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

class LeadController(private val leadService: LeadService) {
    
    suspend fun createLead(call: ApplicationCall) {
        try {
            val request = call.receive<CreateLeadRequest>()
            val operatorId = call.getCurrentOperatorId()
            
            println("🔍 Debug - Dados recebidos do frontend:")
            println("  vagaPiloto: ${request.vagaPiloto}")
            println("  firstName: ${request.firstName}")
            println("  lastName: ${request.lastName}")
            println("  email: ${request.email}")
            
            val lead = leadService.createLead(request, operatorId)
            
            call.respond(HttpStatusCode.Created, lead)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Erro ao criar lead: ${e.message}"))
        }
    }
    
    suspend fun getAllLeads(call: ApplicationCall) {
        try {
            val leads = leadService.getAllLeads()
            call.respond(HttpStatusCode.OK, leads)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Erro ao buscar leads"))
        }
    }
    
    suspend fun getLeadById(call: ApplicationCall) {
        try {
            val leadId = call.parameters["id"] ?: return call.respond(HttpStatusCode.BadRequest)
            
            val lead = leadService.getLeadById(leadId)
            
            if (lead != null) {
                call.respond(HttpStatusCode.OK, lead)
            } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Lead não encontrado"))
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Erro ao buscar lead"))
        }
    }

    suspend fun deleteLead(call: ApplicationCall) {
        try {
            val leadId = call.parameters["id"] ?: return call.respond(HttpStatusCode.BadRequest)
            val deleted = leadService.deleteLeadById(leadId)
            if (deleted) {
                call.respond(HttpStatusCode.OK, mapOf("success" to true))
            } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Lead não encontrado"))
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Erro ao excluir lead: "+e.message))
        }
    }
}

@Serializable
data class CreateLeadRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val position: String,
    val company: String,
    val question1Responses: List<String>? = null,
    val question2Responses: List<String>? = null,
    val question3Responses: List<String>? = null,
    val question4Responses: List<String>? = null,
    val question5Text: String? = null,
    val vagaPiloto: Boolean? = false
)

@Serializable
data class ErrorResponse(val message: String)
