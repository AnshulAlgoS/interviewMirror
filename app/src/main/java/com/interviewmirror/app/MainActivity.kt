package com.interviewmirror.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.interviewmirror.app.ui.navigation.InterviewNavigation
import com.interviewmirror.app.ui.theme.AIInterviewMirrorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var hasAudioPermission by mutableStateOf(false)
    private var showPermissionDialog by mutableStateOf(false)

    private val audioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (!isGranted) {
            showPermissionDialog = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if permission is already granted
        hasAudioPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        // Request permission if not granted
        if (!hasAudioPermission) {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        setContent {
            AIInterviewMirrorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (showPermissionDialog) {
                        PermissionDeniedDialog(
                            onDismiss = { showPermissionDialog = false },
                            onRetry = {
                                showPermissionDialog = false
                                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        )
                    }

                    InterviewNavigation()
                }
            }
        }
    }
}

@Composable
fun PermissionDeniedDialog(
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Microphone Permission Required") },
        text = {
            Text(
                "Interview Mirror needs microphone access to record and analyze your interview responses. " +
                        "Please grant the permission to continue."
            )
        },
        confirmButton = {
            TextButton(onClick = onRetry) {
                Text("Grant Permission")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
