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
        val poolSize = System.getenv("DATABASE_POOL_SIZE")?.toIntOrNull() ?: 10

        val config = HikariConfig().apply {
            this.driverClassName = driverClassName
            this.jdbcUrl = jdbcURL
            this.username = username
            this.password = password
            this.maximumPoolSize = poolSize
            this.isAutoCommit = false
            this.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            this.connectionTimeout = 30000L
            this.idleTimeout = 600000L
            this.maxLifetime = 1800000L
            validate()
        }

        val dataSource = HikariDataSource(config)
        val database = Database.connect(dataSource)
        
        // Aplicar migrações necessárias
        try {
            println("🔄 Verificando e aplicando migrações...")
            MigrationRunner.applyMigration004(database)
        } catch (e: Exception) {
            println("⚠️ Erro ao aplicar migrações: ${e.message}")
            // Continuar mesmo com erro de migração para não quebrar a aplicação
        }
        
        println("✅ Database connection initialized successfully")
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
        
    fun <T> dbQuerySync(block: () -> T): T =
        transaction { block() }
} 