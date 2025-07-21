# FASE 01 - SETUP DO AMBIENTE DE DESENVOLVIMENTO

## Objetivo
Configurar todo o ambiente de desenvolvimento para o sistema MenuErh com todas as dependências necessárias.

## Tecnologias Definidas
- **Backend**: Ktor (Kotlin)
- **Banco de Dados**: PostgreSQL
- **ORM**: Exposed (Kotlin)
- **Frontend**: React + TypeScript
- **Estilização**: Tailwind CSS
- **Comunicação**: API REST + WebSocket
- **Autenticação**: JWT

## Pré-requisitos
- Java 21 (LTS mais recente)
- Node.js 20+ 
- PostgreSQL 15+
- Git

## 1.1 Instalação do Java 21
```bash
# Windows - Baixar do site oficial Oracle ou usar OpenJDK
# Verificar instalação
java -version
javac -version
```

## 1.2 Instalação do Node.js
```bash
# Baixar Node.js 20+ do site oficial
# Verificar instalação
node --version
npm --version
```

## 1.3 Configuração do PostgreSQL
```bash
# Usar PostgreSQL local instalado
# Criar banco de dados
createdb menuerh_db
# Verificar conexão
psql -d menuerh_db -c "SELECT version();"
```

## 1.4 Estrutura de Diretórios
```
menuerh-system/
├── app/
│   ├── (auth)/
│   │   ├── login/             # Tela de login
│   │   │   ├── page.tsx
│   │   │   └── layout.tsx
│   │   └── register/          # Tela de cadastro
│   │       ├── page.tsx
│   │       └── layout.tsx
│   │
│   ├── dashboard/
│   │   ├── page.tsx           # Dashboard principal
│   │   └── layout.tsx
│   │
│   ├── leads/
│   │   ├── page.tsx           # Tela de cadastro de leads
│   │   └── layout.tsx
│   │
│   ├── plans/
│   │   ├── page.tsx           # Apresentação de planos
│   │   └── layout.tsx
│   │
│   ├── jobs/
│   │   ├── page.tsx           # Publicação de vagas
│   │   └── layout.tsx
│   │
│   ├── api/
│   │   ├── auth/
│   │   │   └── route.kt       # Endpoint de autenticação (Ktor)
│   │   ├── leads/
│   │   │   └── route.kt       # Endpoint cadastro leads
│   │   ├── plans/
│   │   │   └── route.kt       # Endpoint planos
│   │   └── websocket/
│   │       └── route.kt       # WebSocket tempo real
│   │
│   └── layout.tsx             # Layout global do app
│
├── components/
│   ├── ui/                    # Componentes visuais
│   │   ├── button.tsx
│   │   ├── input.tsx
│   │   └── modal.tsx
│   ├── layout/
│   │   ├── sidebar.tsx
│   │   ├── header.tsx
│   │   └── main-layout.tsx
│   └── auth/
│       ├── login-form.tsx
│       └── logout-button.tsx
│
├── lib/
│   ├── auth.ts                # Configuração de autenticação
│   ├── db.ts                  # Conexão Exposed
│   ├── utils.ts               # Funções utilitárias
│   └── validations.ts         # Schemas de validação (Zod/Kotlin)
│
├── database/                  # Schema e migrations Exposed
│   ├── migrations/
│   ├── schema/
│   └── seeds/
│
├── types/
│   ├── auth.ts                # Tipagens
│   └── lead.ts
│
├── build.gradle.kts           # Configuração Gradle (Ktor)
├── package.json               # Configuração Node.js (React)
├── tailwind.config.js         # Configuração Tailwind
├── vite.config.ts             # Configuração Vite
└── middleware.ts              # Middlewares globais
```

## 1.5 Configuração do Gradle (Ktor)
```kotlin
// build.gradle.kts
plugins {
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.20"
    application
}

group = "com.menuerh"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // Ktor
    implementation("io.ktor:ktor-server-core:2.3.7")
    implementation("io.ktor:ktor-server-netty:2.3.7")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
    implementation("io.ktor:ktor-server-cors:2.3.7")
    implementation("io.ktor:ktor-server-auth:2.3.7")
    implementation("io.ktor:ktor-server-auth-jwt:2.3.7")
    implementation("io.ktor:ktor-server-websockets:2.3.7")
    
    // Database
    implementation("org.jetbrains.exposed:exposed-core:0.44.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.44.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.44.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.44.1")
    implementation("org.postgresql:postgresql:42.7.1")
    implementation("com.zaxxer:HikariCP:5.1.0")
    
    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.14")
    
    // Validation
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    
    // Test
    testImplementation("io.ktor:ktor-server-test-host:2.3.7")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.20")
}

application {
    mainClass.set("com.menuerh.ApplicationKt")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}
```

## 1.6 Configuração do Package.json (React)
```json
{
  "name": "menuerh-frontend",
  "version": "1.0.0",
  "private": true,
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.20.1",
    "axios": "^1.6.2",
    "zod": "^3.22.4",
    "react-hook-form": "^7.48.2",
    "@hookform/resolvers": "^3.3.2",
    "socket.io-client": "^4.7.4",
    "lucide-react": "^0.294.0",
    "clsx": "^2.0.0",
    "tailwind-merge": "^2.0.0"
  },
  "devDependencies": {
    "@types/react": "^18.2.45",
    "@types/react-dom": "^18.2.18",
    "@types/node": "^20.10.4",
    "@vitejs/plugin-react": "^4.2.1",
    "autoprefixer": "^10.4.16",
    "postcss": "^8.4.32",
    "tailwindcss": "^3.3.6",
    "typescript": "^5.3.3",
    "vite": "^5.0.8"
  },
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "preview": "vite preview"
  }
}
```

## 1.7 Configuração do Tailwind CSS
```javascript
// tailwind.config.js
/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#eff6ff',
          500: '#3b82f6',
          600: '#2563eb',
          700: '#1d4ed8',
        }
      }
    },
  },
  plugins: [],
}
```

## 1.8 Variáveis de Ambiente
```bash
# .env
DATABASE_URL=jdbc:postgresql://localhost:5432/menuerh_db
DATABASE_USER=postgres
DATABASE_PASSWORD=sua_senha
JWT_SECRET=seu_jwt_secret_muito_seguro_aqui
SERVER_PORT=8080
```

## 1.9 Comandos de Verificação
```bash
# Verificar Java
java -version

# Verificar Gradle
./gradlew --version

# Verificar Node.js
node --version
npm --version

# Verificar PostgreSQL
psql --version

# Testar conexão com banco
psql -d menuerh_db -c "SELECT 1;"
```

## 1.10 Próximos Passos
- [ ] Instalar todas as dependências
- [ ] Configurar IDE (IntelliJ IDEA / VS Code)
- [ ] Testar conexão com banco de dados
- [ ] Verificar se todos os comandos funcionam
- [ ] Configurar Git hooks (opcional)

## Tempo Estimado: 2-3 horas 