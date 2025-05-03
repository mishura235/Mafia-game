package com.example.mafia.game

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class WinDialog(val message:String): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setNeutralButton("OK",{_,_->dismiss()})
            .create()
        return dialog
    }

}