package com.example.mafia.network

import android.util.Log
import com.example.mafia.game.GameClientInterface
import com.example.mafia.game.GameEvent
import com.example.mafia.game.GameTimer
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.util.reflect.instanceOf
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


class GameClient(val scope: CoroutineScope) : GameClientInterface{
    override var player = MutableStateFlow(Player(""))
    val handler = CoroutineExceptionHandler{ context , exception ->
        println("CoroutineExceptionHandler got $exception in $context")
    }
    override val chat: MutableStateFlow<MutableList<GameMessage>> = MutableStateFlow(mutableListOf())
    override val lastChangeTimeEvent: MutableStateFlow<GameEvent.ServerGameEvent> = MutableStateFlow(GameEvent.DayGameEvent())
    lateinit var session: DefaultClientWebSocketSession
    override val time = MutableStateFlow("00:00")
    val timer = GameTimer()
    override val players:MutableStateFlow<MutableList<Player>> = MutableStateFlow(mutableListOf())
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
            install(Logging) {
                logger=Logger.ANDROID
                level = LogLevel.ALL
            }
        }
    }
    init {
        scope.launch {
            timer.gameTime.collect{gameTime->
                time.update {gameTime.toString()}
            }
        }
    }
    override fun sendMessage(message: String) {
        scope.launch(handler){
            withContext(Dispatchers.IO){
                session.sendSerialized<Message>(ChatMessage(GameMessage(message, player.value,false)))
            }
        }
    }

    override fun confirmVote(victim: Player) {
        scope.launch(handler) {
            withContext(Dispatchers.IO){
                val event = if (player.value.role==Player.Roles.MAFIA) GameEvent.KillClientGameEvent(player.value,victim)
                else GameEvent.ProsecutionClientGameEvent(player.value,victim)
                Log.d("confirmVote:",event.toString())
                session.sendSerialized<Message>(GameEventMessage(event))
            }
        }
    }

    override fun connect(host: String, username: String) {
        player.update {Player(username)}
        scope.launch() {
            withContext(Dispatchers.IO) {
                session = client.webSocketSession(
                    method = HttpMethod.Get,
                    host = host,
                    port = 8080,
                    path = "/game"
                )
                session.sendSerialized(ConnectRequest(username)as Message)
                while (true) {
                    val message = session.receiveDeserialized<Message>()
                    messageHandler(message)
                }
            }
        }
    }

    private fun messageHandler(message: Message) {
        Log.d("messageHandler: ", message.toString())
        when(message){
            is ChatMessage -> {
                chat.update { (it+message.message).toMutableList() }
            }
            is ConnectRequest -> {}
            is GameEventMessage -> {
                gameEventHandler(message.gameEvent)
            }
            is OnConnectMessage -> {
                player.update {message.player}
                chat.update { (it+message.chat).toMutableList()}
                players.update { message.players.toMutableList() }
            }
            is PlayersUpdateMessage -> {
                player.update { message.player }
                players.update { message.players.toMutableList() }
            }
        }
    }

    private fun gameEventHandler(event: GameEvent){
        when (event){
            is GameEvent.DayGameEvent -> {lastChangeTimeEvent.update { event }}
            is GameEvent.NightGameEvent -> {lastChangeTimeEvent.update { event }}
            is GameEvent.StartGameEvent -> {timer.start()}
            is GameEvent.TimerSyncGameEvent ->{
                timer.gameTime.update { event.time }
            }
            else -> {}
        }
    }

}