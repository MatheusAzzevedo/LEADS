# FASE 03 - BACKEND KTOR E APIs

## Objetivo
Implementar o backend Ktor com APIs REST para autenticação, cadastro de leads, perguntas, planos e dashboard.

## 3.1 Estrutura Modular por Domínio

```
menuerh-system/
├── app/
│   ├── api/                       # APIs Ktor
│   │   ├── auth/
│   │   │   └── route.kt          # Endpoint de autenticação
│   │   ├── leads/
│   │   │   └── route.kt          # Endpoint cadastro leads
│   │   ├── plans/
│   │   │   └── route.kt          # Endpoint planos
│   │   └── websocket/
│   │       └── route.kt          # WebSocket tempo real
│   │
│   ├── (auth)/                   # Páginas de autenticação (React)
│   │   ├── login/
│   │   │   ├── page.tsx
│   │   │   └── layout.tsx
│   │   └── register/
│   │       ├── page.tsx
│   │       └── layout.tsx
│   │
│   ├── dashboard/                # Dashboard (React)
│   │   ├── page.tsx
│   │   └── layout.tsx
│   │
│   ├── leads/                    # Cadastro de leads (React)
│   │   ├── page.tsx
│   │   └── layout.tsx
│   │
│   ├── plans/                    # Apresentação de planos (React)
│   │   ├── page.tsx
│   │   └── layout.tsx
│   │
│   ├── jobs/                     # Publicação de vagas (React)
│   │   ├── page.tsx
│   │   └── layout.tsx
│   │
│   └── layout.tsx                # Layout global
│
├── lib/                          # Utilitários e configurações
│   ├── auth.ts                   # Configuração de autenticação
│   ├── db.ts                     # Conexão Exposed
│   ├── utils.ts                  # Funções utilitárias
│   └── validations.ts            # Schemas de validação
│
├── database/                     # Modelos e configuração do banco
│   ├── models/
│   │   ├── Operator.kt
│   │   ├── Lead.kt
│   │   └── Plan.kt
│   ├── DatabaseConfig.kt
│   ├── migrations/
│   └── seeds/
│
├── components/                   # Componentes React
│   ├── ui/
│   ├── layout/
│   └── auth/
│
├── types/                        # Tipagens TypeScript
│   ├── auth.ts
│   └── lead.ts
│
├── build.gradle.kts              # Configuração Gradle (Ktor)
├── package.json                  # Configuração Node.js (React)
├── tailwind.config.js            # Configuração Tailwind
├── vite.config.ts                # Configuração Vite
└── middleware.ts                 # Middlewares globais
```

## 3.2 Application.kt - Configuração Principal

```kotlin
package com.menuerh

import com.menuerh.auth.AuthRoutes
import com.menuerh.dashboard.DashboardRoutes
import com.menuerh.leads.LeadRoutes
import com.menuerh.plans.PlanRoutes
import com.menuerh.realtime.WebSocketRoutes
import com.menuerh.shared.database.DatabaseConfig
import com.menuerh.shared.middleware.AuthMiddleware
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*

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
        json()
    }
    
    // Configurar CORS
    install(CORS) {
        anyHost()
        allowHeader("*")
        allowMethod("*")
    }
    
    // Configurar rotas
    routing {
        AuthRoutes(this@module)
        LeadRoutes(this@module)
        PlanRoutes(this@module)
        DashboardRoutes(this@module)
        WebSocketRoutes(this@module)
    }
}
```

## 3.3 Domínio de Autenticação

### AuthController.kt
```kotlin
package com.menuerh.auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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
                call.respond(HttpStatusCode.OK, mapOf("valid" to true))
            } else {
                call.respond(HttpStatusCode.Unauthorized, mapOf("valid" to false))
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Token inválido"))
        }
    }
}

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String)
data class ErrorResponse(val message: String)
```

