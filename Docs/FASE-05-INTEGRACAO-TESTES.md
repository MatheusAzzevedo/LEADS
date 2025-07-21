# FASE 05 - INTEGRAÇÃO E TESTES

## Objetivo
Integrar backend e frontend, implementar testes e validar todo o fluxo do sistema MenuErh.

## 5.1 Integração Backend-Frontend

### 5.1.1 Configuração de CORS no Backend

```kotlin
// Application.kt - Atualizar configuração CORS
install(CORS) {
    allowMethod(HttpMethod.Options)
    allowMethod(HttpMethod.Get)
    allowMethod(HttpMethod.Post)
    allowMethod(HttpMethod.Put)
    allowMethod(HttpMethod.Delete)
    allowHeader(HttpHeaders.Authorization)
    allowHeader(HttpHeaders.ContentType)
    allowCredentials = true
    anyHost()
}
```

### 5.1.2 Proxy no Frontend

```typescript
// vite.config.ts - Configurar proxy
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/ws': {
        target: 'ws://localhost:8080',
        ws: true
      }
    }
  }
})
```

## 5.2 Validação com Zod

### 5.2.1 Schemas de Validação (Frontend)

```typescript
// types/schemas.ts
import { z } from 'zod';

export const loginSchema = z.object({
  username: z.string().min(1, 'Usuário é obrigatório'),
  password: z.string().min(1, 'Senha é obrigatória'),
});

export const leadSchema = z.object({
  firstName: z.string().min(1, 'Nome é obrigatório').max(100),
  lastName: z.string().min(1, 'Sobrenome é obrigatório').max(100),
  email: z.string().email('Email inválido'),
  phone: z.string().min(10, 'Telefone deve ter pelo menos 10 dígitos'),
  position: z.string().min(1, 'Cargo é obrigatório').max(100),
  company: z.string().min(1, 'Empresa é obrigatória').max(100),
});

export const questionsSchema = z.object({
  question1Responses: z.array(z.string()).min(1, 'Selecione pelo menos uma opção'),
  question2Responses: z.array(z.string()).min(1, 'Selecione pelo menos uma opção'),
  question3Responses: z.array(z.string()).min(1, 'Selecione pelo menos uma opção'),
  question4Responses: z.array(z.string()).min(1, 'Selecione pelo menos uma opção'),
  question5Text: z.string().optional(),
});

export const planSelectionSchema = z.object({
  selectedPlan: z.string().optional(),
});
```

### 5.2.2 Validação no Backend (Kotlin)

```kotlin
// shared/validation/ValidationSchemas.kt
package com.menuerh.shared.validation

import kotlinx.serialization.Serializable

@Serializable
data class LeadValidationRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val position: String,
    val company: String
) {
    fun validate(): List<String> {
        val errors = mutableListOf<String>()
        
        if (firstName.isBlank()) errors.add("Nome é obrigatório")
        if (lastName.isBlank()) errors.add("Sobrenome é obrigatório")
        if (email.isBlank() || !email.contains("@")) errors.add("Email inválido")
        if (phone.isBlank() || phone.length < 10) errors.add("Telefone inválido")
        if (position.isBlank()) errors.add("Cargo é obrigatório")
        if (company.isBlank()) errors.add("Empresa é obrigatória")
        
        return errors
    }
}
```

## 5.3 Testes Unitários

### 5.3.1 Testes Backend (Kotlin)

```kotlin
// tests/auth/AuthServiceTest.kt
package com.menuerh.auth

import com.menuerh.shared.database.DatabaseConfig
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class AuthServiceTest {
    
    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            DatabaseConfig.init()
        }
    }
    
    @Test
    fun `should authenticate valid operator`() = runBlocking {
        val authService = AuthService()
        val token = authService.authenticate("operador1", "admin123")
        
        assertNotNull(token)
        assertTrue(token!!.isNotEmpty())
    }
    
    @Test
    fun `should reject invalid credentials`() = runBlocking {
        val authService = AuthService()
        val token = authService.authenticate("operador1", "wrongpassword")
        
        assertNull(token)
    }
}
```

