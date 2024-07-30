package com.example.mafia.screens

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import com.example.mafia.viewmodels.ConnectToRoomViewModel
import com.example.mafia.R
import com.example.mafia.databinding.FragmentConnectToRoomBinding

class ConnectToRoomFragment : Fragment(R.layout.fragment_connect_to_room) {

    private val viewModel: ConnectToRoomViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding:FragmentConnectToRoomBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_connect_to_room,container,false)
        binding.viewModel = viewModel
        binding.lifecycleOwner=this
        return binding.root
    }

}