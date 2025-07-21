# FASE 02 - IMPLEMENTAÇÃO: BANCO DE DADOS E AUTENTICAÇÃO

## 📋 Visão Geral da Implementação

A Fase 2 foi implementada com sucesso, criando toda a infraestrutura de banco de dados e autenticação do sistema MenuErh.

## 🗄️ Estrutura do Banco de Dados

### Tabelas Criadas

#### 1. `operators` - Operadores do Sistema
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

**Explicação da função [operators]**: Tabela principal que armazena os operadores do sistema. Cada operador possui um ID único, username único, hash da senha, nome completo e status de atividade. Inclui timestamps automáticos para auditoria.

#### 2. `leads` - Leads Capturados
```sql
CREATE TABLE leads (
    id SERIAL PRIMARY KEY,
    lead_id VARCHAR(20) UNIQUE NOT NULL,
    operator_id INTEGER NOT NULL REFERENCES operators(id),
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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Explicação da função [leads]**: Tabela central que armazena todos os leads capturados. Cada lead está associado a um operador, contém dados pessoais, respostas das perguntas de qualificação (em arrays PostgreSQL) e o plano selecionado. O `lead_id` é um identificador único gerado automaticamente.

#### 3. `plans` - Planos Disponíveis
```sql
CREATE TABLE plans (
    id SERIAL PRIMARY KEY,
    plan_id VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    image_url VARCHAR(500),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Explicação da função [plans]**: Tabela que armazena os planos disponíveis para apresentação aos leads. Cada plano possui um ID único, nome, descrição detalhada, URL da imagem e status de atividade.

### Índices e Performance
- `idx_leads_operator_id` - Otimiza consultas por operador
- `idx_leads_created_at` - Otimiza consultas por data
- `idx_leads_lead_id` - Otimiza busca por ID do lead
- `idx_operators_username` - Otimiza autenticação
- `idx_plans_plan_id` - Otimiza busca de planos

### Triggers Automáticos
- `update_operators_updated_at` - Atualiza automaticamente o `updated_at` dos operadores
- `update_leads_updated_at` - Atualiza automaticamente o `updated_at` dos leads

## 🔐 Sistema de Autenticação

### AuthService.kt
**Explicação da função [AuthService]**: Serviço central de autenticação que gerencia login, geração e validação de tokens JWT. Utiliza bcrypt para verificação de senhas e JWT para tokens de sessão.

**Funcionalidades principais**:
- `authenticate()` - Valida credenciais e retorna token JWT
- `generateToken()` - Gera token JWT com claims do usuário
- `verifyToken()` - Verifica validade do token
- `validateToken()` - Valida token e retorna boolean
- `getUserFromToken()` - Extrai dados do usuário do token

### Configuração JWT
- **Secret**: Configurável via variável de ambiente `JWT_SECRET`
- **Issuer**: "menuerh"
- **Audience**: "menuerh-users"
- **Expiração**: 24 horas
- **Claims**: userId, username, name

## 📊 Modelos Exposed (Kotlin)

### Operator.kt
**Explicação do arquivo [Operator.kt]**: Modelo de dados para operadores usando Exposed ORM. Define a estrutura da tabela e a data class correspondente.

### Lead.kt
**Explicação do arquivo [Lead.kt]**: Modelo de dados para leads com suporte a arrays PostgreSQL para as respostas das perguntas.

### Plan.kt
**Explicação do arquivo [Plan.kt]**: Modelo de dados para planos com campos para nome, descrição e imagem.

## 🔧 Configuração de Banco

### DatabaseConfig.kt
**Explicação da função [DatabaseConfig]**: Configuração centralizada de conexão com PostgreSQL usando HikariCP para pool de conexões.

**Configurações**:
- **Driver**: PostgreSQL
- **Pool**: HikariCP com configurações otimizadas
- **Transações**: Suporte a transações síncronas e assíncronas
- **Timeout**: Configurações de timeout para conexões

## 📚 Repositórios

### OperatorRepository.kt
**Explicação da função [OperatorRepository]**: Camada de acesso a dados para operadores, fornecendo métodos para consulta e manipulação.

**Métodos**:
- `getAllOperators()` - Lista todos os operadores
- `getOperatorById()` - Busca operador por ID
- `getOperatorByUsername()` - Busca operador por username
- `getActiveOperators()` - Lista apenas operadores ativos

### PlanRepository.kt
**Explicação da função [PlanRepository]**: Camada de acesso a dados para planos, fornecendo métodos para consulta.

**Métodos**:
- `getAllPlans()` - Lista todos os planos
- `getPlanById()` - Busca plano por ID
- `getPlanByPlanId()` - Busca plano por plan_id
- `getActivePlans()` - Lista apenas planos ativos

## 🛠️ Utilitários

### ArrayUtils.kt
**Explicação da função [ArrayUtils]**: Utilitários para conversão entre arrays PostgreSQL e List<String> do Kotlin.

**Funcionalidades**:
- `getTextArray()` - Converte coluna PostgreSQL array para List<String>
- `toPostgresArray()` - Converte List<String> para formato PostgreSQL
- `toTextList()` - Converte string PostgreSQL array para List<String>

## 📝 Dados Iniciais

### Operadores Pré-cadastrados
10 operadores com as seguintes credenciais:
- **Usuários**: operador1 a operador10
- **Senha**: admin123 (hash bcrypt)
- **Status**: Todos ativos

### Planos Pré-cadastrados
3 planos disponíveis:
1. **Básico** - Para pequenas equipes
2. **Pro** - Solução mais popular
3. **Enterprise** - Para grandes corporações

## 🚀 Scripts de Setup

### setup-database.ps1 (Windows)
**Explicação da função [setup-database.ps1]**: Script PowerShell para automatizar a configuração completa do banco de dados no Windows.

**Funcionalidades**:
- Verifica conexão com PostgreSQL
- Cria banco de dados se não existir
- Executa migrações automaticamente
- Verifica dados inseridos
- Fornece instruções de uso

### setup-database.sh (Linux/Mac)
**Explicação da função [setup-database.sh]**: Script Bash equivalente para sistemas Unix/Linux.

## 🔍 Como Testar

### 1. Configurar Banco
```powershell
# Windows
.\database\setup-database.ps1

# Linux/Mac
./database/setup-database.sh
```

### 2. Verificar Dados
```sql
-- Verificar operadores
SELECT username, name FROM operators;

-- Verificar planos
SELECT plan_id, name FROM plans;

-- Verificar estrutura
\d operators
\d leads
\d plans
```

### 3. Testar Autenticação
```kotlin
// Exemplo de uso do AuthService
val authService = AuthService()
val token = authService.authenticate("operador1", "admin123")
val isValid = authService.validateToken(token)
```

## 📈 Próximos Passos

### Fase 3 - Backend Ktor e APIs
- [ ] Implementar servidor Ktor
- [ ] Criar endpoints de autenticação
- [ ] Implementar APIs de leads
- [ ] Implementar APIs de planos
- [ ] Configurar CORS e middlewares

### Fase 4 - Frontend React
- [ ] Integrar com APIs do backend
- [ ] Implementar formulário de leads
- [ ] Criar apresentação de planos
- [ ] Desenvolver dashboard completo

### Fase 5 - Integração e Testes
- [ ] Testes unitários
- [ ] Testes de integração
- [ ] Testes end-to-end
- [ ] Deploy e monitoramento

## ✅ Status da Fase 2

**CONCLUÍDA** ✅

- [x] Estrutura do banco de dados
- [x] Scripts de migração
- [x] Modelos Exposed
- [x] Sistema de autenticação
- [x] Repositórios de dados
- [x] Utilitários
- [x] Scripts de setup
- [x] Dados iniciais
- [x] Documentação

**Tempo de Implementação**: ~4 horas
**Arquivos Criados**: 13 arquivos
**Linhas de Código**: ~500 linhas 