package com.menuerh.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.Column
import java.sql.Connection

/**
 * Classe responsável por executar migrações de banco de dados
 */
object MigrationRunner {
    
    /**
     * Aplica a migração 004 - Adiciona coluna vaga_piloto
     */
    fun applyMigration004(database: Database) {
        try {
            transaction(database) {
                // Obter a conexão JDBC da transação do Exposed
                val connection = (this.connection as org.jetbrains.exposed.sql.statements.jdbc.JdbcConnectionImpl).connection
                
                // Verificar se a coluna vaga_piloto já existe
                val checkQuery = """
                    SELECT column_name 
                    FROM information_schema.columns 
                    WHERE table_name = 'leads' AND column_name = 'vaga_piloto'
                """.trimIndent()
                
                val resultSet = connection.prepareStatement(checkQuery).executeQuery()
                val columnExists = resultSet.next()
                resultSet.close()
                
                if (!columnExists) {
                    println("🔄 Aplicando migração 004: Adicionando coluna vaga_piloto...")
                    
                    // Adicionar coluna vaga_piloto
                    connection.prepareStatement("""
                        ALTER TABLE leads ADD COLUMN vaga_piloto BOOLEAN DEFAULT FALSE
                    """.trimIndent()).execute()
                    
                    println("✅ Migração 004 (vaga_piloto) aplicada com sucesso!")
                } else {
                    println("✅ Migração 004 já foi aplicada - coluna vaga_piloto existe")
                }
            }
        } catch (e: Exception) {
            println("❌ Erro ao aplicar migração 004: ${e.message}")
            e.printStackTrace()
            // Não propagar o erro para não quebrar a aplicação
        }
    }
    
    /**
     * Verifica se todas as migrações necessárias foram aplicadas
     */
    fun verifyMigrations(database: Database): Boolean {
        return try {
            transaction(database) {
                // Obter a conexão JDBC da transação do Exposed
                val connection = (this.connection as org.jetbrains.exposed.sql.statements.jdbc.JdbcConnectionImpl).connection
                
                // Verificar se a coluna vaga_piloto existe
                val checkQuery = """
                    SELECT column_name 
                    FROM information_schema.columns 
                    WHERE table_name = 'leads' AND column_name = 'vaga_piloto'
                """.trimIndent()
                
                val resultSet = connection.prepareStatement(checkQuery).executeQuery()
                val hasVagaPiloto = resultSet.next()
                resultSet.close()
                
                if (!hasVagaPiloto) {
                    println("❌ Migração necessária: coluna vaga_piloto não existe")
                    return@transaction false
                }
                
                println("✅ Todas as migrações estão aplicadas")
                true
            }
        } catch (e: Exception) {
            println("❌ Erro ao verificar migrações: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}