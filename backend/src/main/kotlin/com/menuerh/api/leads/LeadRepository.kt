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
                it[question1Responses] = lead.question1Responses
                it[question2Responses] = lead.question2Responses
                it[question3Responses] = lead.question3Responses
                it[question4Responses] = lead.question4Responses
                it[question5Text] = lead.question5Text
                it[vagaPiloto] = lead.vagaPiloto ?: false
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }
            lead.copy(id = id.value)
        }
    }
    
    suspend fun getAllLeads(): List<Lead> {
        return dbQuery {
            try {
                Leads.selectAll()
                    .orderBy(Leads.createdAt, SortOrder.DESC)
                    .map { rowToLead(it) }
            } catch (e: Exception) {
                // Se houver erro (possivelmente coluna vaga_piloto não existe), 
                // tentar buscar sem a coluna vaga_piloto
                if (e.message?.contains("vaga_piloto") == true) {
                    println("⚠️ Coluna vaga_piloto não encontrada, buscando leads sem esta coluna...")
                    Leads.slice(
                        Leads.id, Leads.leadId, Leads.operatorId, Leads.firstName, 
                        Leads.lastName, Leads.email, Leads.phone, Leads.position, 
                        Leads.company, Leads.question1Responses, Leads.question2Responses,
                        Leads.question3Responses, Leads.question4Responses, Leads.question5Text,
                        Leads.createdAt, Leads.updatedAt
                    ).selectAll()
                        .orderBy(Leads.createdAt, SortOrder.DESC)
                        .map { rowToLeadWithoutVagaPiloto(it) }
                } else {
                    throw e
                }
            }
        }
    }
    
    suspend fun getLeadById(leadId: String): Lead? {
        return dbQuery {
            try {
                Leads.select { Leads.leadId eq leadId }
                    .singleOrNull()
                    ?.let { rowToLead(it) }
            } catch (e: Exception) {
                // Se houver erro (possivelmente coluna vaga_piloto não existe), 
                // tentar buscar sem a coluna vaga_piloto
                if (e.message?.contains("vaga_piloto") == true) {
                    println("⚠️ Coluna vaga_piloto não encontrada, buscando lead sem esta coluna...")
                    Leads.slice(
                        Leads.id, Leads.leadId, Leads.operatorId, Leads.firstName, 
                        Leads.lastName, Leads.email, Leads.phone, Leads.position, 
                        Leads.company, Leads.question1Responses, Leads.question2Responses,
                        Leads.question3Responses, Leads.question4Responses, Leads.question5Text,
                        Leads.createdAt, Leads.updatedAt
                    ).select { Leads.leadId eq leadId }
                        .singleOrNull()
                        ?.let { rowToLeadWithoutVagaPiloto(it) }
                } else {
                    throw e
                }
            }
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
            question1Responses = row[Leads.question1Responses],
            question2Responses = row[Leads.question2Responses],
            question3Responses = row[Leads.question3Responses],
            question4Responses = row[Leads.question4Responses],
            question5Text = row[Leads.question5Text],
            vagaPiloto = row[Leads.vagaPiloto],
            createdAt = row[Leads.createdAt],
            updatedAt = row[Leads.updatedAt]
        )
    }

    private fun rowToLeadWithoutVagaPiloto(row: ResultRow): Lead {
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
            question1Responses = row[Leads.question1Responses],
            question2Responses = row[Leads.question2Responses],
            question3Responses = row[Leads.question3Responses],
            question4Responses = row[Leads.question4Responses],
            question5Text = row[Leads.question5Text],
            vagaPiloto = false, // Valor padrão quando a coluna não existe
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