```kotlin
// tests/leads/LeadServiceTest.kt
package com.menuerh.leads

import com.menuerh.shared.database.DatabaseConfig
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class LeadServiceTest {
    
    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            DatabaseConfig.init()
        }
    }
    
    @Test
    fun `should create lead successfully`() = runBlocking {
        val leadRepository = LeadRepository()
        val leadService = LeadService(leadRepository)
        
        val request = CreateLeadRequest(
            firstName = "João",
            lastName = "Silva",
            email = "joao@test.com",
            phone = "11999999999",
            position = "Gerente",
            company = "Empresa Teste"
        )
        
        val lead = leadService.createLead(request, 1)
        
        assertNotNull(lead)
        assertEquals("João", lead.firstName)
        assertEquals("Silva", lead.lastName)
        assertEquals("joao@test.com", lead.email)
        assertNotNull(lead.leadId)
    }
}
```

### 5.3.2 Testes Frontend (Jest + React Testing Library)

```typescript
// frontend/src/__tests__/LoginPage.test.tsx
import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from '../hooks/useAuth';
import { LoginPage } from '../pages/LoginPage';

const renderWithProviders = (component: React.ReactElement) => {
  return render(
    <BrowserRouter>
      <AuthProvider>
        {component}
      </AuthProvider>
    </BrowserRouter>
  );
};

describe('LoginPage', () => {
  test('should render login form', () => {
    renderWithProviders(<LoginPage />);
    
    expect(screen.getByLabelText(/usuário/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/senha/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /entrar/i })).toBeInTheDocument();
  });

  test('should show validation errors for empty fields', async () => {
    renderWithProviders(<LoginPage />);
    
    const submitButton = screen.getByRole('button', { name: /entrar/i });
    fireEvent.click(submitButton);
    
    await waitFor(() => {
      expect(screen.getByText(/usuário é obrigatório/i)).toBeInTheDocument();
      expect(screen.getByText(/senha é obrigatória/i)).toBeInTheDocument();
    });
  });
});
```

```typescript
// frontend/src/__tests__/LeadRegistrationPage.test.tsx
import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from '../hooks/useAuth';
import { LeadRegistrationPage } from '../pages/LeadRegistrationPage';

const renderWithProviders = (component: React.ReactElement) => {
  return render(
    <BrowserRouter>
      <AuthProvider>
        {component}
      </AuthProvider>
    </BrowserRouter>
  );
};

describe('LeadRegistrationPage', () => {
  test('should render lead registration form', () => {
    renderWithProviders(<LeadRegistrationPage />);
    
    expect(screen.getByLabelText(/nome/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/sobrenome/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/telefone/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/cargo/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/empresa/i)).toBeInTheDocument();
  });

  test('should validate email format', async () => {
    renderWithProviders(<LeadRegistrationPage />);
    
    const emailInput = screen.getByLabelText(/email/i);
    fireEvent.change(emailInput, { target: { value: 'invalid-email' } });
    fireEvent.blur(emailInput);
    
    await waitFor(() => {
      expect(screen.getByText(/email inválido/i)).toBeInTheDocument();
    });
  });
});
```

## 5.4 Testes de Integração

### 5.4.1 Testes de API (Backend)

```kotlin
// backend/src/test/kotlin/com/menuerh/integration/ApiIntegrationTest.kt
package com.menuerh.integration

import com.menuerh.shared.database.DatabaseConfig
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class ApiIntegrationTest {
    
    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            DatabaseConfig.init()
        }
    }
    
    @Test
    fun `should login and get token`() = testApplication {
        val client = createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                kotlinx.serialization.json.Json()
            }
        }
        
        val response = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"operador1","password":"admin123"}""")
        }
        
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("token"))
    }
    
    @Test
    fun `should create lead with valid token`() = testApplication {
        val client = createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                kotlinx.serialization.json.Json()
            }
        }
        
        // Primeiro fazer login
        val loginResponse = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"operador1","password":"admin123"}""")
        }
        
        val token = extractTokenFromResponse(loginResponse.bodyAsText())
        
        // Criar lead com token
        val leadResponse = client.post("/leads") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody("""
                {
                    "firstName":"João",
                    "lastName":"Silva",
                    "email":"joao@test.com",
                    "phone":"11999999999",
                    "position":"Gerente",
                    "company":"Empresa Teste"
                }
            """)
        }
        
        assertEquals(HttpStatusCode.Created, leadResponse.status)
    }
    
    private fun extractTokenFromResponse(response: String): String {
        // Implementar extração do token da resposta JSON
        return response.substringAfter("\"token\":\"").substringBefore("\"")
    }
}
```

### 5.4.2 Testes E2E (Frontend)

