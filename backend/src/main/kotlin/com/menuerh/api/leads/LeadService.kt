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
            question1Responses = leadData.question1Responses,
            question2Responses = leadData.question2Responses,
            question3Responses = leadData.question3Responses,
            question4Responses = leadData.question4Responses,
            question5Text = leadData.question5Text,
            vagaPiloto = leadData.vagaPiloto
        )
        
        return leadRepository.createLead(lead)
    }
    
    suspend fun getAllLeads(): List<Lead> {
        return leadRepository.getAllLeads()
    }
    
    suspend fun getLeadById(leadId: String): Lead? {
        return leadRepository.getLeadById(leadId)
    }
    
    private fun generateLeadId(): String {
        return System.currentTimeMillis().toString()
    }

    suspend fun deleteLeadById(leadId: String): Boolean {
        return leadRepository.deleteLeadById(leadId)
    }
}
