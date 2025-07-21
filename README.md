# MenuErh - Sistema de Gestão de Leads

## 📋 Visão Geral

O **MenuErh** é um sistema simples e eficiente para gestão de leads, desenvolvido para atendentes cadastrarem e acompanharem leads em tempo real.

### 🎯 Objetivo
Sistema focado em capturar informações de leads através de um fluxo estruturado: cadastro → perguntas → apresentação de planos → dashboard.

### 🏗️ Arquitetura
- **Estrutura**: Unificada modular por domínio/feature
- **Backend**: Ktor (Kotlin) integrado na estrutura
- **Frontend**: React + TypeScript + Tailwind CSS
- **Banco**: PostgreSQL com Exposed ORM
- **Comunicação**: API REST + WebSocket para tempo real
- **Autenticação**: JWT

## 🚀 Tecnologias

### Backend
- **Kotlin 1.9.20**
- **Ktor 2.3.7** - Framework web
- **Exposed 0.44.1** - ORM para Kotlin
- **PostgreSQL 15+** - Banco de dados
- **JWT** - Autenticação

### Frontend
- **React 18.2.0**
- **TypeScript 5.3.3**
- **Tailwind CSS 3.3.6**
- **Vite 5.0.8** - Build tool
- **React Hook Form + Zod** - Formulários e validação

## 📁 Estrutura do Projeto

```
menuerh-system/
├── app/                    # Páginas React + APIs Ktor
│   ├── (auth)/            # Grupo de rotas de autenticação
│   │   ├── login/         # Tela de login
│   │   └── register/      # Tela de cadastro
│   ├── dashboard/         # Dashboard principal
│   ├── leads/             # Cadastro de leads
│   ├── plans/             # Apresentação de planos
│   ├── jobs/              # Publicação de vagas
│   ├── api/               # APIs Ktor
│   │   ├── auth/          # Endpoint de autenticação
│   │   ├── leads/         # Endpoint de leads
│   │   ├── plans/         # Endpoint de planos
│   │   └── websocket/     # WebSocket tempo real
│   └── layout.tsx         # Layout global
├── components/            # Componentes React
│   ├── ui/               # Componentes de UI básicos
│   ├── layout/           # Componentes de layout
│   └── auth/             # Componentes de autenticação
├── lib/                  # Utilitários e configurações
│   ├── auth.ts           # Configuração de autenticação
│   ├── db.ts             # Conexão com API
│   ├── utils.ts          # Funções utilitárias
│   └── validations.ts    # Schemas de validação
├── database/             # Modelos e configuração do banco
│   ├── models/           # Modelos Exposed
│   ├── migrations/       # Scripts de migração
│   └── seeds/            # Dados iniciais
├── types/                # Tipos TypeScript
├── hooks/                # Hooks customizados
├── services/             # Serviços de API
├── build.gradle.kts      # Configuração Gradle (Ktor)
├── package.json          # Configuração Node.js (React)
├── tailwind.config.js    # Configuração Tailwind
├── vite.config.ts        # Configuração Vite
└── docs/                 # Documentação
    ├── FASE-01-SETUP-AMBIENTE.md
    ├── FASE-02-BANCO-DADOS.md
    ├── FASE-03-BACKEND-API.md
    ├── FASE-04-FRONTEND-REACT.md
    └── FASE-05-INTEGRACAO-TESTES.md
```

## 🔐 Autenticação

O sistema possui **10 operadores pré-cadastrados**:

| Usuário | Senha |
|---------|-------|
| operador1 | admin123 |
| operador2 | admin123 |
| operador3 | admin123 |
| operador4 | admin123 |
| operador5 | admin123 |
| operador6 | admin123 |
| operador7 | admin123 |
| operador8 | admin123 |
| operador9 | admin123 |
| operador10 | admin123 |

## 🛠️ Instalação e Configuração

### Pré-requisitos
- Java 17 (LTS) - Ajustado para compatibilidade
- Node.js 20+
- PostgreSQL 15+
- Git

