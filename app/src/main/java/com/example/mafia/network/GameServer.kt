package com.example.mafia.network



import android.util.Log
import com.example.mafia.game.Game
import com.example.mafia.game.GameEvent
import com.example.mafia.game.GameServerInterface
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.time.Duration
import java.util.Collections


class GameServer(val scope: CoroutineScope) : GameServerInterface {
    override var player = MutableStateFlow(Player(""))
    override val time: MutableStateFlow<String> = MutableStateFlow<String>("00:00")
    override val chat = MutableStateFlow(mutableListOf<GameMessage>())
    override val lastChangeTimeEvent: MutableStateFlow<GameEvent.ServerGameEvent> = MutableStateFlow(GameEvent.DayGameEvent())
    override val players:MutableStateFlow<MutableList<Player>> = MutableStateFlow(mutableListOf())
    val game = Game(chat,players)
    init {
        scope.launch {
            game.timer.gameTime.collect{
                time.value=it.toString()
            }
        }

        scope.launch {
            players.collect{ newPlayers->
                player.update { player->newPlayers.find { it.id==player.id } ?: player}
            }
        }
    }
    private val server by lazy {
        embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
            install(WebSockets) {
                contentConverter =KotlinxWebsocketSerializationConverter(Json)
                pingPeriod = Duration.ofSeconds(15)
                timeout = Duration.ofSeconds(15)
                maxFrameSize = Long.MAX_VALUE
                masking = false
            }

            routing{
                webSocket("/game") {
                    var clientPlayerId = -1
                    scope.launch {
                        withContext(Dispatchers.IO){
                            game.gameStream.collect{ gameEvent ->
                                when(gameEvent){
                                    is GameEvent.KillClientGameEvent -> sendSerialized<Message>(GameEventMessage(gameEvent))
                                    is GameEvent.ProsecutionClientGameEvent -> sendSerialized<Message>(GameEventMessage(gameEvent))
                                    is GameEvent.DayGameEvent -> {
                                        sendSerialized<Message>(GameEventMessage(gameEvent))
                                        lastChangeTimeEvent.update{ gameEvent }
                                    }
                                    is GameEvent.NightGameEvent -> {
                                        sendSerialized<Message>(GameEventMessage(gameEvent))
                                        lastChangeTimeEvent.update{ gameEvent}
                                    }
                                    is GameEvent.StartGameEvent -> sendSerialized<Message>(GameEventMessage(gameEvent))
                                    is GameEvent.TimerSyncGameEvent -> sendSerialized<Message>(GameEventMessage(gameEvent))
                                }
                            }
                        }
                    }
                    scope.launch{
                        withContext(Dispatchers.IO) {
                            chat.collectIndexed { index, value ->
                                if (index > 0) {
                                    sendSerialized(ChatMessage(value.last())as Message)
                                }
                            }
                        }
                    }
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            players.collect {newPlayers->
                                sendSerialized(PlayersUpdateMessage(newPlayers.find { player->player.id==clientPlayerId } ?: Player("")
                                    ,newPlayers)as Message)
                            }
                        }
                    }

                    while (true){
                        val message = receiveDeserialized<Message>()
//                        val clientPlayer = players.value.find { it.id==clientPlayerId } ?: Player("")
//                        if (clientPlayer.isAlive) {
                            messageHandler(message, this, { clientPlayerId = it })
//                        }
                    }
                }

            }
        }
    }

    private suspend fun messageHandler(
        message: Message,
        session: DefaultWebSocketServerSession,
        updateClientPlayerId: (Int)->Unit
    ) {
        Log.d("messageHandler:",message.toString())
            when(message){
                is ChatMessage -> {
                    chat.update { if(message.message.sender.isAlive) (it + message.message).toMutableList() else it }
                }
                is ConnectRequest -> {
                    val clientPlayer = Player.generatePlayerWithId(message.name)
                    updateClientPlayerId(clientPlayer.id)
                    players.update { (it+clientPlayer).toMutableList() }
                    session.sendSerialized(OnConnectMessage(clientPlayer,chat.value,players.value)as Message)
                }
                is GameEventMessage -> gameEventHandler(message.gameEvent)
                else -> {}
            }
    }

    private fun gameEventHandler(event: GameEvent) {
    when(event){
        is GameEvent.KillClientGameEvent -> game.gameStream.update { event }
        is GameEvent.ProsecutionClientGameEvent -> game.gameStream.update { event }
        is GameEvent.DayGameEvent -> lastChangeTimeEvent.update { event }
        is GameEvent.NightGameEvent -> lastChangeTimeEvent.update { event }
        is GameEvent.StartGameEvent -> TODO()
        is GameEvent.TimerSyncGameEvent -> TODO()
    }
    }

    override fun sendMessage(message: String) {
        scope.launch {
            chat.update { if (player.value.isAlive)(it + GameMessage(message,player.value,false)).toMutableList() else it}
        }
    }

    override fun confirmVote(victim: Player) {
        scope.launch {
            withContext(Dispatchers.IO){
                val event:GameEvent.ClientGameEvent= if (player.value.role==Player.Roles.MAFIA) GameEvent.KillClientGameEvent(player.value,victim)
                    else GameEvent.ProsecutionClientGameEvent(player.value,victim)
                game.gameStream.update { event }
            }
        }
    }

    override fun startServer() {
        server.start()
    }

    override fun startGame() {
        scope.launch {
            withContext(Dispatchers.IO){
                game.gameStream.value = GameEvent.StartGameEvent()
            }
        }
        game.start()
    }
}



