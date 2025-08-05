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
import io.ktor.server.plugins.staticfiles.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.http.*
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
    
    // Configurar arquivos estáticos
    install(StaticFiles) {
        static("/") {
            files("static")
            default("index.html")
        }
    }
    
    // Configurar rotas
    routing {
        get("/") {
            call.respondText("""
                <!DOCTYPE html>
                <html>
                <head>
                    <title>MenuErh API</title>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; margin: 40px; background: #f5f5f5; }
                        .container { max-width: 800px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                        h1 { color: #333; text-align: center; }
                        .endpoint { background: #f8f9fa; padding: 15px; margin: 10px 0; border-radius: 5px; border-left: 4px solid #007bff; }
                        .method { color: #007bff; font-weight: bold; }
                        .status { color: #28a745; font-weight: bold; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>🚀 MenuErh API - Sistema de Gestão de Leads</h1>
                        <p class="status">✅ API funcionando corretamente!</p>
                        
                        <h2>📋 Endpoints Disponíveis:</h2>
                        
                        <div class="endpoint">
                            <span class="method">POST</span> <strong>/auth/login</strong><br>
                            Fazer login no sistema
                        </div>
                        
                        <div class="endpoint">
                            <span class="method">GET</span> <strong>/leads</strong><br>
                            Listar todos os leads (requer autenticação)
                        </div>
                        
                        <div class="endpoint">
                            <span class="method">POST</span> <strong>/leads</strong><br>
                            Criar novo lead (requer autenticação)
                        </div>
                        
                        <div class="endpoint">
                            <span class="method">GET</span> <strong>/dashboard/stats</strong><br>
                            Obter estatísticas do dashboard (requer autenticação)
                        </div>
                        
                        <div class="endpoint">
                            <span class="method">WS</span> <strong>/websocket</strong><br>
                            Conexão WebSocket para tempo real
                        </div>
                        
                        <h2>🔗 Links Úteis:</h2>
                        <p>
                            <a href="/dashboard/stats" target="_blank">Ver Estatísticas (JSON)</a><br>
                            <strong>Nota:</strong> Endpoints protegidos requerem token JWT no header Authorization
                        </p>
                        
                        <h2>📖 Documentação:</h2>
                        <p>Esta é uma API REST. Para usar a interface web completa, acesse o frontend React.</p>
                    </div>
                </body>
                </html>
            """.trimIndent(), ContentType.Text.Html)
        }
    }
    
    AuthRoutes()
    LeadRoutes()
    DashboardRoutes()
    WebSocketRoutes()
}
