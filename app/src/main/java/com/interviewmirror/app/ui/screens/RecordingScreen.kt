package com.interviewmirror.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.interviewmirror.app.data.model.InterviewQuestion
import com.interviewmirror.app.ui.theme.*
import kotlin.math.sin

@Composable
fun RecordingScreen(
    question: InterviewQuestion,
    questionNumber: Int,
    totalQuestions: Int,
    audioLevel: Float = 0.5f,
    partialTranscript: String = "",
    onStopRecording: () -> Unit
) {
    // Animated waveform
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    // Dynamic amplitude based on audio level
    val targetAmplitude = 30f + (audioLevel * 60f)
    val amplitude by animateFloatAsState(
        targetValue = targetAmplitude,
        animationSpec = tween(200, easing = FastOutSlowInEasing),
        label = "amplitude"
    )

    // Pulsing effect for recording indicator
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    // Mic icon pulse
    val micScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkMidnight, RichNavy, DeepOcean)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Progress indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question $questionNumber/$totalQuestions",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MediumAqua,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.2.sp
                    )
                )

                // Recording time indicator
                Text(
                    text = "● REC",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = ErrorRed,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.alpha(pulseAlpha)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { questionNumber.toFloat() / totalQuestions },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MediumAqua,
                trackColor = RichNavy,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Animated microphone with pulse effect
            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer pulse circles
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(micScale)
                        .background(
                            color = MediumAqua.copy(alpha = pulseAlpha * 0.2f),
                            shape = CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(
                            color = MediumAqua.copy(alpha = pulseAlpha * 0.4f),
                            shape = CircleShape
                        )
                )
                // Center mic icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(DeepOcean, MediumAqua)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Recording",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Listening...",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = AiryBlue,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Speak clearly and naturally",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MediumAqua.copy(alpha = 0.7f)
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Real-time waveform visualizer
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                colors = CardDefaults.cardColors(
                    containerColor = RichNavy.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        val width = size.width
                        val height = size.height
                        val centerY = height / 2

                        // Main waveform
                        for (i in 0 until 60) {
                            val x = (width / 60) * i
                            val offset = (phase + i * 8) * Math.PI / 180
                            val y = centerY + sin(offset) * amplitude

                            val alpha = 0.9f - (i / 60f) * 0.3f
                            drawCircle(
                                color = MediumAqua.copy(alpha = alpha),
                                radius = 4f + (audioLevel * 3f),
                                center = Offset(x, y.toFloat())
                            )
                        }

                        // Secondary wave for depth
                        for (i in 0 until 60) {
                            val x = (width / 60) * i
                            val offset = (phase + i * 8 + 180) * Math.PI / 180
                            val y = centerY + sin(offset) * (amplitude * 0.6f)

                            val alpha = 0.6f - (i / 60f) * 0.2f
                            drawCircle(
                                color = AiryBlue.copy(alpha = alpha),
                                radius = 3f + (audioLevel * 2f),
                                center = Offset(x, y.toFloat())
                            )
                        }
                    }

                    // Audio level indicator
                    Text(
                        text = "🎤 ${(audioLevel * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AiryBlue.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Live transcript display
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = DarkMidnight.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = MediumAqua,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Your Response",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MediumAqua,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Divider(
                        color = MediumAqua.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (partialTranscript.isEmpty()) {
                            Text(
                                text = "Start speaking...",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = AiryBlue.copy(alpha = 0.4f),
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                ),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            Text(
                                text = partialTranscript,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = AiryBlue,
                                    lineHeight = 28.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stop button with modern design
            Button(
                onClick = onStopRecording,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepOcean,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Stop & Analyze",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
