package com.example.saahas.ui.Screens.reportcrime

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Post(modifier: Modifier = Modifier) {

    Column(modifier = modifier.fillMaxSize(),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        // Your UI components for the Volunteer screen go here
        // For example, you can add a Text or Button to indicate this is the Volunteer screen
        // Text(text = "Volunteer Screen", modifier = modifier)
        Text("Post Screen", modifier = modifier.size(54.dp))
    }
}