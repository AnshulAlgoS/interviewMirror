package com.interviewmirror.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.interviewmirror.app.data.model.AnalysisResult
import com.interviewmirror.app.data.model.InterviewQuestion
import com.interviewmirror.app.ui.theme.*

@Composable
fun FeedbackScreen(
    question: InterviewQuestion,
    analysisResult: AnalysisResult,
    hasMoreQuestions: Boolean,
    onNextQuestion: () -> Unit,
    onEndSession: () -> Unit
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
            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "🤖 AI Analysis Complete",
                style = MaterialTheme.typography.titleLarge,
                color = AccentPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // AI Interviewer Feedback
            if (analysisResult.aiFeedback != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = AccentPrimary.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "💬 Interviewer Feedback",
                            style = MaterialTheme.typography.titleMedium,
                            color = AccentPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = analysisResult.aiFeedback,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3f
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // AI Confidence Assessment
            if (analysisResult.aiConfidenceAssessment != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = GradientEnd.copy(alpha = 0.8f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "🎯 Confidence Assessment",
                            style = MaterialTheme.typography.titleMedium,
                            color = AccentSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = analysisResult.aiConfidenceAssessment,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Strengths Identified
            if (analysisResult.aiStrengths.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = SuccessGreen.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "✨ Strengths",
                            style = MaterialTheme.typography.titleMedium,
                            color = SuccessGreen
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        analysisResult.aiStrengths.forEach { strength ->
                            Text(
                                text = "• $strength",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Areas to Improve
            if (analysisResult.aiImprovements.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = WarningYellow.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "📈 Areas to Improve",
                            style = MaterialTheme.typography.titleMedium,
                            color = WarningYellow
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        analysisResult.aiImprovements.forEach { improvement ->
                            Text(
                                text = "• $improvement",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Emotion Emoji
            val emoji = when (analysisResult.tone) {
                AnalysisResult.Tone.CONFIDENT -> "😃"
                AnalysisResult.Tone.CALM -> "😊"
                AnalysisResult.Tone.NEUTRAL -> "😐"
                AnalysisResult.Tone.NERVOUS -> "😬"
                AnalysisResult.Tone.ANXIOUS -> "😰"
            }
            Text(
                text = emoji,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = MaterialTheme.typography.titleLarge.fontSize * 2),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confidence Meter
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = GradientEnd.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Confidence Meter",
                        style = MaterialTheme.typography.titleMedium,
                        color = AccentSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { analysisResult.confidenceScore },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp),
                        color = when {
                            analysisResult.confidenceScore >= 0.7f -> SuccessGreen
                            analysisResult.confidenceScore >= 0.5f -> WarningYellow
                            else -> ErrorRed
                        },
                        trackColor = GradientStart,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${(analysisResult.confidenceScore * 100).toInt()}% Confident",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Speech Rate
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
                            text = "Speech Rate",
                            style = MaterialTheme.typography.titleMedium,
                            color = AccentSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${analysisResult.speechRate} words/min",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "🗣️",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filler Words
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
                            text = "Filler Words",
                            style = MaterialTheme.typography.titleMedium,
                            color = AccentSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${analysisResult.fillerCount} detected",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (analysisResult.fillerWords.isNotEmpty()) {
                            Text(
                                text = analysisResult.fillerWords.joinToString(", ") { it.word },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                    Text(
                        text = "💬",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // AI Follow-up Question
            if (analysisResult.aiFollowUpQuestion != null && hasMoreQuestions) {
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
                            text = "❓ Follow-up Question",
                            style = MaterialTheme.typography.titleMedium,
                            color = AccentSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = analysisResult.aiFollowUpQuestion,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3f
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Action Buttons
            if (hasMoreQuestions) {
                Button(
                    onClick = onNextQuestion,
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
                        text = "Next Question",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onEndSession,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ErrorRed
                    )
                ) {
                    Text(text = "End Session")
                }
            } else {
                Button(
                    onClick = onEndSession,
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
                        text = "View Summary",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
