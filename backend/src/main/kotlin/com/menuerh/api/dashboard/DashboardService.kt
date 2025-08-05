package com.menuerh.api.dashboard

import com.menuerh.api.leads.LeadService
import kotlinx.serialization.Serializable

class DashboardService(
    private val leadService: LeadService
) {
    
    suspend fun getDashboardStats(): DashboardStats {
        val leads = leadService.getAllLeads()
        
        println("🔍 Debug - DashboardService - Analisando leads:")
        leads.forEach { lead ->
            println("  Lead: ${lead.firstName} ${lead.lastName}")
            println("    question1Responses: ${lead.question1Responses}")
            println("    vagaPiloto: ${lead.vagaPiloto}")
        }
        
        val totalLeads = leads.size
        val elegivelVagaPiloto = leads.count { it.vagaPiloto == true }
        
        // Contar por tipo de interesse - um lead pode ter múltiplos interesses
        val estagiarios = leads.count { lead ->
            lead.question1Responses?.any { it == "Estagiários" } == true
        }
        
        val aprendizes = leads.count { lead ->
            lead.question1Responses?.any { it == "Aprendizes" } == true
        }
        
        val efetivos = leads.count { lead ->
            lead.question1Responses?.any { it == "Efetivos" } == true
        }
        
        println("🔍 Debug - DashboardService - Contadores:")
        println("  Total de leads: $totalLeads")
        println("  Elegível vaga piloto: $elegivelVagaPiloto")
        println("  Estagiários: $estagiarios")
        println("  Aprendizes: $aprendizes")
        println("  Efetivos: $efetivos")
        
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