package com.example.mafia.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mafia.R


class MainFragment : Fragment(R.layout.fragment_main) {
    private lateinit var connectToRoomButton: Button
    private lateinit var newRoomButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);
        connectToRoomButton = view.findViewById(R.id.connect)
        newRoomButton = view.findViewById(R.id.new_room)
        connectToRoomButton.setOnClickListener{
            findNavController().navigate(R.id.action_mainFragment_to_connectToRoomFragment)
        }
        newRoomButton.setOnClickListener{
            findNavController().navigate(R.id.action_mainFragment_to_newRoomFragment)
        }

    }
}