package com.menuerh.api.dashboard

import com.menuerh.api.leads.LeadService
import com.menuerh.api.plans.PlanService
import com.menuerh.database.Plan
import kotlinx.serialization.Serializable

class DashboardService(
    private val leadService: LeadService,
    private val planService: PlanService
) {
    
    suspend fun getDashboardStats(): DashboardStats {
        val leads = leadService.getAllLeads()
        val plans = planService.getAllPlans()
        
        val totalLeads = leads.size
        val leadsByPlan = leads.groupBy { it.selectedPlan ?: "Nenhum" }
        
        return DashboardStats(
            totalLeads = totalLeads,
            leadsByPlan = leadsByPlan.mapValues { it.value.size },
            plans = plans
        )
    }
}

@Serializable
data class DashboardStats(
    val totalLeads: Int,
    val leadsByPlan: Map<String, Int>,
    val plans: List<Plan>
) 