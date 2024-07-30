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

    val clickCreate: View.OnClickListener
        get() = View.OnClickListener { createRoom() }

    val clickStart: View.OnClickListener
        get() = View.OnClickListener {startGame() }

    private val _playersCount = MutableLiveData<Int>(1)
    private val _isRoomCreated = MutableLiveData<Boolean>(false)

    private fun createRoom(){


    }
    private fun startGame(){

    }

}