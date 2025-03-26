package com.example.mafia.network

import com.example.mafia.game.GameEvent
import com.example.mafia.game.NetworkInterface
import kotlinx.coroutines.flow.MutableStateFlow

object GameNetworking:NetworkInterface {
    private lateinit var _networkCore:NetworkInterface
    fun setNetworkCore(networkCore:NetworkInterface){
        _networkCore=networkCore
    }
    override val chat: MutableStateFlow<MutableList<GameMessage>>
        get() = _networkCore.chat
    override val lastChangeTimeEvent: MutableStateFlow<GameEvent.ServerGameEvent>
        get() = _networkCore.lastChangeTimeEvent
    override val players: MutableStateFlow<MutableList<Player>>
        get() = _networkCore.players
    override val player: MutableStateFlow<Player>
        get() = _networkCore.player
    override val time:MutableStateFlow<String>
        get() = _networkCore.time
    override fun sendMessage(message: String){
        _networkCore.sendMessage(message)
    }

    override fun confirmVote(victim: Player) {
        _networkCore.confirmVote(victim)
    }
}