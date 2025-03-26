package com.example.mafia.game

import android.util.Log
import com.example.mafia.network.ChatMessage
import com.example.mafia.network.GameMessage
import com.example.mafia.network.Player
import io.ktor.util.reflect.instanceOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.util.Collections
import kotlin.streams.toList


class Game(
    val chat:MutableStateFlow<MutableList<GameMessage>>,
    val players:MutableStateFlow<MutableList<Player>>,
) {
    private val godPlayer = Player("GOD")
    private val mafiaCount=1
    private val prosecution by lazy {players.value.map { Vote(it.id,0) }.toMutableList()}
    val voted = mutableListOf<Player>()
    val gameStream = MutableStateFlow<GameEvent>(GameEvent.TimerSyncGameEvent(0))
    val timer = GameTimer()
    init {
        CoroutineScope(Dispatchers.IO).launch{
            gameStream.collect{
                if (it is GameEvent.ClientGameEvent){
                    when (it) {
                        is GameEvent.KillClientGameEvent -> {kill(it)}
                        is GameEvent.ProsecutionClientGameEvent -> {prosecution(it)}
                    }
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            timer.gameTime.collect { time->
                if (time%5==0){
                    gameStream.update { GameEvent.TimerSyncGameEvent(time) }
                }
                if (time%60==0 && time%120!=0){
                    gameStream.update { GameEvent.NightGameEvent() }
                    chat.update { (it+GameMessage("Город засыпает просыпается мафия",godPlayer,false)).toMutableList() }
                }
                if (time%120==0){
                    gameStream.update { GameEvent.DayGameEvent() }
                    chat.update { (it+GameMessage("Город просыпается",godPlayer,false)).toMutableList() }
                }

            }
        }
    }
    fun start(){

        val roles = MutableList<Player.Roles>(players.value.size-mafiaCount) { Player.Roles.CIVILIAN } + MutableList<Player.Roles>(mafiaCount,{Player.Roles.MAFIA})
        val tempPlayers = players.value
        val res = mutableListOf<Player>()
        roles.shuffled().forEachIndexed() { index, role ->
            res.add(tempPlayers[index].copy(role=role))
        }
        players.update { res }
        timer.start()

    }

    private fun prosecution(event: GameEvent.ProsecutionClientGameEvent) {
        if(event.accusing !in voted) {
            Log.d("prosecution",prosecution.toString())
            prosecution.find { it.playerId == event.accused.id }!!.accusingCount += 1
            voted += event.accusing
        }
        if (voted.size==players.value.size-1){
            execution()
        }
    }
    private fun execution(){
        val res = mutableListOf<Player>()
        players.value.forEach {
            if (it.id==prosecution.maxBy { it.accusingCount }.playerId) {
                res.add(it.copy(isAlive = false))
            } else{
                res.add(it)
            }
        }
        players.update { res }
        prosecution.forEach { it.accusingCount=0 }
        checkWinner()
    }

    private fun checkWinner() {
        if(players.value.filter{ it.role==Player.Roles.CIVILIAN&&it.isAlive }.size==1){
            gameStream.update { GameEvent.MafiaWinner() }
            chat.update { (it+GameMessage("Мафия выиграла",godPlayer,false)).toMutableList() }
        } else if (players.value.none { it.role == Player.Roles.MAFIA && it.isAlive }){
            gameStream.update { GameEvent.CivilianWinner() }
            chat.update { (it+GameMessage("Мафия проиграла",godPlayer,false)).toMutableList() }
        }
    }

    private fun kill(event: GameEvent.KillClientGameEvent) {
        if (event.killer.role == Player.Roles.MAFIA) {
            val res = mutableListOf<Player>()
            players.value.forEach {
                if (it.id==event.victim.id) res.add(it.copy(isAlive = false))
                else res.add(it)
            }
            players.update { res }
            checkWinner()
        }
    }
    data class Vote(
        val playerId: Int,
        var accusingCount:Int
    )
}


@Serializable
sealed class GameEvent(){

    @Serializable
    sealed class ServerGameEvent():GameEvent()
    @Serializable
    class TimerSyncGameEvent(val time:Int):ServerGameEvent()
    @Serializable
    class NightGameEvent():ServerGameEvent()
    @Serializable
    class DayGameEvent():ServerGameEvent()
    @Serializable
    class StartGameEvent():ServerGameEvent()

    @Serializable
    sealed class ClientGameEvent():GameEvent()
    @Serializable
    data class KillClientGameEvent(val killer:Player,val victim:Player):ClientGameEvent()
    @Serializable
    data class ProsecutionClientGameEvent(val accusing:Player,val accused:Player):ClientGameEvent()

    @Serializable
    class MafiaWinner():ServerGameEvent()

    @Serializable
    class CivilianWinner():ServerGameEvent()

}

class GameTimer(time:Int=0){
    val gameTime=MutableStateFlow<Int>(time)
    val job = CoroutineScope(Dispatchers.IO).launch(start = CoroutineStart.LAZY){
        while (true){
            delay(1000)
            gameTime.value+=1
        }
    }
    fun start(){
        if(!job.isActive){
            job.start()
        }
    }
}