### AuthRoutes.kt
```kotlin
package com.menuerh.auth

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.AuthRoutes() {
    val authService = AuthService()
    val authController = AuthController(authService)
    
    routing {
        route("/auth") {
            post("/login") {
                authController.login(this)
            }
            
            get("/validate") {
                authController.validateToken(this)
            }
        }
    }
}
```

## 3.4 Domínio de Leads

### LeadRepository.kt
```kotlin
package com.menuerh.leads

import com.menuerh.shared.database.dbQuery
import com.menuerh.shared.models.Leads
import com.menuerh.shared.models.Lead
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

class LeadRepository {
    
    suspend fun createLead(lead: Lead): Lead {
        return dbQuery {
            val id = Leads.insertAndGetId {
                it[leadId] = lead.leadId
                it[operatorId] = lead.operatorId
                it[firstName] = lead.firstName
                it[lastName] = lead.lastName
                it[email] = lead.email
                it[phone] = lead.phone
                it[position] = lead.position
                it[company] = lead.company
                it[question1Responses] = lead.question1Responses?.joinToString(",")
                it[question2Responses] = lead.question2Responses?.joinToString(",")
                it[question3Responses] = lead.question3Responses?.joinToString(",")
                it[question4Responses] = lead.question4Responses?.joinToString(",")
                it[question5Text] = lead.question5Text
                it[selectedPlan] = lead.selectedPlan
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
            
            lead.copy(id = id.value)
        }
    }
    
    suspend fun getAllLeads(): List<Lead> {
        return dbQuery {
            Leads.selectAll()
                .orderBy(Leads.createdAt, SortOrder.DESC)
                .map { rowToLead(it) }
        }
    }
    
    suspend fun getLeadById(leadId: String): Lead? {
        return dbQuery {
            Leads.select { Leads.leadId eq leadId }
                .singleOrNull()
                ?.let { rowToLead(it) }
        }
    }
    
    suspend fun getLeadsByOperator(operatorId: Int): List<Lead> {
        return dbQuery {
            Leads.select { Leads.operatorId eq operatorId }
                .orderBy(Leads.createdAt, SortOrder.DESC)
                .map { rowToLead(it) }
        }
    }
    
    private fun rowToLead(row: ResultRow): Lead {
        return Lead(
            id = row[Leads.id].value,
            leadId = row[Leads.leadId],
            operatorId = row[Leads.operatorId],
            firstName = row[Leads.firstName],
            lastName = row[Leads.lastName],
            email = row[Leads.email],
            phone = row[Leads.phone],
            position = row[Leads.position],
            company = row[Leads.company],
            question1Responses = row[Leads.question1Responses]?.split(","),
            question2Responses = row[Leads.question2Responses]?.split(","),
            question3Responses = row[Leads.question3Responses]?.split(","),
            question4Responses = row[Leads.question4Responses]?.split(","),
            question5Text = row[Leads.question5Text],
            selectedPlan = row[Leads.selectedPlan],
            createdAt = row[Leads.createdAt],
            updatedAt = row[Leads.updatedAt]
        )
    }
}
```

### LeadService.kt
```kotlin
package com.menuerh.leads

import com.menuerh.shared.models.Lead
import java.time.LocalDateTime

class LeadService(private val leadRepository: LeadRepository) {
    
    suspend fun createLead(leadData: CreateLeadRequest, operatorId: Int): Lead {
        val leadId = generateLeadId()
        
        val lead = Lead(
            leadId = leadId,
            operatorId = operatorId,
            firstName = leadData.firstName,
            lastName = leadData.lastName,
            email = leadData.email,
            phone = leadData.phone,
            position = leadData.position,
            company = leadData.company,
            question1Responses = leadData.question1Responses,
            question2Responses = leadData.question2Responses,
            question3Responses = leadData.question3Responses,
            question4Responses = leadData.question4Responses,
            question5Text = leadData.question5Text,
            selectedPlan = leadData.selectedPlan
        )
        
        return leadRepository.createLead(lead)
    }
    
    suspend fun getAllLeads(): List<Lead> {
        return leadRepository.getAllLeads()
    }
    
    suspend fun getLeadById(leadId: String): Lead? {
        return leadRepository.getLeadById(leadId)
    }
    
    suspend fun getLeadsByOperator(operatorId: Int): List<Lead> {
        return leadRepository.getLeadsByOperator(operatorId)
    }
    
    private fun generateLeadId(): String {
        return System.currentTimeMillis().toString()
    }
}

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
    val selectedPlan: String? = null
)
```

