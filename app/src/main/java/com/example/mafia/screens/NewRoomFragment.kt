package com.example.mafia.screens

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.mafia.viewmodels.NewRoomViewModel
import com.example.mafia.R
import com.example.mafia.databinding.FragmentConnectToRoomBinding
import com.example.mafia.databinding.FragmentNewRoomBinding

class NewRoomFragment : Fragment(R.layout.fragment_new_room) {

    private val viewModel: NewRoomViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentNewRoomBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_new_room,container,false)
        binding.viewModel = viewModel
        binding.lifecycleOwner=this
        return binding.root
    }
}