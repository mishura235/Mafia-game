package com.example.mafia.game

import com.example.mafia.network.GameMessage
import com.example.mafia.network.Player
import kotlinx.coroutines.flow.MutableStateFlow

interface NetworkInterface{
    val player:MutableStateFlow<Player>
    val time: MutableStateFlow<String>
    val players:MutableStateFlow<MutableList<Player>>
    val chat:MutableStateFlow<MutableList<GameMessage>>
    val lastChangeTimeEvent:MutableStateFlow<GameEvent.ServerGameEvent>
    fun sendMessage(message: String)
    fun confirmVote(victim: Player)
}
interface GameClientInterface : NetworkInterface {
    fun connect(host:String,username:String)
}
interface GameServerInterface:NetworkInterface{

    fun startServer()
    fun startGame()
}



