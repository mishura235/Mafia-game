package com.example.mafia.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mafia.network.GameClient
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConnectToRoomViewModel : ViewModel() {
    private val _username: MutableLiveData<String> = MutableLiveData<String>()
    val username:MutableLiveData<String>
        get() = _username

    private val _roomIP: MutableLiveData<String> = MutableLiveData<String>()
    val roomIP:MutableLiveData<String>
        get() = _roomIP


    fun connectToRoom() {
        val data = MutableLiveData<HttpResponse>()
        viewModelScope.launch {
            val temp = GameClient.connectToRoom(roomIP.value.toString())
            withContext(Dispatchers.Main){
                data.value=temp
            }
        }.invokeOnCompletion {
            Log.d("aaaa",data.value.toString())
        }
    }
}