package com.example.mafia.network

import com.example.mafia.game.GameEvent
import io.ktor.server.websocket.WebSocketServerSession
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Message()

@Serializable
data class ConnectRequest(
    val name: String
):Message()

@Serializable
data class ChatMessage(val message: GameMessage):Message()

@Serializable
data class OnConnectMessage(val player:Player,val chat:List<GameMessage>,val players: List<Player>):Message()
@Serializable
data class PlayersUpdateMessage(val player: Player,val players: List<Player>):Message()

@Serializable
data class GameEventMessage(val gameEvent: GameEvent):Message()

@Serializable
data class GameMessage(
    val text: String,
    val sender: Player,
    val hidden: Boolean,
){

    fun getFormattedSender()="${sender.nick}:"
    fun getFormattedText()= text

}
@Serializable
data class Player(
    var nick:String,
    var isAlive:Boolean=true,
    var role:Roles=Roles.UNDEFINED,
    var id:Int=-1
){

    companion object{
        var lastId = 0
        fun generatePlayerWithId(nick: String): Player {
            return Player(nick,id= lastId++)
        }
    }


    enum class Roles {
        UNDEFINED,
        CIVILIAN,
        MAFIA
    }
}


