package com.menuerh.api.leads

import com.menuerh.database.DatabaseConfig.dbQuery
import com.menuerh.database.Leads
import com.menuerh.database.Lead
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime
class LeadRepository {
    
    suspend fun createLead(lead: Lead): Lead {
        return dbQuery {
            val id = Leads.insertAndGetId {
                it[leadId] = lead.leadId
                it[operatorId] = lead.operatorId
                it[firstName] = lead.firstName
                it[lastName] = lead.lastName
                it[email] = lead.email
                it[phone] = lead.phone
                it[position] = lead.position
                it[company] = lead.company
                it[interest] = lead.interest?.joinToString(",")
                it[hiring_flow] = lead.hiring_flow
                it[investment_value] = lead.investment_value
                it[eligible_for_pilot] = lead.eligible_for_pilot
                it[selectedPlan] = lead.selectedPlan
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
            lead.copy(id = id.value)
        }
    }
    
    suspend fun getAllLeads(): List<Lead> {
        return dbQuery {
            Leads.selectAll()
                .orderBy(Leads.createdAt, SortOrder.DESC)
                .map { rowToLead(it) }
        }
    }
    
    suspend fun getLeadById(leadId: String): Lead? {
        return dbQuery {
            Leads.select { Leads.leadId eq leadId }
                .singleOrNull()
                ?.let { rowToLead(it) }
        }
    }
    
    suspend fun getLeadsByOperator(operatorId: Int): List<Lead> {
        return dbQuery {
            Leads.select { Leads.operatorId eq operatorId }
                .orderBy(Leads.createdAt, SortOrder.DESC)
                .map { rowToLead(it) }
        }
    }
    
    private fun rowToLead(row: ResultRow): Lead {
        return Lead(
            id = row[Leads.id].value,
            leadId = row[Leads.leadId],
            operatorId = row[Leads.operatorId].value,
            firstName = row[Leads.firstName],
            lastName = row[Leads.lastName],
            email = row[Leads.email],
            phone = row[Leads.phone],
            position = row[Leads.position],
            company = row[Leads.company],
            interest = row[Leads.interest]?.split(","),
            hiring_flow = row[Leads.hiring_flow],
            investment_value = row[Leads.investment_value],
            eligible_for_pilot = row[Leads.eligible_for_pilot],
            selectedPlan = row[Leads.selectedPlan],
            createdAt = row[Leads.createdAt],
            updatedAt = row[Leads.updatedAt]
        )
    }

    suspend fun deleteLeadById(leadId: String): Boolean {
        return dbQuery {
            Leads.deleteWhere { Leads.leadId eq leadId } > 0
        }
    }
} 