### LeadController.kt
```kotlin
package com.menuerh.leads

import com.menuerh.shared.middleware.getCurrentOperatorId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class LeadController(private val leadService: LeadService) {
    
    suspend fun createLead(call: ApplicationCall) {
        try {
            val request = call.receive<CreateLeadRequest>()
            val operatorId = call.getCurrentOperatorId()
            
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
    
    suspend fun getLeadsByOperator(call: ApplicationCall) {
        try {
            val operatorId = call.getCurrentOperatorId()
            val leads = leadService.getLeadsByOperator(operatorId)
            
            call.respond(HttpStatusCode.OK, leads)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Erro ao buscar leads do operador"))
        }
    }
}

data class ErrorResponse(val message: String)
```

### LeadRoutes.kt
```kotlin
package com.menuerh.leads

import com.menuerh.shared.middleware.AuthMiddleware
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.LeadRoutes() {
    val leadRepository = LeadRepository()
    val leadService = LeadService(leadRepository)
    val leadController = LeadController(leadService)
    
    routing {
        route("/leads") {
            install(AuthMiddleware) {
                route("/") {
                    post {
                        leadController.createLead(this)
                    }
                    
                    get {
                        leadController.getAllLeads(this)
                    }
                    
                    get("/my") {
                        leadController.getLeadsByOperator(this)
                    }
                    
                    get("/{id}") {
                        leadController.getLeadById(this)
                    }
                }
            }
        }
    }
}
```

## 3.5 Domínio de Planos

### PlanService.kt
```kotlin
package com.menuerh.plans

import com.menuerh.shared.database.dbQuery
import com.menuerh.shared.models.Plans
import com.menuerh.shared.models.Plan
import org.jetbrains.exposed.sql.selectAll

class PlanService {
    
    suspend fun getAllPlans(): List<Plan> {
        return dbQuery {
            Plans.selectAll()
                .where { Plans.isActive eq true }
                .map { rowToPlan(it) }
        }
    }
    
    private fun rowToPlan(row: org.jetbrains.exposed.sql.ResultRow): Plan {
        return Plan(
            id = row[Plans.id].value,
            planId = row[Plans.planId],
            name = row[Plans.name],
            description = row[Plans.description],
            imageUrl = row[Plans.imageUrl],
            isActive = row[Plans.isActive],
            createdAt = row[Plans.createdAt]
        )
    }
}
```

### PlanController.kt
```kotlin
package com.menuerh.plans

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class PlanController(private val planService: PlanService) {
    
    suspend fun getAllPlans(call: ApplicationCall) {
        try {
            val plans = planService.getAllPlans()
            call.respond(HttpStatusCode.OK, plans)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Erro ao buscar planos"))
        }
    }
}

data class ErrorResponse(val message: String)
```

### PlanRoutes.kt
```kotlin
package com.menuerh.plans

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.PlanRoutes() {
    val planService = PlanService()
    val planController = PlanController(planService)
    
    routing {
        route("/plans") {
            get {
                planController.getAllPlans(this)
            }
        }
    }
}
```

## 3.6 Domínio de Dashboard

