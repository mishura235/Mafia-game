package com.example.mafia.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.mafia.R
import com.example.mafia.databinding.FragmentConnectToRoomBinding
import com.example.mafia.databinding.FragmentGameBinding
import com.example.mafia.game.ActionDialog
import com.example.mafia.network.GameMessage
import com.example.mafia.network.GameNetworking
import com.example.mafia.network.Player
import com.example.mafia.recyclerview.ChatAdapter
import com.example.mafia.recyclerview.PlayersAdapter
import com.example.mafia.viewmodels.GameViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameFragment : Fragment() {
    lateinit var sendMessage:Button
    lateinit var messageText:EditText
    companion object {
        fun newInstance() = GameFragment()
    }

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentGameBinding=
            DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sendMessage = view.findViewById(R.id.sendMessage)
        messageText = view.findViewById(R.id.messageText)
        val playersAdapter = PlayersAdapter(::createDialog)
        val chatAdapter = ChatAdapter()
        val rcViewPlayers:RecyclerView = view.findViewById(R.id.recyclerViewPlayers)
        val rcViewChat:RecyclerView = view.findViewById(R.id.recyclerViewChat)
        rcViewChat.adapter=chatAdapter
        rcViewPlayers.adapter = playersAdapter
        sendMessage.setOnClickListener{
            GameNetworking.sendMessage(messageText.text.toString())
        }
        CoroutineScope(Dispatchers.Main).launch{
            GameNetworking.chat.collect{
                chatAdapter.changeMessages(it)
            }
        }
        CoroutineScope(Dispatchers.Main).launch{
            GameNetworking.players.collect{
                playersAdapter.changePlayers(it)
            }
        }
    }
    private fun createDialog(player: Player){
        ActionDialog(player,viewModel::confirmVote).show(childFragmentManager,"ConfirmDialog")
    }

}