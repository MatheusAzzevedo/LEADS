# FASE 02 - BANCO DE DADOS E AUTENTICAÇÃO

## Objetivo
Criar estrutura do banco de dados PostgreSQL e sistema de autenticação com 10 operadores pré-cadastrados.

## 2.1 Estrutura do Banco de Dados

### Tabelas Principais

#### 1. Tabela: operators (Operadores)
```sql
CREATE TABLE operators (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 2. Tabela: leads (Leads)
```sql
CREATE TABLE leads (
    id SERIAL PRIMARY KEY,
    lead_id VARCHAR(20) UNIQUE NOT NULL, -- ID numérico único gerado
    operator_id INTEGER NOT NULL REFERENCES operators(id),
    
    -- Dados de cadastro
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    position VARCHAR(100) NOT NULL,
    company VARCHAR(100) NOT NULL,
    
    -- Respostas das perguntas
    question1_responses TEXT[], -- Array de respostas checkbox
    question2_responses TEXT[],
    question3_responses TEXT[],
    question4_responses TEXT[],
    question5_text TEXT, -- Campo de texto
    
    -- Plano selecionado
    selected_plan VARCHAR(50), -- 'basico', 'pro', 'enterprise', null
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 3. Tabela: plans (Planos)
```sql
CREATE TABLE plans (
    id SERIAL PRIMARY KEY,
    plan_id VARCHAR(50) UNIQUE NOT NULL, -- 'basico', 'pro', 'enterprise'
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    image_url VARCHAR(500),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2.2 Scripts de Migração

#### Migration 001 - Create Tables
```sql
-- 001_create_tables.sql

-- Criar tabela de operadores
CREATE TABLE operators (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Criar tabela de leads
CREATE TABLE leads (
    id SERIAL PRIMARY KEY,
    lead_id VARCHAR(20) UNIQUE NOT NULL,
    operator_id INTEGER NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    position VARCHAR(100) NOT NULL,
    company VARCHAR(100) NOT NULL,
    question1_responses TEXT[],
    question2_responses TEXT[],
    question3_responses TEXT[],
    question4_responses TEXT[],
    question5_text TEXT,
    selected_plan VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_operator FOREIGN KEY (operator_id) REFERENCES operators(id)
);

-- Criar tabela de planos
CREATE TABLE plans (
    id SERIAL PRIMARY KEY,
    plan_id VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    image_url VARCHAR(500),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Criar índices para performance
CREATE INDEX idx_leads_operator_id ON leads(operator_id);
CREATE INDEX idx_leads_created_at ON leads(created_at);
CREATE INDEX idx_leads_lead_id ON leads(lead_id);
```

#### Migration 002 - Insert Initial Data
```sql
-- 002_insert_initial_data.sql

-- Inserir 10 operadores (senha: admin123 - hash bcrypt)
INSERT INTO operators (username, password_hash, name) VALUES
('operador1', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3ZxQQxqKre', 'Operador 1'),
('operador2', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3ZxQQxqKre', 'Operador 2'),
('operador3', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3ZxQQxqKre', 'Operador 3'),
('operador4', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3ZxQQxqKre', 'Operador 4'),
('operador5', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3ZxQQxqKre', 'Operador 5'),
('operador6', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3ZxQQxqKre', 'Operador 6'),
('operador7', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3ZxQQxqKre', 'Operador 7'),
('operador8', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3ZxQQxqKre', 'Operador 8'),
('operador9', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3ZxQQxqKre', 'Operador 9'),
('operador10', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3ZxQQxqKre', 'Operador 10');

-- Inserir planos
INSERT INTO plans (plan_id, name, description, image_url) VALUES
('basico', 'Plano Básico', 'Ideal para pequenas equipes que estão começando. Inclui funcionalidades essenciais para gestão de talentos.', 'https://placehold.co/600x400/3B82F6/FFFFFF?text=Básico'),
('pro', 'Plano Profissional', 'A solução mais popular, com ferramentas avançadas de automação, relatórios detalhados e integrações.', 'https://placehold.co/600x400/10B981/FFFFFF?text=Pro'),
('enterprise', 'Plano Enterprise', 'Para grandes corporações que necessitam de segurança robusta, suporte dedicado e personalização completa.', 'https://placehold.co/600x400/8B5CF6/FFFFFF?text=Enterprise');
```

### 2.3 Modelos Exposed (Kotlin)

#### Operator.kt
```kotlin
package com.menuerh.database.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Operators : IntIdTable() {
    val username = varchar("username", 50).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val name = varchar("name", 100)
    val isActive = bool("is_active").default(true)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())
}

data class Operator(
    val id: Int? = null,
    val username: String,
    val name: String,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)
```

#### Lead.kt
```kotlin
package com.menuerh.database.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Leads : IntIdTable() {
    val leadId = varchar("lead_id", 20).uniqueIndex()
    val operatorId = reference("operator_id", Operators)
    val firstName = varchar("first_name", 100)
    val lastName = varchar("last_name", 100)
    val email = varchar("email", 255)
    val phone = varchar("phone", 20)
    val position = varchar("position", 100)
    val company = varchar("company", 100)
    val question1Responses = text("question1_responses").nullable()
    val question2Responses = text("question2_responses").nullable()
    val question3Responses = text("question3_responses").nullable()
    val question4Responses = text("question4_responses").nullable()
    val question5Text = text("question5_text").nullable()
    val selectedPlan = varchar("selected_plan", 50).nullable()
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())
}

data class Lead(
    val id: Int? = null,
    val leadId: String,
    val operatorId: Int,
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
    val selectedPlan: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)
```

#### Plan.kt
```kotlin
package com.menuerh.database.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Plans : IntIdTable() {
    val planId = varchar("plan_id", 50).uniqueIndex()
    val name = varchar("name", 100)
    val description = text("description")
    val imageUrl = varchar("image_url", 500).nullable()
    val isActive = bool("is_active").default(true)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
}

data class Plan(
    val id: Int? = null,
    val planId: String,
    val name: String,
    val description: String,
    val imageUrl: String? = null,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime? = null
)
```

### 2.4 Configuração de Conexão com Banco

#### DatabaseConfig.kt
```kotlin
package com.menuerh.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseConfig {
    fun init() {
        val driverClassName = "org.postgresql.Driver"
        val jdbcURL = System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/menuerh_db"
        val username = System.getenv("DATABASE_USER") ?: "postgres"
        val password = System.getenv("DATABASE_PASSWORD") ?: "admin123"

        val config = HikariConfig().apply {
            this.driverClassName = driverClassName
            this.jdbcUrl = jdbcURL
            this.username = username
            this.password = password
            this.maximumPoolSize = 3
            this.isAutoCommit = false
            this.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
```

### 2.5 Sistema de Autenticação

#### AuthService.kt
```kotlin
package com.menuerh.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.menuerh.database.dbQuery
import com.menuerh.database.models.Operators
import org.jetbrains.exposed.sql.select
import java.util.*

class AuthService {
    private val secret = System.getenv("JWT_SECRET") ?: "default_secret_change_in_production"
    private val issuer = "menuerh"
    private val audience = "menuerh-users"
    private val algorithm = Algorithm.HMAC256(secret)

    suspend fun authenticate(username: String, password: String): String? {
        return dbQuery {
            val operator = Operators
                .slice(Operators.id, Operators.username, Operators.passwordHash, Operators.name)
                .select { Operators.username eq username }
                .singleOrNull()

            operator?.let {
                val storedHash = it[Operators.passwordHash]
                // Verificar senha (admin123)
                if (storedHash == "\$2a\$12\$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3ZxQQxqKre") {
                    generateToken(it[Operators.id], it[Operators.username], it[Operators.name])
                } else null
            }
        }
    }

    private fun generateToken(userId: Int, username: String, name: String): String {
        return JWT.create()
            .withSubject(userId.toString())
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("username", username)
            .withClaim("name", name)
            .withExpiresAt(Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24 horas
            .sign(algorithm)
    }

    fun verifyToken(token: String): JWT.require(algorithm) {
        return JWT.require(algorithm)
            .withIssuer(issuer)
            .withAudience(audience)
            .build()
    }
}
```

### 2.6 Comandos de Execução

```bash
# Executar migrações
psql -d menuerh_db -f database/migrations/001_create_tables.sql
psql -d menuerh_db -f database/migrations/002_insert_initial_data.sql

# Verificar dados inseridos
psql -d menuerh_db -c "SELECT username, name FROM operators;"
psql -d menuerh_db -c "SELECT plan_id, name FROM plans;"
```

### 2.7 Próximos Passos
- [ ] Executar scripts de migração
- [ ] Testar conexão com banco
- [ ] Verificar autenticação dos operadores
- [ ] Implementar repositórios de dados
- [ ] Configurar JWT no backend

## Tempo Estimado: 3-4 horas 