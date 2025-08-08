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
import io.ktor.server.response.*
import io.ktor.http.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
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
        allowHost("menurh-back-production.up.railway.app", schemes = listOf("https"))
        allowHost("*.up.railway.app", schemes = listOf("https"))
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
    
    // Servir SPA (Vite build) e fallback de rotas para evitar 404 em /login, /dashboard, etc.
    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, status ->
            val uri = call.request.uri
            val isGet = call.request.httpMethod == HttpMethod.Get
            val isApiOrStatic = uri.startsWith("/api") || uri.startsWith("/websocket") || uri.startsWith("/assets") || uri.startsWith("/auth")
            val isFileRequest = uri.endsWith(".js") || uri.endsWith(".css") || uri.endsWith(".map") || uri.endsWith(".svg") || uri.endsWith(".ico")

            if (isGet && !isApiOrStatic && !isFileRequest) {
                val indexStream = this@module::class.java.classLoader.getResourceAsStream("static/index.html")
                if (indexStream != null) {
                    val bytes = indexStream.readAllBytes()
                    call.respondBytes(bytes, ContentType.Text.Html)
                    return@status
                }
            }
            call.respond(status)
        }
    }

    // Rotas estáticas para arquivos do build (dist)
    routing {
        // Arquivos gerados pelo Vite: /assets/*, index.html, vite.svg, etc.
        staticResources("/assets", "static/assets")
        staticResources("/", "static")

        // Evitar 404 do favicon em alguns navegadores
        get("/favicon.ico") {
            val favicon = this@module::class.java.classLoader.getResourceAsStream("static/favicon.ico")
            if (favicon != null) {
                call.respondBytes(favicon.readAllBytes(), ContentType.parse("image/x-icon"))
            } else {
                // Tentar servir o vite.svg como fallback
                val svg = this@module::class.java.classLoader.getResourceAsStream("static/vite.svg")
                if (svg != null) {
                    call.respondBytes(svg.readAllBytes(), ContentType.parse("image/svg+xml"))
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
    
    // Configurar rotas da API
    AuthRoutes()
    LeadRoutes()
    DashboardRoutes()
    WebSocketRoutes()
}
