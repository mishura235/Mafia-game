package com.example.mafia

import kotlinx.serialization.Serializable

class Game() {


    val chat: MutableList<GameMessage> = mutableListOf()

    fun start() {

    }
}

@Serializable
data class GameMessage(
    val text: String,
    val sender: String,
    val hidden: Boolean
)
