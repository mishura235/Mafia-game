package com.example.mafia.game

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.mafia.R
import com.example.mafia.network.GameNetworking
import com.example.mafia.network.Player

class ActionDialog(val victim: Player,val positiveCallback:(Player)->Unit):DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = if (GameNetworking.player.value.role==Player.Roles.MAFIA)
            getString(R.string.kill_confirmation) else getString(R.string.prosecution_confirmation)
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("Yes"){_,_->positiveCallback(victim)}
            .setNegativeButton("No"){_,_->dismiss()}
            .create()
        return dialog
    }
}