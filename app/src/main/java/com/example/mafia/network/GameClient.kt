package com.example.mafia.network

import androidx.lifecycle.MutableLiveData
import com.example.mafia.game.GameClientInterface
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


class GameClient : GameClientInterface{
    lateinit var session: DefaultClientWebSocketSession
    private val client by lazy {
        HttpClient(OkHttp) {
            engine {
                config {
                    followRedirects(true)
                }
                preconfigured = OkHttpClient.Builder()
                    .pingInterval(15, TimeUnit.SECONDS)
                    .build()
            }
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }

        }
    }



    override fun sendMessage(message: GameMessage) {
        CoroutineScope(Dispatchers.IO).launch{
            session.sendSerialized(message)
        }
    }

    override fun connect(host: String, username: String) {
        CoroutineScope(Dispatchers.IO).launch{
            session = client.webSocketSession ( method = HttpMethod.Post, host = host, port = 8080 , path="/chat")
            session.sendSerialized(ConnectRequest(username))
            session.receiveDeserialized<GameMessage>().text
        }
    }
}