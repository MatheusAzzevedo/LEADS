package com.menuerh.api.websocket

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import java.util.*

fun Application.WebSocketRoutes() {
    val webSocketService = WebSocketService()
    
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    
    routing {
        webSocket("/ws") {
            val sessionId = UUID.randomUUID().toString()
            webSocketService.connect(sessionId, this)
        }
    }
} 