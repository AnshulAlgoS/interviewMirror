package com.interviewmirror.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.interviewmirror.app.data.model.InterviewSession
import com.interviewmirror.app.ui.theme.*

@Composable
fun SummaryScreen(
    session: InterviewSession?,
    onSaveProgress: (InterviewSession) -> Unit,
    onFinish: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GradientStart, GradientEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Trophy Icon
            Text(
                text = "🏆",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = MaterialTheme.typography.titleLarge.fontSize * 4
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "Session Summary",
                style = MaterialTheme.typography.titleLarge,
                color = AccentPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (session != null) {
                // Stats Cards
                StatsCard(
                    title = "Questions Answered",
                    value = session.questionsAnswered.toString(),
                    icon = "📝"
                )

                Spacer(modifier = Modifier.height(16.dp))

                StatsCard(
                    title = "Average Confidence",
                    value = "${(session.averageConfidence * 100).toInt()}%",
                    icon = "💪",
                    color = when {
                        session.averageConfidence >= 0.7f -> SuccessGreen
                        session.averageConfidence >= 0.5f -> WarningYellow
                        else -> ErrorRed
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                StatsCard(
                    title = "Total Filler Words",
                    value = session.totalFillerWords.toString(),
                    icon = "💬"
                )

                Spacer(modifier = Modifier.height(16.dp))

                StatsCard(
                    title = "Average Speech Rate",
                    value = "${session.averageSpeechRate} words/min",
                    icon = "🗣️"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Improvement Areas
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = AccentSecondary.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "💡 Improvement Areas",
                            style = MaterialTheme.typography.titleMedium,
                            color = AccentSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        session.improvementAreas.forEach { area ->
                            Text(
                                text = "• $area",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Motivational Quote
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = AccentPrimary.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = getMotivationalQuote(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = AccentPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Save Progress Button
                Button(
                    onClick = { onSaveProgress(session) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentPrimary,
                        contentColor = DarkBackground
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save Progress to Firebase",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Return Home Button
            OutlinedButton(
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AccentSecondary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (session != null) "Return Home" else "Done")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun StatsCard(
    title: String,
    value: String,
    icon: String,
    color: androidx.compose.ui.graphics.Color = AccentPrimary
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = GradientEnd.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    color = color
                )
            }
            Text(
                text = icon,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = MaterialTheme.typography.titleLarge.fontSize * 2
                )
            )
        }
    }
}

fun getMotivationalQuote(): String {
    val quotes = listOf(
        "Great job improving today! Keep up the excellent work! 🌟",
        "Every practice session brings you closer to your goals! 💪",
        "You're making amazing progress. Stay consistent! ✨",
        "Confidence comes with practice. You're on the right track! 🚀",
        "Keep pushing yourself. You're doing better than you think! 🎯"
    )
    return quotes.random()
}
