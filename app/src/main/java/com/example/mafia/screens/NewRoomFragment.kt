package com.example.mafia.screens


import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.mafia.R
import com.example.mafia.databinding.FragmentNewRoomBinding
import com.example.mafia.viewmodels.NewRoomViewModel


class NewRoomFragment : Fragment(R.layout.fragment_new_room) {

    val viewModel: NewRoomViewModel by viewModels {NewRoomViewModel.NewRoomViewModelFactory(navController)}
    val navController: NavController by lazy {findNavController()}
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentNewRoomBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_new_room, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val wifiManager =
            context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        viewModel.roomIP.value =
            Formatter.formatIpAddress(wifiManager.connectionInfo.getIpAddress());


    }
}