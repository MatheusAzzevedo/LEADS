import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.http.*

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(CORS) {
            anyHost()
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Delete)
            allowHeader(HttpHeaders.Authorization)
            allowHeader(HttpHeaders.ContentType)
        }
        
        routing {
            get("/health") {
                call.respondText("OK", ContentType.Text.Plain)
            }
            
            get("/api/dashboard/stats") {
                call.respond(mapOf(
                    "totalLeads" to 0,
                    "leadsByPlan" to mapOf(
                        "basic" to 0,
                        "pro" to 0,
                        "enterprise" to 0
                    ),
                    "plans" to emptyList<Map<String, Any>>()
                ))
            }
            
            get("/api/leads/my") {
                call.respond(emptyList<Map<String, Any>>())
            }
            
            get("/api/plans") {
                call.respond(listOf(
                    mapOf(
                        "planId" to "basic",
                        "name" to "Básico",
                        "description" to "Plano básico",
                        "price" to 99.0,
                        "isActive" to true
                    ),
                    mapOf(
                        "planId" to "pro",
                        "name" to "Pro",
                        "description" to "Plano profissional",
                        "price" to 199.0,
                        "isActive" to true
                    ),
                    mapOf(
                        "planId" to "enterprise",
                        "name" to "Enterprise",
                        "description" to "Plano empresarial",
                        "price" to 399.0,
                        "isActive" to true
                    )
                ))
            }
            
            post("/api/auth/login") {
                call.respond(mapOf(
                    "token" to "test-token-123",
                    "user" to mapOf(
                        "id" to 1,
                        "username" to "operador1",
                        "name" to "Operador 1"
                    )
                ))
            }
            
            get("/api/auth/validate") {
                call.respond(mapOf("valid" to true))
            }
            
            post("/api/leads") {
                call.respond(mapOf(
                    "leadId" to "LEAD-${System.currentTimeMillis()}",
                    "operatorId" to 1,
                    "firstName" to "Teste",
                    "lastName" to "Lead",
                    "email" to "teste@teste.com",
                    "phone" to "(11) 99999-9999",
                    "position" to "Teste",
                    "company" to "Empresa Teste",
                    "selectedPlan" to "basic",
                    "createdAt" to "2024-01-15T10:00:00Z"
                ))
            }
        }
    }.start(wait = true)
} 