package com.example.mafia.network

import com.example.mafia.game.Game

object GameNetworking:Game {
    lateinit var networkCore:Game
    override fun sendMessage(message: GameMessage){
        networkCore.sendMessage(message)
    }
}