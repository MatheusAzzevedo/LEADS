package com.menuerh.api.dashboard

import com.menuerh.api.leads.LeadService
import kotlinx.serialization.Serializable

class DashboardService(
    private val leadService: LeadService
) {
    
    suspend fun getDashboardStats(): DashboardStats {
        val leads = leadService.getAllLeads()
        
        val totalLeads = leads.size
        
        return DashboardStats(
            totalLeads = totalLeads
        )
    }
}

@Serializable
data class DashboardStats(
    val totalLeads: Int
) 