package com.menuerh.database.repositories

import com.menuerh.database.DatabaseConfig.dbQuery
import com.menuerh.database.schema.Plans
import com.menuerh.database.schema.Plan
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class PlanRepository {
    
    suspend fun getAllPlans(): List<Plan> = dbQuery {
        Plans.selectAll().map { row ->
            Plan(
                id = row[Plans.id],
                planId = row[Plans.planId],
                name = row[Plans.name],
                description = row[Plans.description],
                imageUrl = row[Plans.imageUrl],
                isActive = row[Plans.isActive],
                createdAt = row[Plans.createdAt]
            )
        }
    }
    
    suspend fun getPlanById(id: Int): Plan? = dbQuery {
        Plans.select { Plans.id eq id }
            .singleOrNull()
            ?.let { row ->
                Plan(
                    id = row[Plans.id],
                    planId = row[Plans.planId],
                    name = row[Plans.name],
                    description = row[Plans.description],
                    imageUrl = row[Plans.imageUrl],
                    isActive = row[Plans.isActive],
                    createdAt = row[Plans.createdAt]
                )
            }
    }
    
    suspend fun getPlanByPlanId(planId: String): Plan? = dbQuery {
        Plans.select { Plans.planId eq planId }
            .singleOrNull()
            ?.let { row ->
                Plan(
                    id = row[Plans.id],
                    planId = row[Plans.planId],
                    name = row[Plans.name],
                    description = row[Plans.description],
                    imageUrl = row[Plans.imageUrl],
                    isActive = row[Plans.isActive],
                    createdAt = row[Plans.createdAt]
                )
            }
    }
    
    suspend fun getActivePlans(): List<Plan> = dbQuery {
        Plans.select { Plans.isActive eq true }
            .map { row ->
                Plan(
                    id = row[Plans.id],
                    planId = row[Plans.planId],
                    name = row[Plans.name],
                    description = row[Plans.description],
                    imageUrl = row[Plans.imageUrl],
                    isActive = row[Plans.isActive],
                    createdAt = row[Plans.createdAt]
                )
            }
    }
} 