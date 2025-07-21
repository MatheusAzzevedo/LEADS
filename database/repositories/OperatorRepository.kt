package com.menuerh.database.repositories

import com.menuerh.database.DatabaseConfig.dbQuery
import com.menuerh.database.schema.Operators
import com.menuerh.database.schema.Operator
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class OperatorRepository {
    
    suspend fun getAllOperators(): List<Operator> = dbQuery {
        Operators.selectAll().map { row ->
            Operator(
                id = row[Operators.id],
                username = row[Operators.username],
                name = row[Operators.name],
                isActive = row[Operators.isActive],
                createdAt = row[Operators.createdAt],
                updatedAt = row[Operators.updatedAt]
            )
        }
    }
    
    suspend fun getOperatorById(id: Int): Operator? = dbQuery {
        Operators.select { Operators.id eq id }
            .singleOrNull()
            ?.let { row ->
                Operator(
                    id = row[Operators.id],
                    username = row[Operators.username],
                    name = row[Operators.name],
                    isActive = row[Operators.isActive],
                    createdAt = row[Operators.createdAt],
                    updatedAt = row[Operators.updatedAt]
                )
            }
    }
    
    suspend fun getOperatorByUsername(username: String): Operator? = dbQuery {
        Operators.select { Operators.username eq username }
            .singleOrNull()
            ?.let { row ->
                Operator(
                    id = row[Operators.id],
                    username = row[Operators.username],
                    name = row[Operators.name],
                    isActive = row[Operators.isActive],
                    createdAt = row[Operators.createdAt],
                    updatedAt = row[Operators.updatedAt]
                )
            }
    }
    
    suspend fun getActiveOperators(): List<Operator> = dbQuery {
        Operators.select { Operators.isActive eq true }
            .map { row ->
                Operator(
                    id = row[Operators.id],
                    username = row[Operators.username],
                    name = row[Operators.name],
                    isActive = row[Operators.isActive],
                    createdAt = row[Operators.createdAt],
                    updatedAt = row[Operators.updatedAt]
                )
            }
    }
} 