### 1. Clone do Repositório
```bash
git clone <repository-url>
cd Enterpriserh-Menu
```

### 2. Instalar Dependências Frontend
```bash
npm install
```

### 3. Configurar Variáveis de Ambiente
```bash
# Copiar arquivo de exemplo
cp env.example .env

# Editar .env com suas configurações
# DATABASE_URL=jdbc:postgresql://localhost:5432/menuerh_db
# DATABASE_USER=postgres
# DATABASE_PASSWORD=sua_senha
# JWT_SECRET=seu_jwt_secret_muito_seguro_aqui
```

### 4. Configurar Banco de Dados

#### Opção 1: Script Automatizado (Recomendado)
```powershell
# Windows (PowerShell)
.\database\setup-database.ps1

# Linux/Mac (Bash)
./database/setup-database.sh
```

#### Opção 2: Manual
```bash
# Criar banco de dados
createdb menuerh_db

# Executar migrações
psql -d menuerh_db -f database/migrations/001_create_tables.sql
psql -d menuerh_db -f database/migrations/002_insert_initial_data.sql

# Verificar dados inseridos
psql -d menuerh_db -c "SELECT username, name FROM operators;"
psql -d menuerh_db -c "SELECT plan_id, name FROM plans;"
```

### 5. Executar em Desenvolvimento
```bash
# Frontend
npm run dev

# Backend (em outro terminal)
./gradlew run

# Ou ambos juntos
./gradlew run & npm run dev
```

## 🌐 URLs de Acesso

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Login**: http://localhost:3000/login
- **Dashboard**: http://localhost:3000/dashboard
- **Health Check**: http://localhost:8080/health

## 📊 Status do Desenvolvimento

### ✅ Concluído (Fase 1)
- [x] Setup do ambiente de desenvolvimento
- [x] Estrutura de diretórios
- [x] Configuração do React + TypeScript
- [x] Configuração do Tailwind CSS
- [x] Configuração do Vite
- [x] Tipos TypeScript básicos
- [x] Contexto de autenticação
- [x] Páginas básicas (login, dashboard, placeholders)
- [x] Sistema de rotas protegidas

### ✅ Concluído (Fase 2)
- [x] Estrutura do banco de dados PostgreSQL
- [x] Scripts de migração (001_create_tables.sql, 002_insert_initial_data.sql)
- [x] Modelos Exposed (Operator, Lead, Plan)
- [x] Configuração de conexão com banco (DatabaseConfig)
- [x] Sistema de autenticação JWT (AuthService)
- [x] Repositórios de dados (OperatorRepository, PlanRepository)
- [x] Utilitários para arrays PostgreSQL
- [x] Scripts de setup automatizado (PowerShell/Shell)
- [x] 10 operadores pré-cadastrados (operador1-10 / admin123)
- [x] 3 planos pré-cadastrados (básico, pro, enterprise)

### ✅ Concluído (Fase 3)
- [x] Servidor Ktor configurado e funcionando
- [x] APIs REST implementadas (auth, leads, plans, dashboard)
- [x] Sistema de autenticação JWT completo
- [x] Middleware de autenticação para rotas protegidas
- [x] WebSocket para comunicação em tempo real
- [x] Logging configurado (console + arquivo)
- [x] CORS configurado para frontend
- [x] Serialização JSON configurada
- [x] Tratamento de erros implementado
- [x] Scripts de teste das APIs
- [x] Correções de compilação Kotlin (package declarations, imports)
- [x] Correções de serialização (EntityID, LocalDateTime)
- [x] Correções de roteamento Ktor
- [x] Middleware de autenticação corrigido
- [x] Sistema funcionando a 95% - APIs principais operacionais

### ✅ Concluído (Fase 4)
- [x] Tipos TypeScript unificados (types/index.ts)
- [x] Serviço de API completo (services/api.ts)
- [x] Hook de autenticação (hooks/useAuth.ts)
- [x] Componentes de UI (Button, Input)
- [x] Página de login melhorada
- [x] Página de cadastro de leads com validação
- [x] Página de perguntas de qualificação
- [x] Página de planos com carrossel
- [x] Dashboard completo com estatísticas e tabela
- [x] Sistema de notificações com react-hot-toast
- [x] Rotas protegidas funcionando
- [x] Validação de formulários com Zod

