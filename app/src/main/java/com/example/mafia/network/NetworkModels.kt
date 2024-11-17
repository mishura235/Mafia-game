package com.example.mafia.network

import android.annotation.SuppressLint
import io.ktor.server.websocket.WebSocketServerSession
import kotlinx.serialization.Serializable

@Serializable
data class ConnectRequest(
    val name: String
)
@Serializable
data class GameMessage(
    val text: String,
    val sender: String,
    val hidden: Boolean,
)

data class User(
    val name: String,
    val session: WebSocketServerSession
)