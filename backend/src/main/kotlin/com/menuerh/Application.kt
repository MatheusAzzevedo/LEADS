package com.menuerh

import com.menuerh.api.auth.AuthRoutes
import com.menuerh.api.dashboard.DashboardRoutes
import com.menuerh.api.leads.LeadRoutes
import com.menuerh.api.websocket.WebSocketRoutes
import com.menuerh.database.DatabaseConfig
import com.menuerh.middleware.AuthMiddleware
import io.ktor.http.HttpMethod
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = System.getenv("SERVER_PORT")?.toInt() ?: 8080, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    // Inicializar banco de dados
    DatabaseConfig.init()
    
    // Configurar serialização JSON
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    
    // Configurar CORS
    install(CORS) {
        allowHost("localhost:3000", schemes = listOf("http"))
        allowHost("127.0.0.1:3000", schemes = listOf("http"))
        allowHost("leads-production-9022.up.railway.app", schemes = listOf("https"))
        allowHeader(io.ktor.http.HttpHeaders.ContentType)
        allowHeader(io.ktor.http.HttpHeaders.Authorization)
        allowHeader("*")
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowCredentials = true
    }
    
    // Configurar rotas
    AuthRoutes()
    LeadRoutes()
    DashboardRoutes()
    WebSocketRoutes()
}