```typescript
// frontend/cypress/e2e/lead-flow.cy.ts
describe('Lead Registration Flow', () => {
  beforeEach(() => {
    cy.visit('/login')
  })

  it('should complete full lead registration flow', () => {
    // Login
    cy.get('[data-testid="username-input"]').type('operador1')
    cy.get('[data-testid="password-input"]').type('admin123')
    cy.get('[data-testid="login-button"]').click()

    // Verificar redirecionamento para cadastro
    cy.url().should('include', '/leads')

    // Preencher dados do lead
    cy.get('[data-testid="firstName-input"]').type('João')
    cy.get('[data-testid="lastName-input"]').type('Silva')
    cy.get('[data-testid="email-input"]').type('joao@test.com')
    cy.get('[data-testid="phone-input"]').type('11999999999')
    cy.get('[data-testid="position-input"]').type('Gerente')
    cy.get('[data-testid="company-input"]').type('Empresa Teste')
    cy.get('[data-testid="next-button"]').click()

    // Verificar redirecionamento para perguntas
    cy.url().should('include', '/questions')

    // Responder perguntas
    cy.get('[data-testid="question1-checkbox-0"]').check()
    cy.get('[data-testid="question2-checkbox-1"]').check()
    cy.get('[data-testid="question3-checkbox-2"]').check()
    cy.get('[data-testid="question4-checkbox-0"]').check()
    cy.get('[data-testid="question5-textarea"]').type('Anotação de teste')
    cy.get('[data-testid="next-button"]').click()

    // Verificar redirecionamento para planos
    cy.url().should('include', '/plans')

    // Selecionar plano
    cy.get('[data-testid="plan-radio-basico"]').check()
    cy.get('[data-testid="finalize-button"]').click()

    // Verificar sucesso e redirecionamento
    cy.get('[data-testid="success-toast"]').should('be.visible')
    cy.url().should('include', '/leads')
  })
})
```

## 5.5 Testes de Performance

### 5.5.1 Testes de Carga (Backend)

```kotlin
// backend/src/test/kotlin/com/menuerh/performance/LoadTest.kt
package com.menuerh.performance

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class LoadTest {
    
    @Test
    fun `should handle concurrent lead creation`() = runTest {
        val client = HttpClient()
        val numberOfRequests = 100
        val concurrentRequests = 10
        
        val time = measureTimeMillis {
            val jobs = (1..numberOfRequests).map { requestId ->
                async {
                    try {
                        val response = client.post("http://localhost:8080/leads") {
                            contentType(ContentType.Application.Json)
                            setBody("""
                                {
                                    "firstName":"User$requestId",
                                    "lastName":"Test",
                                    "email":"user$requestId@test.com",
                                    "phone":"11999999999",
                                    "position":"Test",
                                    "company":"Test Company"
                                }
                            """)
                        }
                        response.status
                    } catch (e: Exception) {
                        HttpStatusCode.InternalServerError
                    }
                }
            }
            
            val results = jobs.awaitAll()
            val successCount = results.count { it == HttpStatusCode.Created }
            
            println("Success rate: ${successCount * 100 / numberOfRequests}%")
        }
        
        println("Total time: ${time}ms")
        println("Average time per request: ${time / numberOfRequests}ms")
    }
}
```

## 5.6 Scripts de Deploy e Teste

### 5.6.1 Script de Teste Completo

```bash
#!/bin/bash
# test-system.sh

echo "🧪 Iniciando testes do sistema MenuErh..."

# Verificar se o banco está rodando
echo "📊 Verificando conexão com banco..."
psql -d menuerh_db -c "SELECT 1;" > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "❌ Banco de dados não está rodando"
    exit 1
fi
echo "✅ Banco de dados OK"

# Executar testes do backend
echo "🔧 Executando testes do backend..."
cd backend
./gradlew test
if [ $? -ne 0 ]; then
    echo "❌ Testes do backend falharam"
    exit 1
fi
echo "✅ Testes do backend OK"

# Executar testes do frontend
echo "🎨 Executando testes do frontend..."
cd ../frontend
npm test -- --watchAll=false
if [ $? -ne 0 ]; then
    echo "❌ Testes do frontend falharam"
    exit 1
fi
echo "✅ Testes do frontend OK"

# Testes de integração
echo "🔗 Executando testes de integração..."
cd ../backend
./gradlew integrationTest
if [ $? -ne 0 ]; then
    echo "❌ Testes de integração falharam"
    exit 1
fi
echo "✅ Testes de integração OK"

echo "🎉 Todos os testes passaram!"
```

### 5.6.2 Script de Deploy Local

