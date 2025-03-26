package com.example.mafia.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mafia.network.GameNetworking
import com.example.mafia.network.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    fun confirmVote(victim: Player) {
        GameNetworking.confirmVote(victim)
    }

    val _timer = MutableLiveData("00:00")
    val timer :MutableLiveData<String>
        get() = _timer

    init {
        CoroutineScope(Dispatchers.Main).launch {
            GameNetworking.time.collect{
                timer.value=it
            }
        }
    }


}