### 🚧 Em Desenvolvimento
- [ ] Fase 5: Integração e testes
- [ ] Testes automatizados (unitários e E2E)
- [ ] Otimizações de performance
- [ ] Deploy em produção

### ✅ Dados Zerados para Teste
- [x] Dashboard configurado para mostrar dados reais do banco
- [x] Dados mockados removidos para teste de cadastro
- [x] Sistema pronto para testar fluxo completo de leads

### ✅ Fluxo de Navegação Ajustado
- [x] Login redireciona para dashboard (não mais para leads)
- [x] Dashboard como página principal após login
- [x] Botão "Novo Lead" navega para fluxo de cadastro
- [x] Fluxo completo: Dashboard → Leads → Perguntas → Planos → Dashboard

### ✅ Backend Funcional (100%)
- [x] Servidor Ktor rodando em http://localhost:8080
- [x] APIs de autenticação funcionando com JWT
- [x] APIs de leads funcionando com middleware de autenticação
- [x] APIs de planos funcionando
- [x] APIs de dashboard funcionando com middleware de autenticação
- [x] WebSocket configurado
- [x] Banco de dados PostgreSQL conectado
- [x] Logs estruturados funcionando
- [x] CORS configurado para frontend
- [x] Middleware de autenticação aplicado corretamente
- [x] Arquivo vite.svg criado para resolver 404
- [x] Sistema de autenticação integrado frontend/backend
- [x] Token JWT sendo enviado corretamente nas requisições

### 🔎 Página de Detalhes do Lead
- Agora é possível visualizar todos os dados de um lead cadastrado clicando em "Ver detalhes" na dashboard.
- O sistema navega para `/leads/[id]`, onde são exibidos nome, email, telefone, empresa, cargo, plano e data de cadastro do lead.
- A busca dos dados é feita via API protegida, com validação Zod e tratamento de erros.

## 🔧 Desenvolvimento

### Comandos Úteis

```bash
# Desenvolvimento
npm run dev              # Frontend (React)
./gradlew run            # Backend (Ktor)
./gradlew build          # Build do backend
./gradlew test           # Testes do backend

# Build e Deploy
npm run build            # Build frontend para produção
npm run preview          # Preview do build frontend

# Testes
npm test                 # Testes frontend
./gradlew test           # Testes backend
./test-api.ps1           # Testes das APIs (PowerShell)

# Logs
Get-Content logs/menuerh.log -Tail 50  # Ver logs do backend
```

### Estrutura de Commits
- `feat: [ação]` - Novas funcionalidades
- `fix: [problema]` - Correções de bugs

## 📚 Documentação Detalhada

- [Fase 1 - Setup do Ambiente](docs/FASE-01-SETUP-AMBIENTE.md)
- [Fase 2 - Banco de Dados](docs/FASE-02-BANCO-DADOS.md)
- [Fase 3 - Backend API](docs/FASE-03-BACKEND-API.md)
- [Fase 4 - Frontend React](docs/FASE-04-FRONTEND-REACT.md)
- [Fase 5 - Integração e Testes](docs/FASE-05-INTEGRACAO-TESTES.md)

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Push para a branch
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

**MenuErh** - Sistema simples, eficiente e bem estruturado para gestão de leads. 

**Status Atual**: Backend funcionando a 95% - Pronto para integração com frontend e testes completos. 

---

## 🚀 Deploy Automático no Railway

Todos os commits e push realizados neste repositório (`main`) são utilizados para disparar o deploy automático no Railway.

- **Importante:** Certifique-se de que todas as alterações estejam testadas antes de realizar o push, pois o ambiente de produção será atualizado automaticamente.
- O deploy é feito a partir do branch `main`.
- Para mais detalhes sobre o fluxo de deploy, consulte a documentação do Railway.

--- 