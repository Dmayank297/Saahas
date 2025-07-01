package com.example.saahas.ui.Screens.HelplineNumber


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.saahas.Models.HelplineNumber



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelplineNumberScreen() {
    val viewModel: HelplineViewModel = viewModel()
    val helplines by viewModel.helplines
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("National Helplines", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back navigation */ }) {
                        Icon(
                            imageVector =  androidx.compose.material.icons.Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle notification action */ }) {
                        Icon(
                            imageVector =  androidx.compose.material.icons.Icons.Filled.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
                NavigationBarItem(
                    icon = { Icon(imageVector = androidx.compose.material.icons.Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = false,
                    onClick = { /* Handle Home click */ }
                )
                NavigationBarItem(
                    icon = { Icon(imageVector = androidx.compose.material.icons.Icons.Filled.Call,
                        contentDescription = "Record") },
                    label = { Text("Record") },
                    selected = false,
                    onClick = { /* Handle Record click */ }
                )
                NavigationBarItem(
                    icon = { Icon(imageVector =  androidx.compose.material.icons.Icons.Filled.PlayArrow,
                        contentDescription = "Video") },
                    label = { Text("Video") },
                    selected = false,
                    onClick = { /* Handle Video click */ }
                )
                NavigationBarItem(
                    icon = { Icon(imageVector = androidx.compose.material.icons.Icons.Filled.AccountCircle,
                        contentDescription = "Helplines", tint = Color(0xFFFF5722)) },
                    label = { Text("Helplines") },
                    selected = true,
                    onClick = { /* Handle Helplines click */ }
                )
                NavigationBarItem(
                    icon = { Icon(imageVector =  androidx.compose.material.icons.Icons.Filled.Person,
                        contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = false,
                    onClick = { /* Handle Profile click */ }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(helplines.size) { index ->
                HelplineCard(helpline = helplines[index], onCallClick = { number ->
                    // Handle call action (e.g., open dialer with the number)
                    viewModel.onCall(context,number)
                })
            }
        }
    }
}

@Composable
fun HelplineCard(helpline: HelplineNumber, onCallClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .height(80.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF16D39) // Light orange background like in the image
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = helpline.iconResId),
                    contentDescription = "${helpline.name} Icon",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp)
                )
                Column {
                    Text(
                        text = helpline.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    )
                    Text(
                        text = helpline.number,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    )
                }
            }
            IconButton(
                onClick = { onCallClick(helpline.number) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector =  androidx.compose.material.icons.Icons.Filled.Call,
                    contentDescription = "Call ${helpline.name}",
                    tint = Color.Gray
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    HelplineNumberScreen()
}