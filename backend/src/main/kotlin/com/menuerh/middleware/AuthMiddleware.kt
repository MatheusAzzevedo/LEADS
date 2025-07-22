package com.menuerh.middleware

import com.auth0.jwt.JWT
import com.menuerh.database.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import io.ktor.util.AttributeKey

class AuthMiddleware {
    private val authService = AuthService()
    
    suspend fun intercept(context: PipelineContext<Unit, ApplicationCall>) {
        val call = context.call
        val token = call.request.header("Authorization")?.removePrefix("Bearer ")
        
        if (token == null) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Token não fornecido"))
            context.finish()
            return
        }
        
        try {
            val decodedJWT = JWT.decode(token)
            
            // Adicionar informações do usuário ao contexto
            call.attributes.put(OperatorIdKey, decodedJWT.subject.toInt())
            call.attributes.put(OperatorUsernameKey, decodedJWT.getClaim("username").asString())
            call.attributes.put(OperatorNameKey, decodedJWT.getClaim("name").asString())
            
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Token inválido"))
            context.finish()
        }
    }
}

fun Application.installAuthMiddleware() {
    val authMiddleware = AuthMiddleware()
    intercept(ApplicationCallPipeline.Call) {
        authMiddleware.intercept(this)
    }
}

val OperatorIdKey = AttributeKey<Int>("operatorId")
val OperatorUsernameKey = AttributeKey<String>("operatorUsername")
val OperatorNameKey = AttributeKey<String>("operatorName")

suspend fun ApplicationCall.getCurrentOperatorId(): Int {
    return attributes[OperatorIdKey]
}

suspend fun ApplicationCall.getCurrentOperatorUsername(): String {
    return attributes[OperatorUsernameKey]
}

suspend fun ApplicationCall.getCurrentOperatorName(): String {
    return attributes[OperatorNameKey]
} 