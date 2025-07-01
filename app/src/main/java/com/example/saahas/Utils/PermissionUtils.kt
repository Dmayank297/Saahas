package com.example.saahas.Utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.activity.ComponentActivity

object PermissionUtils {
    private var activity: ComponentActivity? = null
    private val permissionCallbacks = mutableMapOf<String, (Boolean) -> Unit>()
    private var pendingPermission: String? = null

    // Initialize with activity context (call this in MainActivity or equivalent)
    fun initialize(context: Context) {
        if (context is ComponentActivity) {
            activity = context
        } else {
            throw IllegalArgumentException("Context must be a ComponentActivity")
        }
    }

    // Check if a permission is granted
    fun isPermissionGranted(permission: String): Boolean {
        val context = activity ?: throw IllegalStateException("PermissionUtils not initialized")
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Request a single permission
    fun requestPermission(permission: String, callback: (Boolean) -> Unit = {}) {
        val activity = activity ?: throw IllegalStateException("PermissionUtils not initialized")
        if (isPermissionGranted(permission)) {
            callback(true)
            return
        }

        permissionCallbacks[permission] = callback
        pendingPermission = permission

        val launcher = activity.activityResultRegistry.register(
            "permission_$permission",
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            permissionCallbacks[permission]?.invoke(isGranted)
            permissionCallbacks.remove(permission)
            pendingPermission = null
        }

        launcher.launch(permission)
    }

    // Request multiple permissions
    fun requestPermissions(permissions: Array<String>, callback: (Map<String, Boolean>) -> Unit) {
        val activity = activity ?: throw IllegalStateException("PermissionUtils not initialized")
        val results = permissions.associateWith { isPermissionGranted(it) }
        if (results.all { it.value }) {
            callback(results)
            return
        }

        val launcher = activity.activityResultRegistry.register(
            "permissions_${permissions.joinToString()}",
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            callback(result)
        }

        launcher.launch(permissions)
    }

    // Composable to handle permission requests in UI
    @Composable
    fun PermissionRequestHandler(
        permission: String,
        onResult: (Boolean) -> Unit = {},
        content: @Composable () -> Unit
    ) {
        val context = LocalContext.current
        var permissionState by remember { mutableStateOf(isPermissionGranted(permission)) }

        DisposableEffect(Unit) {
            requestPermission(permission) { granted ->
                permissionState = granted
                onResult(granted)
            }
            onDispose { }
        }

        if (permissionState) {
            content()
        }
    }

    // Composable to observe permission state changes
    @Composable
    fun ObservePermissionState(
        permission: String,
        onPermissionChanged: (Boolean) -> Unit
    ) {
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner, permission) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    val isGranted = isPermissionGranted(permission)
                    onPermissionChanged(isGranted)
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }
}