### DashboardService.kt
```kotlin
package com.menuerh.dashboard

import com.menuerh.leads.LeadService
import com.menuerh.plans.PlanService

class DashboardService(
    private val leadService: LeadService,
    private val planService: PlanService
) {
    
    suspend fun getDashboardStats(): DashboardStats {
        val leads = leadService.getAllLeads()
        val plans = planService.getAllPlans()
        
        val totalLeads = leads.size
        val leadsByPlan = leads.groupBy { it.selectedPlan ?: "Nenhum" }
        
        return DashboardStats(
            totalLeads = totalLeads,
            leadsByPlan = leadsByPlan.mapValues { it.value.size },
            plans = plans
        )
    }
}

data class DashboardStats(
    val totalLeads: Int,
    val leadsByPlan: Map<String, Int>,
    val plans: List<com.menuerh.shared.models.Plan>
)
```

### DashboardController.kt
```kotlin
package com.menuerh.dashboard

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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

data class ErrorResponse(val message: String)
```

### DashboardRoutes.kt
```kotlin
package com.menuerh.dashboard

import com.menuerh.leads.LeadService
import com.menuerh.plans.PlanService
import com.menuerh.shared.middleware.AuthMiddleware
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.DashboardRoutes() {
    val leadService = LeadService(com.menuerh.leads.LeadRepository())
    val planService = PlanService()
    val dashboardService = DashboardService(leadService, planService)
    val dashboardController = DashboardController(dashboardService)
    
    routing {
        route("/dashboard") {
            install(AuthMiddleware) {
                get("/stats") {
                    dashboardController.getStats(this)
                }
            }
        }
    }
}
```

## 3.7 Middleware de Autenticação

### AuthMiddleware.kt
```kotlin
package com.menuerh.shared.middleware

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.menuerh.auth.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

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
            val verifier = authService.verifyToken(token)
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
```

## 3.8 WebSocket para Tempo Real

### WebSocketService.kt
```kotlin
package com.menuerh.realtime

import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedSendChannelException
import java.util.concurrent.ConcurrentHashMap

class WebSocketService {
    private val connections = ConcurrentHashMap<String, DefaultWebSocketSession>()
    
    suspend fun connect(sessionId: String, session: DefaultWebSocketSession) {
        connections[sessionId] = session
        
        try {
            for (frame in session.incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val text = frame.readText()
                        // Processar mensagem se necessário
                    }
                    is Frame.Close -> {
                        connections.remove(sessionId)
                        break
                    }
                }
            }
        } catch (e: ClosedSendChannelException) {
            connections.remove(sessionId)
        }
    }
    
    suspend fun broadcastLeadCreated(leadData: String) {
        connections.values.forEach { session ->
            try {
                session.send(Frame.Text("LEAD_CREATED:$leadData"))
            } catch (e: Exception) {
                // Ignorar erros de conexão fechada
            }
        }
    }
}
```

### WebSocketRoutes.kt
```kotlin
package com.menuerh.realtime

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.*

fun Application.WebSocketRoutes() {
    val webSocketService = WebSocketService()
    
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    
    routing {
        webSocket("/ws") {
            val sessionId = UUID.randomUUID().toString()
            webSocketService.connect(sessionId, this)
        }
    }
}
```

## 3.9 Configuração de Logging

### logback.xml
```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{YYYY-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```

## 3.10 Comandos de Teste

```bash
# Compilar e executar
./gradlew build
./gradlew run

# Testar APIs
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"operador1","password":"admin123"}'

curl -X GET http://localhost:8080/plans \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"

curl -X POST http://localhost:8080/leads \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{"firstName":"João","lastName":"Silva","email":"joao@email.com","phone":"11999999999","position":"Gerente","company":"Empresa ABC"}'
```

## 3.11 Próximos Passos
- [ ] Implementar validação com Zod
- [ ] Adicionar tratamento de erros global
- [ ] Implementar cache para planos
- [ ] Configurar CORS adequadamente
- [ ] Implementar rate limiting
- [ ] Adicionar logs estruturados

## Tempo Estimado: 6-8 horas 