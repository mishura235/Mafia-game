package com.example.mafia.viewmodels

import android.util.Log
import android.view.View
import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map


class NewRoomViewModel : ViewModel() {
    val playersCount:MutableLiveData<Int>
        get() = _playersCount
    val isRoomCreated: LiveData<Boolean>
        get()= _isRoomCreated

    val clickCreate: View.OnClickListener = View.OnClickListener { createRoom() }

    val clickStart: View.OnClickListener = View.OnClickListener {startGame() }


    private val _playersCount = MutableLiveData<Int>(1)
    private val _isRoomCreated = MutableLiveData<Boolean>(false)

    private fun createRoom(){
        Log.d("ssssss",_isRoomCreated.value.toString())
        _isRoomCreated.value = !_isRoomCreated.value!!
        Log.d("ssssss",_isRoomCreated.value.toString())
        _playersCount.value = _playersCount.value!! + 1
    }
    private fun startGame(){

    }

}