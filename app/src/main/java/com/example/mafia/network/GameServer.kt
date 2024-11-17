package com.example.mafia.network



import com.example.mafia.game.GameServerInterface
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.Duration
import java.util.Collections



class GameServer : GameServerInterface {
    val chat = MutableStateFlow(mutableListOf<GameMessage>())
    val users = Collections.synchronizedList<User>(ArrayList())
    private val server by lazy {
        embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
                pingPeriod = Duration.ofSeconds(15)
                timeout = Duration.ofSeconds(15)
                maxFrameSize = Long.MAX_VALUE
                masking = false

            }

            routing{

                webSocket("/chat") {
                    users.add(User(receiveDeserialized<ConnectRequest>().name,this))
                    sendSerialized(chat)
                    while (true) {
                        val newMessage = receiveDeserialized<GameMessage>()
                        chat.update { (it + newMessage).toMutableList() }
                        for (user in users) {
                            user.session.sendSerialized(newMessage)
                        }
                    }
                }
            }
        }
    }

    override fun sendMessage(message: GameMessage) {
        CoroutineScope(Dispatchers.IO).launch {
            chat.update { (it + message).toMutableList() }
            for (user in users) {
                user.session.sendSerialized(message)
            }
        }
    }

    override fun startServer() {
        server.start()
    }

    override fun startGame() {

    }
}



