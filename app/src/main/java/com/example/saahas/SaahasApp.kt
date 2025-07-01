package com.example.saahas

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import com.example.saahas.ui.Authentication.SignIn
import com.example.saahas.ui.Authentication.SignUp
import com.example.saahas.ui.Screens.AddFriends
import com.example.saahas.ui.Screens.Button.AudioRecord.VoiceRecordingScreen
import com.example.saahas.ui.Screens.HelplineNumber.HelplineNumberScreen
import com.example.saahas.ui.Screens.Location.LocationMapScreen
import com.example.saahas.ui.Screens.SOSScreen
import com.example.saahas.ui.Screens.Sensor.SensorScreen
import com.example.saahas.ui.Screens.ServiceTriggerOptionScreen
import com.example.saahas.ui.Screens.SplashScreen
import com.example.saahas.ui.Screens.reportcrime.CommunityReportScreen
import com.example.saahas.ui.Screens.reportcrime.Post
import com.example.saahas.ui.Screens.reportcrime.ReportCrime
import com.example.saahas.ui.Screens.reportcrime.Volunteer
import com.example.simple.OnboardingScreen

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun SaahasApp(permission: PermissionManager) {
    AppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val appState = rememberAppState()


                NavHost(
                    navController = appState.navController,
                    startDestination = SPLASH_SCREEN, // Start with SplashScreen like MathApp
                ) {
                    saahasAppGraph(appState, permission)
                }

        }
    }
}

@Composable
fun rememberAppState(navController: NavHostController = rememberNavController()) =
    remember(navController) {
        NavigationState(navController)
    }


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
fun NavGraphBuilder.saahasAppGraph(appState: NavigationState, permission: PermissionManager) {


    composable(route = SPLASH_SCREEN) {
        SplashScreen(
            openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) }
        )
    }

    composable(route = SIGN_IN) {
        SignIn(
            openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) }
        )
    }

    composable(route = SIGN_UP) {
        SignUp(
            openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) }
        )
    }

    composable(route = ONBOARDING) {
        OnboardingScreen(
            onSkip = { appState.navigate(SIGN_IN) },
            onFinish = { appState.navigate(SIGN_IN) }
        )
    }

    composable(route = SOS_SCREEN) {
        SOSScreen(
            permission = permission,
            openScreen = { route -> appState.navigate(route) },
            openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) }
        )
    }

    composable(route = ADD_FRIENDS) {
        AddFriends(
            permissionManager = permission,
            openScreen = { route -> appState.navigate(route) },
            popUpScreen = { appState.popUp() }
        )
    }

    composable(route = LOCATION_MAP_SCREEN) {
        LocationMapScreen(
            permission = permission
        )
    }

    composable(route = VOICE_RECORDING_SCREEN) {
        VoiceRecordingScreen(
            permission = permission
        )
    }

    composable(route = SENSOR_SCREEN) {
        SensorScreen(
            permissionManager = permission
        )
    }

    composable(route = SERVICE_TRIGGER_OPTION_SCREEN) {
        ServiceTriggerOptionScreen(permission)
    }

    composable(route = HELPLINE_NUMBER_SCREEN) {
        HelplineNumberScreen()
    }

    composable(route = REPORT_CRIME) {
        ReportCrime()
    }

    composable(route = COMMUNITY_REPORT_SCREEN) {
        CommunityReportScreen(
            openScreen = { route -> appState.navigate(route) }
        )
    }

    composable(route = VOLUNTEER) {
        Volunteer()
    }

    composable(route = VOLUNTEER) {
        Post()
    }
}