```bash
#!/bin/bash
# deploy-local.sh

echo "🚀 Deploy local do MenuErh..."

# Parar serviços existentes
echo "🛑 Parando serviços..."
pkill -f "java.*ktor" || true
pkill -f "node.*vite" || true

# Build do backend
echo "🔧 Build do backend..."
cd backend
./gradlew build
if [ $? -ne 0 ]; then
    echo "❌ Build do backend falhou"
    exit 1
fi

# Build do frontend
echo "🎨 Build do frontend..."
cd ../frontend
npm run build
if [ $? -ne 0 ]; then
    echo "❌ Build do frontend falhou"
    exit 1
fi

# Iniciar backend
echo "🚀 Iniciando backend..."
cd ../backend
nohup ./gradlew run > ../logs/backend.log 2>&1 &
BACKEND_PID=$!

# Aguardar backend inicializar
sleep 10

# Verificar se backend está rodando
curl -f http://localhost:8080/health > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "❌ Backend não iniciou corretamente"
    exit 1
fi

# Iniciar frontend
echo "🎨 Iniciando frontend..."
cd ../frontend
nohup npm run preview > ../logs/frontend.log 2>&1 &
FRONTEND_PID=$!

# Aguardar frontend inicializar
sleep 5

# Verificar se frontend está rodando
curl -f http://localhost:4173 > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "❌ Frontend não iniciou corretamente"
    exit 1
fi

echo "✅ Sistema MenuErh rodando!"
echo "🌐 Frontend: http://localhost:4173"
echo "🔧 Backend: http://localhost:8080"
echo "📊 Dashboard: http://localhost:4173/dashboard"

# Salvar PIDs para parar depois
echo $BACKEND_PID > ../logs/backend.pid
echo $FRONTEND_PID > ../logs/frontend.pid
```

## 5.7 Monitoramento e Logs

### 5.7.1 Configuração de Logs

```kotlin
// backend/src/main/resources/logback.xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{YYYY-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/menuerh.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/menuerh.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{YYYY-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="STDOUT"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

### 5.7.2 Endpoint de Health Check

```kotlin
// backend/src/main/kotlin/com/menuerh/shared/health/HealthRoutes.kt
package com.menuerh.shared.health

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.menuerh.shared.database.DatabaseConfig
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

fun Application.HealthRoutes() {
    routing {
        get("/health") {
            try {
                // Verificar conexão com banco
                val dbStatus = kotlinx.coroutines.runBlocking {
                    try {
                        DatabaseConfig.dbQuery {
                            org.jetbrains.exposed.sql.select(1).singleOrNull()
                        }
                        "OK"
                    } catch (e: Exception) {
                        "ERROR"
                    }
                }
                
                val status = if (dbStatus == "OK") "healthy" else "unhealthy"
                val statusCode = if (dbStatus == "OK") HttpStatusCode.OK else HttpStatusCode.ServiceUnavailable
                
                call.respond(statusCode, mapOf(
                    "status" to status,
                    "database" to dbStatus,
                    "timestamp" to java.time.LocalDateTime.now().toString()
                ))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf(
                    "status" to "error",
                    "message" to e.message
                ))
            }
        }
    }
}
```

## 5.8 Checklist de Validação

### 5.8.1 Funcionalidades Principais
- [ ] Login com operadores 1-10 (senha: admin123)
- [ ] Cadastro de leads com validação
- [ ] Fluxo de perguntas com checkboxes
- [ ] Apresentação de planos com carrossel
- [ ] Dashboard com estatísticas
- [ ] WebSocket para tempo real
- [ ] Validação de dados com Zod
- [ ] Tratamento de erros

### 5.8.2 Performance
- [ ] Tempo de resposta < 500ms para APIs
- [ ] Suporte a 10+ usuários simultâneos
- [ ] Carregamento de página < 3s
- [ ] Uso de memória < 512MB

### 5.8.3 Segurança
- [ ] Autenticação JWT
- [ ] Validação de entrada
- [ ] CORS configurado
- [ ] Senhas hasheadas
- [ ] Rate limiting

### 5.8.4 Usabilidade
- [ ] Interface responsiva
- [ ] Feedback visual para ações
- [ ] Navegação intuitiva
- [ ] Mensagens de erro claras

## 5.9 Próximos Passos
- [ ] Executar todos os testes
- [ ] Corrigir bugs encontrados
- [ ] Otimizar performance
- [ ] Documentar APIs
- [ ] Preparar para produção

## Tempo Estimado: 4-6 horas 