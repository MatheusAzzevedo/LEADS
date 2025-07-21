package com.menuerh.database.utils

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.jdbc.JdbcConnection
import java.sql.Array as SqlArray

object ArrayUtils {
    
    fun ResultRow.getTextArray(column: org.jetbrains.exposed.sql.Column<String?>): List<String>? {
        val array = this[column]
        return if (array != null) {
            try {
                // Converter string PostgreSQL array para List<String>
                array.removeSurrounding("{", "}")
                    .split(",")
                    .map { it.trim().removeSurrounding("\"") }
                    .filter { it.isNotEmpty() }
            } catch (e: Exception) {
                null
            }
        } else null
    }
    
    fun List<String>?.toPostgresArray(): String? {
        return this?.let { list ->
            if (list.isEmpty()) "{}" else "{${list.joinToString(",") { "\"$it\"" }}}"
        }
    }
    
    fun String?.toTextList(): List<String>? {
        return this?.let { str ->
            if (str.isEmpty() || str == "{}") emptyList()
            else {
                str.removeSurrounding("{", "}")
                    .split(",")
                    .map { it.trim().removeSurrounding("\"") }
                    .filter { it.isNotEmpty() }
            }
        }
    }
} 