package com.example.mafia.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import com.example.mafia.R
import com.example.mafia.network.GameClient
import com.example.mafia.network.GameNetworking
import com.example.mafia.network.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConnectToRoomViewModel(val navController: NavController) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    class ConnectToRoomViewModelFactory(val navController: NavController):ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ConnectToRoomViewModel(navController) as T
        }
    }
    private val _username: MutableLiveData<String> = MutableLiveData<String>()
    val username: MutableLiveData<String>
        get() = _username

    private val _roomIP: MutableLiveData<String> = MutableLiveData<String>()
    val roomIP: MutableLiveData<String>
        get() = _roomIP
    val client = GameClient(viewModelScope)

    fun connectToRoom() {
        client.connect(roomIP.value.toString(),username.value.toString())
        GameNetworking.setNetworkCore(client)
        navController.navigate(R.id.action_connectToRoomFragment_to_gameFragment)

    }
}