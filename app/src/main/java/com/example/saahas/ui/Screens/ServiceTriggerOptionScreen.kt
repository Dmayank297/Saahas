package com.example.saahas.ui.Screens

import android.app.Application
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.saahas.Models.Room.VoiceRecordingDatabase
import com.example.saahas.PermissionManager
import com.example.saahas.R
import com.example.saahas.ui.Screens.Sensor.SensorViewModel
import com.example.saahas.ui.Screens.Sensor.SensorViewModelFactory
import com.example.saahas.ui.Screens.SpeechRecognation.VoiceControlViewModel
import com.example.saahas.ui.Screens.SpeechRecognation.VoiceControlViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceTriggerOptionScreen(
    permission: PermissionManager
) {
    val context = LocalContext.current
    val application = context.applicationContext as android.app.Application


    val viewModelFactory = remember {
        SensorViewModelFactory(context.applicationContext as Application, context, permission, VoiceRecordingDatabase.getDatabase(context).contactDao())
    }
    val sensorViewModel: SensorViewModel = viewModel(factory = viewModelFactory)

    val voiceControlViewModel: VoiceControlViewModel = viewModel(
        factory = VoiceControlViewModelFactory(context)
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Services Trigger Option",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            ScreenUiStructure(
                sensorViewModel = sensorViewModel,
                voiceControlViewModel = voiceControlViewModel,
            )
        }
    }
}

@Composable
fun ScreenUiStructure(
    modifier: Modifier = Modifier,
    sensorViewModel: SensorViewModel,
    voiceControlViewModel: VoiceControlViewModel
) {
    val context = LocalContext.current
    var showAccessibilityDialog by remember { mutableStateOf(false) }

    // Show the accessibility dialog when triggered
    if (showAccessibilityDialog) {
        AlertDialog(
            onDismissRequest = { showAccessibilityDialog = false },
            title = { Text("Enable Accessibility Service") },
            text = { Text("To enable volume button actions, please turn on accessibility service in settings.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showAccessibilityDialog = false
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        context.startActivity(intent)
                    }
                ) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAccessibilityDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OptionCard(
            title = "Volume Button Service",
            description = "Activate emergency assistance by using your phone’s volume buttons." +
                    " The app detects specific button presses and triggers safety measures.\n" +
                    "\n" +
                    "1. Press Volume-Up two times: Send an SOS alert to emergency contacts with your current location.\n" +
                    "2. Hold Volume-Down for 3-5 sec: Automatically call the first saved emergency contact.\n" +
                    "These features work even the phone is idle.",
            onToggle = { isEnabled ->
                if (isEnabled) {
                    if (!checkAccessibilityEnabled(context)) {
                        showAccessibilityDialog = true
                    } else {
                        // Accessibility is enabled, proceed with starting the service
                        Toast.makeText(context, "Volume Button Service is enabled", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Volume Button Service is disabled", Toast.LENGTH_SHORT).show()
                }
            },
            isAccessibilityEnabled = checkAccessibilityEnabled(context)
        )

        OptionCard(
            title = "Smart Voice",
            description = ("Activate emergency assistance by speaking a preset voice command." +
                    " The app listens in the background and responds when triggered.\n" +
                    "\n" +
                    "1. Hands-Free SOS Alert: Trigger an SOS alert with " + "Send SOS" + " voice command.\n" +
                    "2. Live-Location Sharing: Start sending your live location to contacts with " + "Start Live Location" + " voice command.\n" +
                    "3. Call Emergency Services: Instantly call the nearby-police station using Alert police voice commands.\n" +
                    "4. Live Camera Recording: Start recording video and share it with emergency contacts with Start Recording voice command."),
            onToggle = { isEnabled ->
                if (isEnabled) {
                    voiceControlViewModel.startVoiceService()
                } else {
                    voiceControlViewModel.stopVoiceService()
                }
            }
        )

        OptionCard(
            title = "Shake Trigger",
            description = "Activate emergency assistance by shaking your phone." +
                    " The app detects sudden, forceful shakes and triggers safety actions.\n" +
                    "\n" +
                    "1. Mark Unsafe Location: Flag the current location as unsafe with shaking the phone 3 times or more.\n" +
                    "2. Live Cam Recording: Start recording audio/video just by shaking the phone once.",
            modifier = Modifier.fillMaxWidth(),
            onToggle = { isEnabled ->
                if (isEnabled) {
                    sensorViewModel.startSensors()
                } else {
                    sensorViewModel.stopSensors()
                }
            }
        )
    }
}

@Composable
fun OptionCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    onToggle: (Boolean) -> Unit = {},
    isAccessibilityEnabled: Boolean = true // Add parameter to control toggle state based on accessibility
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isToggled by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Update toggle state based on accessibility status
    LaunchedEffect(isAccessibilityEnabled) {
        if (title == "Volume Button Service" && !isAccessibilityEnabled) {
            isToggled = false
        }
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF16D39)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (title == "Volume Button Service" && !isAccessibilityEnabled && !isToggled) {
                                // Prevent toggling on if accessibility is not enabled
                                onToggle(true)
                            } else {
                                isToggled = !isToggled
                            }
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = if (isToggled) R.drawable.toggle_on else R.drawable.toggle_off),
                            contentDescription = if (isToggled) "Turn Off" else "Turn On",
                            tint = Color.Unspecified,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    IconButton(onClick = { isExpanded = !isExpanded }) {
                        Icon(
                            painter = painterResource(
                                id = if (isExpanded) R.drawable.arrow_up
                                else R.drawable.arrow_down
                            ),
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = Color.Black
                        )
                    }
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Black,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

fun checkAccessibilityEnabled(context: Context): Boolean {
    val service = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    )
    return service?.contains(context.packageName) == true
}