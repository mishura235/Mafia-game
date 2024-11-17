package com.example.mafia.game

import com.example.mafia.network.GameMessage
import kotlinx.serialization.Serializable

interface Game{
    fun sendMessage(message: GameMessage)
}
interface GameClientInterface : Game {
    fun connect(host:String,username:String)
}
interface GameServerInterface:Game{
    fun startServer()
    fun startGame()
}



