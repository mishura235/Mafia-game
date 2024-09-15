package com.example.mafia.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.mafia.Game
import com.example.mafia.R
import com.example.mafia.network.GameServer
import com.example.mafia.screens.NewRoomFragment


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


    val clickStart: View.OnClickListener
        get() = View.OnClickListener { startGame() }
    private val _playersCount = MutableLiveData<Int>(1)
    private val _isRoomCreated = MutableLiveData<Boolean>(false)
    private val server = GameServer(Game())
    private fun createRoom() {
        _isRoomCreated.value = true
        server.startRoomServer()

    }

    private fun startGame() {
        navController.navigate(R.id.action_newRoomFragment_to_gameFragment)
    }

}