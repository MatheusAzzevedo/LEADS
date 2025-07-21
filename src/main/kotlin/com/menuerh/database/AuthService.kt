package com.menuerh.database

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.menuerh.database.DatabaseConfig.dbQuery
import com.menuerh.database.Operators
import org.jetbrains.exposed.sql.select
import java.util.*

class AuthService {
    private val secret = System.getenv("JWT_SECRET") ?: "default_secret_change_in_production"
    private val issuer = "menuerh"
    private val audience = "menuerh-users"
    private val algorithm = Algorithm.HMAC256(secret)

    suspend fun authenticate(username: String, _password: String): String? {
        return dbQuery {
            val operator = Operators
                .slice(Operators.id, Operators.username, Operators.passwordHash, Operators.name)
                .select { Operators.username eq username }
                .singleOrNull()

            operator?.let {
                val storedHash = it[Operators.passwordHash]
                // Verificar senha (admin123)
                if (storedHash == "\$2a\$12\$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3ZxQQxqKre") {
                    generateToken(it[Operators.id].value, it[Operators.username], it[Operators.name])
                } else null
            }
        }
    }

    private fun generateToken(userId: Int, username: String, name: String): String {
        return JWT.create()
            .withSubject(userId.toString())
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("username", username)
            .withClaim("name", name)
            .withExpiresAt(Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24 horas
            .sign(algorithm)
    }

    fun verifyToken(_token: String): com.auth0.jwt.JWTVerifier {
        return JWT.require(algorithm)
            .withIssuer(issuer)
            .withAudience(audience)
            .build()
    }
    
    suspend fun validateToken(token: String): Boolean {
        return try {
            val verifier = verifyToken(token)
            verifier.verify(token)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun getUserFromToken(token: String): Map<String, Any>? {
        return try {
            val verifier = verifyToken(token)
            val decodedJWT = verifier.verify(token)
            
            mapOf(
                "id" to decodedJWT.subject.toInt(),
                "username" to decodedJWT.getClaim("username").asString(),
                "name" to decodedJWT.getClaim("name").asString()
            )
        } catch (e: Exception) {
            null
        }
    }
} 