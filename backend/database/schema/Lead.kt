package com.menuerh.database.schema

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Leads : IntIdTable() {
    val leadId = varchar("lead_id", 20).uniqueIndex()
    val operatorId = reference("operator_id", Operators)
    val firstName = varchar("first_name", 100)
    val lastName = varchar("last_name", 100)
    val email = varchar("email", 255)
    val phone = varchar("phone", 20)
    val position = varchar("position", 100)
    val company = varchar("company", 100)
    val question1Responses = text("question1_responses").nullable()
    val question2Responses = text("question2_responses").nullable()
    val question3Responses = text("question3_responses").nullable()
    val question4Responses = text("question4_responses").nullable()
    val question5Text = text("question5_text").nullable()
    val selectedPlan = varchar("selected_plan", 50).nullable()
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())
}

data class Lead(
    val id: Int? = null,
    val leadId: String,
    val operatorId: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val position: String,
    val company: String,
    val question1Responses: List<String>? = null,
    val question2Responses: List<String>? = null,
    val question3Responses: List<String>? = null,
    val question4Responses: List<String>? = null,
    val question5Text: String? = null,
    val selectedPlan: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) 