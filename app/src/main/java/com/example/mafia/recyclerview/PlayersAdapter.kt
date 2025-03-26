package com.example.mafia.recyclerview

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.transition.Visibility
import com.example.mafia.R
import com.example.mafia.game.ActionDialog
import com.example.mafia.game.GameEvent
import com.example.mafia.network.GameNetworking
import com.example.mafia.network.Player
import io.ktor.http.CacheControl

class PlayersAdapter(val createDialog: (Player)->Unit): RecyclerView.Adapter<PlayersAdapter.PlayersViewHolder>() {
    class PlayersViewHolder(itemView: View): ViewHolder(itemView) {
        val playerIcon :ImageView= itemView.findViewById(R.id.player_icon)
        val playerNick:TextView = itemView.findViewById(R.id.player_nick)
    }
    var players = mutableListOf<Player>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayersViewHolder {
        val itemView =
        LayoutInflater.from(parent.context)
            .inflate(R.layout.player_icon, parent, false)
    return PlayersViewHolder(itemView)
    }

    override fun getItemCount(): Int = players.size


    override fun onBindViewHolder(holder: PlayersViewHolder, position: Int) {
        holder.playerNick.text = players[position].nick
        holder.playerIcon.setImageResource(if (players[position].isAlive) R.drawable.account_cowboy_hat else R.drawable.ic_launcher_foreground)
        holder.playerIcon.setOnClickListener {
            Log.d( "onBindViewHolder: ",GameNetworking.player.value.toString()+GameNetworking.lastChangeTimeEvent.value.toString()+players[position])
            if (GameNetworking.lastChangeTimeEvent.value is GameEvent.DayGameEvent &&
                GameNetworking.player.value.role == Player.Roles.CIVILIAN){
                createDialog(players[position])
            }else if (GameNetworking.lastChangeTimeEvent.value is GameEvent.NightGameEvent &&
                GameNetworking.player.value.role == Player.Roles.MAFIA){
                createDialog(players[position])
            }
        }
    }

    fun changePlayers(players: MutableList<Player>){
        this.players=players
        notifyDataSetChanged()
    }
}