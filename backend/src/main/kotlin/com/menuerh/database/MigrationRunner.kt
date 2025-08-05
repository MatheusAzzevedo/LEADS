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
                val connection = this.connection.connection as Connection
                
                // Verificar se a coluna vaga_piloto já existe
                val resultSet = connection.prepareStatement("""
                    SELECT column_name 
                    FROM information_schema.columns 
                    WHERE table_name = 'leads' AND column_name = 'vaga_piloto'
                """.trimIndent()).executeQuery()
                
                val columnExists = resultSet.next()
                resultSet.close()
                
                if (!columnExists) {
                    println("🔄 Aplicando migração 004: Adicionando coluna vaga_piloto...")
                    
                    // Adicionar coluna vaga_piloto
                    exec("""
                        ALTER TABLE leads ADD COLUMN vaga_piloto BOOLEAN DEFAULT FALSE
                    """.trimIndent())
                    
                    // Remover coluna selected_plan se existir
                    try {
                        exec("""
                            ALTER TABLE leads DROP COLUMN IF EXISTS selected_plan
                        """.trimIndent())
                    } catch (e: Exception) {
                        println("⚠️ Coluna selected_plan já foi removida ou não existe")
                    }
                    
                    // Remover tabela plans se existir
                    try {
                        exec("""
                            DROP TABLE IF EXISTS plans CASCADE
                        """.trimIndent())
                    } catch (e: Exception) {
                        println("⚠️ Tabela plans já foi removida ou não existe")
                    }
                    
                    // Remover índice relacionado a plans se existir
                    try {
                        exec("""
                            DROP INDEX IF EXISTS idx_plans_plan_id
                        """.trimIndent())
                    } catch (e: Exception) {
                        println("⚠️ Índice idx_plans_plan_id já foi removido ou não existe")
                    }
                    
                    println("✅ Migração 004 aplicada com sucesso!")
                } else {
                    println("✅ Migração 004 já foi aplicada - coluna vaga_piloto existe")
                }
            }
        } catch (e: Exception) {
            println("❌ Erro ao aplicar migração 004: ${e.message}")
            throw e
        }
    }
    
    /**
     * Verifica se todas as migrações necessárias foram aplicadas
     */
    fun verifyMigrations(database: Database): Boolean {
        return try {
            transaction(database) {
                val connection = this.connection.connection as Connection
                
                // Verificar se a coluna vaga_piloto existe
                val resultSet = connection.prepareStatement("""
                    SELECT column_name 
                    FROM information_schema.columns 
                    WHERE table_name = 'leads' AND column_name = 'vaga_piloto'
                """.trimIndent()).executeQuery()
                
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
            false
        }
    }
}