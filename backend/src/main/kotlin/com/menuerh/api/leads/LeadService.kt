package com.menuerh.api.leads

import com.menuerh.database.Lead
import java.time.LocalDateTime

class LeadService(private val leadRepository: LeadRepository) {
    
    suspend fun createLead(leadData: CreateLeadRequest, operatorId: Int): Lead {
        val leadId = generateLeadId()
        
        val lead = Lead(
            leadId = leadId,
            operatorId = operatorId,
            firstName = leadData.firstName,
            lastName = leadData.lastName,
            email = leadData.email,
            phone = leadData.phone,
            position = leadData.position,
            company = leadData.company,
            interest = leadData.interest,
            hiring_flow = leadData.hiring_flow,
            investment_value = leadData.investment_value,
            eligible_for_pilot = leadData.eligible_for_pilot,
            selectedPlan = leadData.selectedPlan
        )
        
        return leadRepository.createLead(lead)
    }
    
    suspend fun getAllLeads(): List<Lead> {
        return leadRepository.getAllLeads()
    }
    
    suspend fun getLeadById(leadId: String): Lead? {
        return leadRepository.getLeadById(leadId)
    }
    
    suspend fun getLeadsByOperator(operatorId: Int): List<Lead> {
        return leadRepository.getLeadsByOperator(operatorId)
    }
    
    private fun generateLeadId(): String {
        return System.currentTimeMillis().toString()
    }

    suspend fun deleteLeadById(leadId: String): Boolean {
        return leadRepository.deleteLeadById(leadId)
    }
} 