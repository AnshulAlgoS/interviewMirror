package com.interviewmirror.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.interviewmirror.app.ui.screens.AnalyzingScreen
import com.interviewmirror.app.ui.screens.FeedbackScreen
import com.interviewmirror.app.ui.screens.LandingScreen
import com.interviewmirror.app.ui.screens.QuestionScreen
import com.interviewmirror.app.ui.screens.RecordingScreen
import com.interviewmirror.app.ui.screens.SummaryScreen
import com.interviewmirror.app.ui.viewmodel.InterviewUiState
import com.interviewmirror.app.ui.viewmodel.InterviewViewModel

@Composable
fun InterviewNavigation(
    viewModel: InterviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val liveTranscript by viewModel.liveTranscript.collectAsState()
    val audioLevel by viewModel.audioLevel.collectAsState()

    when (val state = uiState) {
        is InterviewUiState.Initial -> {
            LandingScreen(
                onStartInterview = {
                    viewModel.selectDomain(it)
                }
            )
        }

        is InterviewUiState.Question -> {
            QuestionScreen(
                question = state.question,
                questionNumber = state.questionNumber,
                totalQuestions = state.totalQuestions,
                onRecordAnswer = { viewModel.startRecording() },
                onEndSession = { viewModel.endSession() }
            )
        }

        is InterviewUiState.Recording -> {
            RecordingScreen(
                question = state.question,
                questionNumber = state.questionNumber,
                totalQuestions = state.totalQuestions,
                audioLevel = audioLevel,
                partialTranscript = liveTranscript,
                onStopRecording = { viewModel.stopRecording() }
            )
        }

        is InterviewUiState.Analyzing -> {
            AnalyzingScreen()
        }

        is InterviewUiState.Feedback -> {
            FeedbackScreen(
                question = state.question,
                analysisResult = state.analysisResult,
                hasMoreQuestions = state.hasMoreQuestions,
                onNextQuestion = { viewModel.nextQuestion() },
                onEndSession = { viewModel.endSession() }
            )
        }

        is InterviewUiState.Summary -> {
            SummaryScreen(
                session = state.session,
                onSaveProgress = { viewModel.saveSessionToFirebase(it) },
                onFinish = { viewModel.resetToInitial() }
            )
        }

        is InterviewUiState.SaveSuccess -> {
            SummaryScreen(
                session = null,
                onSaveProgress = {},
                onFinish = { viewModel.resetToInitial() }
            )
        }

        is InterviewUiState.Error -> {
            // Show error and go back
            LandingScreen(
                onStartInterview = { viewModel.selectDomain(it) }
            )
        }

        else -> {
            LandingScreen(
                onStartInterview = { viewModel.selectDomain(it) }
            )
        }
    }
}
