package com.example.mafia.recyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.mafia.R
import com.example.mafia.network.GameMessage

class ChatAdapter:Adapter<ChatAdapter.ChatViewHolder>() {
    class ChatViewHolder(itemView: View): ViewHolder(itemView){
        val sender: TextView= itemView.findViewById(R.id.sender_name)
        val message: TextView= itemView.findViewById(R.id.message)
    }
    var messages = mutableListOf<GameMessage>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_message,parent,false)
        return ChatViewHolder(itemView)
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.sender.text = messages[position].getFormattedSender()
        holder.message.text = messages[position].getFormattedText()
    }

    fun addMessage(message: GameMessage){
        this.messages.add(message)
        notifyDataSetChanged()
    }
    fun changeMessages(messages: MutableList<GameMessage>){
        this.messages = messages
        notifyDataSetChanged()
    }

}