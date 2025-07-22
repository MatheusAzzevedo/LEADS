# Deploy Monorepo Kotlin/React no Railway

## Estrutura do Projeto

- Backend (Kotlin/Ktor): raiz do projeto, código em `src/main/kotlin/com/menuerh/`, build com Gradle.
- Frontend (React/Vite): raiz do projeto, código em `src/` e `app/`, build com npm/yarn.

---

## 1. Serviços no Railway

- **Crie dois serviços separados:**
  - **Backend:** para o Ktor/Kotlin
  - **Frontend:** para o React/Vite

---

## 2. Configuração do Backend (Ktor/Kotlin)

- **Root Directory:** deixe vazio (raiz do projeto)
- **Start Command:** deixe vazio (usando Dockerfile)
- **Dockerfile:**

```dockerfile
# Etapa de build
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build

# Etapa de execução
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

- **build.gradle.kts:**
```kotlin
import org.gradle.api.file.DuplicatesStrategy

tasks.withType<Jar> {
    manifest {
        attributes(
            "Main-Class" to "com.menuerh.ApplicationKt"
        )
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}
```

- **Variáveis de ambiente:**
  - `DATABASE_URL=jdbc:postgresql://postgres.railway.internal:5432/railway`
  - `DATABASE_USER=postgres`
  - `DATABASE_PASSWORD=<senha_do_railway>`
  - Outras do seu .env.example

- **Watch Paths:**
  - `src/**`
  - `database/**`
  - `build.gradle.kts`

---

## 3. Configuração do Frontend (React/Vite)

- **Root Directory:** vazio (raiz do projeto)
- **Start Command:** `npm run build && npx serve dist`
- **Variáveis de ambiente:**
  - `VITE_API_URL=https://<url-do-backend-no-railway>`
  - `VITE_WS_URL=wss://<url-do-backend-no-railway>`
- **Watch Paths:**
  - `src/**`
  - `package.json`
  - `vite.config.ts`

---

## 4. Banco de Dados (Postgres Railway)

- Use as variáveis fornecidas pelo Railway, mas **no backend** use o formato JDBC:
  - `jdbc:postgresql://postgres.railway.internal:5432/railway`
- Usuário e senha em variáveis separadas.

---

## 5. Dicas e Troubleshooting

- **Erro `no main manifest attribute, in app.jar`:**
  - Corrija o bloco `jar` no `build.gradle.kts` para incluir o atributo `Main-Class`.
- **Erro de arquivos duplicados no JAR:**
  - Adicione `duplicatesStrategy = DuplicatesStrategy.EXCLUDE` no bloco `jar`.
- **Erro de senha do banco:**
  - Use a senha do campo `PGPASSWORD` do serviço de banco do Railway, não a senha da sua conta Railway.
- **Erro de formato da URL do banco:**
  - Use sempre o prefixo `jdbc:` no backend.
- **Backend não sobe:**
  - Confirme que o Dockerfile está na raiz e o comando de start está vazio.
- **Frontend não encontra backend:**
  - Confirme que `VITE_API_URL` está correto e sem barra no final.

---

## 6. Fluxo de Deploy

1. Commit e push no GitHub.
2. Railway detecta mudanças e faz build/deploy de cada serviço.
3. Backend builda com Dockerfile, frontend com Nixpacks.
4. Variáveis de ambiente são aplicadas em cada serviço.
5. Sistema online!

---

**Documentação gerada automaticamente após troubleshooting real.** 