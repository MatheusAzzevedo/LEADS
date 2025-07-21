package com.menuerh.api.auth

import com.menuerh.database.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

class AuthController(private val authService: AuthService) {
    
    suspend fun login(call: ApplicationCall) {
        try {
            val request = call.receive<LoginRequest>()
            
            val token = authService.authenticate(request.username, request.password)
            
            if (token != null) {
                call.respond(HttpStatusCode.OK, LoginResponse(token = token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Credenciais inválidas"))
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Erro no login: ${e.message}"))
        }
    }
    
    suspend fun validateToken(call: ApplicationCall) {
        try {
            val token = call.request.header("Authorization")?.removePrefix("Bearer ")
            
            if (token != null && authService.validateToken(token)) {
                val userData = authService.getUserFromToken(token)
                call.respond(HttpStatusCode.OK, mapOf(
                    "valid" to true,
                    "user" to userData
                ))
            } else {
                call.respond(HttpStatusCode.Unauthorized, mapOf("valid" to false))
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Token inválido"))
        }
    }
}

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class LoginResponse(val token: String)

@Serializable
data class ErrorResponse(val message: String) 