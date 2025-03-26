package com.example.mafia.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.mafia.R
import com.example.mafia.network.GameNetworking
import com.example.mafia.network.GameServer
import com.example.mafia.network.Player
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class NewRoomViewModel(private val navController: NavController) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class NewRoomViewModelFactory(private val navController: NavController):ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NewRoomViewModel(navController) as T
        }
    }


    val playersCount: LiveData<Int>
        get() = _playersCount
    val isRoomCreated: LiveData<Boolean>
        get() = _isRoomCreated
    val roomIP: MutableLiveData<String>
        get() = _roomIP


    private val _roomIP = MutableLiveData<String>()
    val clickCreate: View.OnClickListener
        get() = View.OnClickListener { createRoom() }
    val nickname:MutableLiveData<String>
        get() = _nickname

    val clickStart: View.OnClickListener
        get() = View.OnClickListener { startGame() }
    private val _playersCount = MutableLiveData<Int>(1)
    private val _isRoomCreated = MutableLiveData<Boolean>(false)
    private val _nickname: MutableLiveData<String> = MutableLiveData<String>("")
    private val server = GameServer(viewModelScope)
    private fun createRoom() {
        viewModelScope.launch {
            server.players.collect {
                _playersCount.value = it.size
            }
        }
        val player=Player.generatePlayerWithId(_nickname.value.toString())
        server.player.update {player}
        server.players.value += player
        _isRoomCreated.value = true
        server.startServer()
    }

    private fun startGame() {
        server.startGame()
        GameNetworking.setNetworkCore(server)
        navController.navigate(R.id.action_newRoomFragment_to_gameFragment)
    }

}