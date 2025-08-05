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

        println("🔍 Verificando variáveis de ambiente para conexão com banco de dados...")
        
        val databaseUrl = System.getenv("DATABASE_URL")
        val isProduction = !databaseUrl.isNullOrBlank()

        val config = HikariConfig()
        config.driverClassName = driverClassName

        if (isProduction) {
            println("🌱 Ambiente de Produção (Railway) detectado.")
            config.jdbcUrl = databaseUrl
            config.username = System.getenv("DATABASE_USER")
            config.password = System.getenv("DATABASE_PASSWORD")
        } else {
            println("🏠 Ambiente de Desenvolvimento (Local) detectado.")
            val dbHost = System.getenv("PGHOST") ?: "localhost"
            val dbPort = System.getenv("PGPORT") ?: "5432"
            val dbName = System.getenv("PGDATABASE") ?: "menuerh_db"
            config.jdbcUrl = "jdbc:postgresql://$dbHost:$dbPort/$dbName"
            config.username = System.getenv("PGUSER") ?: "postgres"
            config.password = System.getenv("PGPASSWORD") ?: "admin123"
        }

        println("🔗 Tentando conectar ao banco: ${config.jdbcUrl}")
        println("👤 Usuário: ${config.username}")

        config.maximumPoolSize = System.getenv("DATABASE_POOL_SIZE")?.toIntOrNull() ?: 10
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.connectionTimeout = 30000L // 30 seconds
        config.idleTimeout = 600000L // 10 minutes
        config.maxLifetime = 1800000L // 30 minutes
        
        try {
            config.validate()
        } catch (e: Exception) {
            println("❌ Configuração do Hikari inválida: ${e.message}")
            throw e
        }

        val dataSource = HikariDataSource(config)
        val database = Database.connect(dataSource)

        try {
            println("🔄 Verificando e aplicando migrações...")
            MigrationRunner.applyMigration004(database)
            println("✅ Migrações aplicadas com sucesso.")
        } catch (e: Exception) {
            println("⚠️ Erro ao aplicar migrações: ${e.message}")
            // É importante falhar se as migrações não puderem ser aplicadas para garantir a consistência do schema.
            throw IllegalStateException("Falha ao aplicar migrações do banco de dados", e)
        }

        println("✅ Conexão com o banco de dados inicializada com sucesso.")
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
        
    fun <T> dbQuerySync(block: () -> T): T =
        transaction { block() }
}
