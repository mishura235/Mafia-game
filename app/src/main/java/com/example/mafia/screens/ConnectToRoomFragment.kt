package com.example.mafia.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.mafia.R
import com.example.mafia.databinding.FragmentConnectToRoomBinding
import com.example.mafia.viewmodels.ConnectToRoomViewModel

class ConnectToRoomFragment : Fragment(R.layout.fragment_connect_to_room) {

    private val viewModel: ConnectToRoomViewModel by viewModels{ConnectToRoomViewModel.ConnectToRoomViewModelFactory(navController)}
    val navController: NavController by lazy {findNavController()}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentConnectToRoomBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_connect_to_room, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

}