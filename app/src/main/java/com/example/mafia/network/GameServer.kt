package com.example.mafia.network


import com.example.mafia.Game
import com.example.mafia.GameMessage
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSocketServerSession
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import kotlinx.serialization.json.Json
import java.time.Duration
import java.util.Collections


class GameServer(val game: Game) {
    private val server by lazy {
        embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
                pingPeriod = Duration.ofSeconds(15)
                timeout = Duration.ofSeconds(15)
                maxFrameSize = Long.MAX_VALUE
                masking = false
            }

            routing {
                val sessions = Collections.synchronizedList<WebSocketServerSession>(ArrayList())
                webSocket("/chat") {
                    sessions.add(this)
                    sendSerialized(game.chat)
                    while (true) {
                        val newMessage = receiveDeserialized<GameMessage>()
                        game.chat.add(newMessage)
                        for (session in sessions) {
                            session.sendSerialized(newMessage)
                        }
                    }
                }
            }
        }
    }

    fun startRoomServer() {
        server.start()
        game.start()
    }
}



