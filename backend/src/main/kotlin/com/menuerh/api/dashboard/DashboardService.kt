package com.menuerh.api.dashboard

import com.menuerh.api.leads.LeadService
import kotlinx.serialization.Serializable

class DashboardService(
    private val leadService: LeadService
) {
    
    suspend fun getDashboardStats(): DashboardStats {
        val leads = leadService.getAllLeads()
        
        val totalLeads = leads.size
        val elegivelVagaPiloto = leads.count { it.vagaPiloto == true }
        
        // Contar por tipo de interesse
        val estagiarios = leads.count { lead ->
            lead.question1Responses?.contains("Estagiários") == true
        }
        
        val aprendizes = leads.count { lead ->
            lead.question1Responses?.contains("Aprendizes") == true
        }
        
        val efetivos = leads.count { lead ->
            lead.question1Responses?.contains("Efetivos") == true
        }
        
        return DashboardStats(
            totalLeads = totalLeads,
            elegivelVagaPiloto = elegivelVagaPiloto,
            estagiarios = estagiarios,
            aprendizes = aprendizes,
            efetivos = efetivos
        )
    }
}

@Serializable
data class DashboardStats(
    val totalLeads: Int,
    val elegivelVagaPiloto: Int,
    val estagiarios: Int,
    val aprendizes: Int,
    val efetivos: Int
) 