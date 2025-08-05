package com.menuerh.database

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.Column
import java.time.LocalDateTime

object Leads : IntIdTable() {
    val leadId = varchar("lead_id", 20).uniqueIndex()
    val operatorId = reference("operator_id", com.menuerh.database.Operators)
    val firstName = varchar("first_name", 100)
    val lastName = varchar("last_name", 100)
    val email = varchar("email", 255)
    val phone = varchar("phone", 20)
    val position = varchar("position", 100)
    val company = varchar("company", 100)
    val question1Responses = registerColumn<List<String>?>("question1_responses", PostgresTextArrayColumnType())
    val question2Responses = registerColumn<List<String>?>("question2_responses", PostgresTextArrayColumnType())
    val question3Responses = registerColumn<List<String>?>("question3_responses", PostgresTextArrayColumnType())
    val question4Responses = registerColumn<List<String>?>("question4_responses", PostgresTextArrayColumnType())
    val question5Text = text("question5_text").nullable()
    val vagaPiloto = bool("vaga_piloto").default(false)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())
}

@Serializable
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
    val vagaPiloto: Boolean? = false,
    @Serializable(with = com.menuerh.database.LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime? = null,
    @Serializable(with = com.menuerh.database.LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime? = null
)

// Tipo de coluna personalizado para arrays PostgreSQL
class PostgresTextArrayColumnType : org.jetbrains.exposed.sql.ColumnType() {
    override fun sqlType(): String = "TEXT[]"
    
    override fun valueFromDB(value: Any): Any {
        return when (value) {
            is java.sql.Array -> {
                val array = value.array as? Array<*>
                array?.mapNotNull { it?.toString() } ?: emptyList<String>()
            }
            is String -> {
                if (value.isEmpty() || value == "{}") emptyList<String>()
                else {
                    value.removeSurrounding("{", "}")
                        .split(",")
                        .map { it.trim().removeSurrounding("\"") }
                        .filter { it.isNotEmpty() }
                }
            }
            else -> emptyList<String>()
        }
    }
    
    override fun notNullValueToDB(value: Any): Any {
        return when (value) {
            is List<*> -> {
                val connection = org.jetbrains.exposed.sql.transactions.TransactionManager.current().connection
                val jdbcConnection = connection.connection as java.sql.Connection
                jdbcConnection.createArrayOf("TEXT", value.toTypedArray())
            }
            else -> value
        }
    }
}
