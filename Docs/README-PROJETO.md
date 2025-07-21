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

## 📊 Fluxo do Sistema

### 1. Cadastro do Lead
- Nome, sobrenome, email, telefone, cargo, empresa
- Geração automática de ID único
- Validação de dados com Zod

### 2. Perguntas de Qualificação
- 5 perguntas com checkboxes
- Última pergunta com campo de texto
- Respostas salvas no banco

### 3. Apresentação de Planos
- Carrossel com 3 planos
- Seleção de plano de interesse
- Interface visual atrativa

### 4. Dashboard
- Estatísticas em tempo real
- Tabela de leads cadastrados
- Detalhes completos por lead
- Filtros e busca

## 🛠️ Instalação e Configuração

### Pré-requisitos
- Java 21 (LTS)
- Node.js 20+
- PostgreSQL 15+
- Git

### 1. Clone do Repositório
```bash
git clone <repository-url>
cd menuerh-system
```

### 2. Configuração do Banco
```bash
# Criar banco de dados
createdb menuerh_db

# Executar migrações
psql -d menuerh_db -f database/migrations/001_create_tables.sql
psql -d menuerh_db -f database/migrations/002_insert_initial_data.sql
```

### 3. Configuração do Sistema
```bash
# Configurar variáveis de ambiente
cp .env.example .env
# Editar .env com suas configurações

# Instalar dependências do React
npm install

# Instalar dependências do Ktor
./gradlew build

# Executar em desenvolvimento
npm run dev
```

## 🌐 URLs de Acesso

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Health Check**: http://localhost:8080/health
- **Dashboard**: http://localhost:3000/dashboard

## 📝 APIs Principais

### Autenticação
- `POST /auth/login` - Login de operador
- `GET /auth/validate` - Validar token

### Leads
- `POST /leads` - Criar novo lead
- `GET /leads` - Listar todos os leads
- `GET /leads/my` - Leads do operador logado
- `GET /leads/{id}` - Detalhes do lead

### Planos
- `GET /plans` - Listar planos disponíveis

### Dashboard
- `GET /dashboard/stats` - Estatísticas do dashboard

## 🔧 Desenvolvimento

### Comandos Úteis

```bash
# Sistema completo
npm run dev              # Desenvolvimento (React + Ktor)
npm run build            # Build para produção
npm run preview          # Preview do build
npm test                 # Executar testes React
./gradlew test           # Executar testes Ktor
```

### Estrutura de Commits
- `feat: [ação]` - Novas funcionalidades
- `fix: [problema]` - Correções de bugs

## 🧪 Testes

### Executar Todos os Testes
```bash
# Script de teste completo
./test-system.sh
```

### Testes Individuais
```bash
# Backend
cd backend && ./gradlew test

# Frontend
cd frontend && npm test

# E2E
cd frontend && npm run cypress:run
```

## 📈 Monitoramento

### Logs
- Backend: `logs/menuerh.log`
- Frontend: Console do navegador

### Métricas
- Health Check: `/health`
- Performance: Tempo de resposta < 500ms
- Uptime: Monitoramento via logs

## 🔒 Segurança

- Autenticação JWT
- Validação de entrada com Zod
- CORS configurado
- Senhas hasheadas (bcrypt)
- Rate limiting (opcional)

## 🚀 Deploy

### Deploy Local
```bash
# Script de deploy
./deploy-local.sh
```

### Deploy Produção
1. Configurar variáveis de ambiente
2. Build do frontend: `npm run build`
3. Build do backend: `./gradlew build`
4. Configurar reverse proxy (nginx)
5. Configurar SSL/TLS

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

## 📞 Suporte

Para dúvidas ou problemas:
- Abra uma issue no GitHub
- Consulte a documentação nas pastas `docs/`
- Verifique os logs de erro

---

**MenuErh** - Sistema simples, eficiente e bem estruturado para gestão de leads. 