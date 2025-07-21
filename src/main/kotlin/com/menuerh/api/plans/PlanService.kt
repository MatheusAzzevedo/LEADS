package com.menuerh.api.plans

import com.menuerh.database.DatabaseConfig.dbQuery
import com.menuerh.database.Plans
import com.menuerh.database.Plan
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class PlanService {
    
    suspend fun getAllPlans(): List<Plan> {
        return dbQuery {
            try {
                println("DEBUG: Executando consulta de planos...")
                val result = Plans.select { Plans.isActive eq true }
                    .map { rowToPlan(it) }
                println("DEBUG: Encontrados ${result.size} planos")
                result
            } catch (e: Exception) {
                println("DEBUG: Erro na consulta de planos: ${e.message}")
                e.printStackTrace()
                throw e
            }
        }
    }
    
    private fun rowToPlan(row: org.jetbrains.exposed.sql.ResultRow): Plan {
        return Plan(
            id = row[Plans.id].value,
            planId = row[Plans.planId],
            name = row[Plans.name],
            description = row[Plans.description],
            imageUrl = row[Plans.imageUrl],
            isActive = row[Plans.isActive],
            createdAt = row[Plans.createdAt]
        )
    }
} 