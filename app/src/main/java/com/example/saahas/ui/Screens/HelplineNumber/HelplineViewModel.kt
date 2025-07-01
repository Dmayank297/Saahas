package com.example.saahas.ui.Screens.HelplineNumber

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.saahas.Models.HelplineNumber

class HelplineViewModel : ViewModel() {
    private var _helplines by mutableStateOf(
        listOf(
            HelplineNumber("Police", "112", android.R.drawable.ic_menu_call),
            HelplineNumber("Ambulance", "108", android.R.drawable.ic_menu_call),
            HelplineNumber("Fire", "101", android.R.drawable.ic_menu_call),
            HelplineNumber("Pregnancy", "102", android.R.drawable.ic_menu_call),
            HelplineNumber("Women Helpline", "1091", android.R.drawable.ic_menu_call)
        )
    )
    val helplines: State<List<HelplineNumber>> = mutableStateOf(_helplines)

    fun onCall(androidContext: Context, number: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$number")
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        try {
            // Launch the dialer intent
            intent.resolveActivity(androidContext.packageManager)?.let {
                androidContext.startActivity(intent)
            }
        } catch (e: Exception) {
            // Handle error (e.g., no dialer app available)
            e.printStackTrace()
        }
    }
}