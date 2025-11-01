package com.interviewmirror.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.interviewmirror.app.data.model.InterviewDomain
import com.interviewmirror.app.ui.theme.*

@Composable
fun LandingScreen(
    onStartInterview: (InterviewDomain) -> Unit
) {
    var showDomainSelection by remember { mutableStateOf(false) }

    // Animated rotation for mic icon
    val infiniteTransition = rememberInfiniteTransition(label = "mic_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_rotation_angle"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GradientStart, GradientEnd)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (showDomainSelection) {
            DomainSelectionContent(
                onDomainSelected = { domain ->
                    onStartInterview(domain)
                },
                onBack = { showDomainSelection = false }
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                // Animated Mic Icon
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Microphone",
                    modifier = Modifier
                        .size(120.dp)
                        .rotate(rotation),
                    tint = AccentPrimary
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Title
                Text(
                    text = "AI Interview Mirror",
                    style = MaterialTheme.typography.titleLarge,
                    color = AccentPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tagline
                Text(
                    text = "Your pocket interviewer that never sleeps.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(64.dp))

                // Start Interview Button
                Button(
                    onClick = { showDomainSelection = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPrimary,
                        contentColor = DarkBackground
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Start Interview",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // SDK Info
                Text(
                    text = "✨ Powered by RunAnywhere SDK\nOn-device AI • Privacy First • Lightning Fast",
                    style = MaterialTheme.typography.labelSmall,
                    color = AccentSecondary.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 32.dp)
                )
            }
        }
    }
}

@Composable
fun DomainSelectionContent(
    onDomainSelected: (InterviewDomain) -> Unit,
    onBack: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Text(
            text = "Choose Your Domain",
            style = MaterialTheme.typography.titleLarge,
            color = AccentPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        val domains = listOf(
            InterviewDomain.TECH to "💻 Tech",
            InterviewDomain.HR to "👥 HR",
            InterviewDomain.PRODUCT to "📦 Product",
            InterviewDomain.DESIGN to "🎨 Design"
        )

        domains.forEach { (domain, label) ->
            DomainButton(
                label = label,
                onClick = { onDomainSelected(domain) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        TextButton(onClick = onBack) {
            Text(
                text = "← Back",
                color = AccentSecondary
            )
        }
    }
}

@Composable
fun DomainButton(
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = GradientEnd,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
