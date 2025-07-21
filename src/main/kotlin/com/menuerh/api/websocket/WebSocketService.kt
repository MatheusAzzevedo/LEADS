package com.menuerh.api.websocket

import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedSendChannelException
import java.util.concurrent.ConcurrentHashMap

class WebSocketService {
    private val connections = ConcurrentHashMap<String, DefaultWebSocketSession>()
    
    suspend fun connect(sessionId: String, session: DefaultWebSocketSession) {
        connections[sessionId] = session
        
        try {
            for (frame in session.incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val text = frame.readText()
                        // Processar mensagem se necessário
                        println("WebSocket message received: $text")
                    }
                    is Frame.Close -> {
                        connections.remove(sessionId)
                        break
                    }
                    else -> {
                        // Ignorar outros tipos de frame
                    }
                }
            }
        } catch (e: ClosedSendChannelException) {
            connections.remove(sessionId)
        }
    }
    
    suspend fun broadcastLeadCreated(leadData: String) {
        connections.values.forEach { session ->
            try {
                session.send(Frame.Text("LEAD_CREATED:$leadData"))
            } catch (e: Exception) {
                // Ignorar erros de conexão fechada
            }
        }
    }
    
    suspend fun broadcastMessage(message: String) {
        connections.values.forEach { session ->
            try {
                session.send(Frame.Text(message))
            } catch (e: Exception) {
                // Ignorar erros de conexão fechada
            }
        }
    }
    
    fun getConnectionCount(): Int = connections.size
} 