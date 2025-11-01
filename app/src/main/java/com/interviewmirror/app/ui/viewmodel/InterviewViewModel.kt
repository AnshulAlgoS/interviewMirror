package com.interviewmirror.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interviewmirror.app.data.model.AnalysisResult
import com.interviewmirror.app.data.model.InterviewDomain
import com.interviewmirror.app.data.model.InterviewQuestion
import com.interviewmirror.app.data.model.InterviewSession
import com.interviewmirror.app.data.model.QuestionResponse
import com.interviewmirror.app.data.repository.InterviewRepository
import com.interviewmirror.app.data.repository.QuestionRepository
import com.interviewmirror.app.sdk.RunAnywhereSDK
import com.interviewmirror.app.service.RealTimeSpeechRecognizer
import com.interviewmirror.app.service.TextToSpeechService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterviewViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val interviewRepository: InterviewRepository,
    private val runAnywhereSDK: RunAnywhereSDK,
    private val ttsService: TextToSpeechService,
    private val speechRecognizer: RealTimeSpeechRecognizer
) : ViewModel() {

    companion object {
        private const val TAG = "🎯 INTERVIEW_VM"
    }

    private val _uiState = MutableStateFlow<InterviewUiState>(InterviewUiState.Initial)
    val uiState: StateFlow<InterviewUiState> = _uiState.asStateFlow()

    // Real-time transcription state
    private val _liveTranscript = MutableStateFlow("")
    val liveTranscript: StateFlow<String> = _liveTranscript.asStateFlow()

    // Audio level for visualization
    private val _audioLevel = MutableStateFlow(0f)
    val audioLevel: StateFlow<Float> = _audioLevel.asStateFlow()

    // TTS speaking state
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private var currentDomain: InterviewDomain? = null
    private var currentQuestions: List<InterviewQuestion> = emptyList()
    private var currentQuestionIndex: Int = 0
    private var responses: MutableList<QuestionResponse> = mutableListOf()
    private var recordingStartTime: Long = 0

    init {
        initializeServices()
        observeSpeechRecognizer()
    }

    private fun initializeServices() {
        viewModelScope.launch {
            Log.d(TAG, "🚀 Initializing services...")

            // Initialize TTS
            val ttsSuccess = ttsService.initialize()
            Log.d(TAG, "TTS initialized: $ttsSuccess")

            // Initialize SDK
            val sdkSuccess = runAnywhereSDK.initialize()
            Log.d(TAG, "SDK initialized: $sdkSuccess")

            if (!sdkSuccess) {
                _uiState.value = InterviewUiState.Error("Failed to initialize AI services")
            }
        }
    }

    private fun observeSpeechRecognizer() {
        viewModelScope.launch {
            // Observe transcription state
            speechRecognizer.transcriptionState.collect { state ->
                when (state) {
                    is RealTimeSpeechRecognizer.TranscriptionState.PartialResult -> {
                        _liveTranscript.value = state.text
                        Log.d(TAG, "📝 Partial: ${state.text}")
                    }

                    is RealTimeSpeechRecognizer.TranscriptionState.FinalResult -> {
                        _liveTranscript.value = state.text
                        Log.d(TAG, "✅ Final: ${state.text}")
                    }

                    is RealTimeSpeechRecognizer.TranscriptionState.Error -> {
                        Log.e(TAG, "❌ Speech error: ${state.message}")
                    }

                    else -> {}
                }
            }
        }

        viewModelScope.launch {
            // Observe audio level
            speechRecognizer.audioLevel.collect { level ->
                _audioLevel.value = level
            }
        }
    }

    fun selectDomain(domain: InterviewDomain) {
        Log.d(TAG, "📚 Selected domain: ${domain.name}")
        currentDomain = domain
        currentQuestions = questionRepository.getQuestionsForDomain(domain)
        currentQuestionIndex = 0
        responses.clear()

        if (currentQuestions.isNotEmpty()) {
            val firstQuestion = currentQuestions[currentQuestionIndex]
            _uiState.value = InterviewUiState.Question(
                question = firstQuestion,
                questionNumber = currentQuestionIndex + 1,
                totalQuestions = currentQuestions.size
            )

            // AI speaks the first question
            speakQuestion(firstQuestion.question)
        }
    }

    private fun speakQuestion(questionText: String) {
        viewModelScope.launch {
            _isSpeaking.value = true
            Log.d(TAG, "🔊 AI speaking question...")
            ttsService.speak(questionText)
            _isSpeaking.value = false
            Log.d(TAG, "✅ AI finished speaking")
        }
    }

    fun startRecording() {
        viewModelScope.launch {
            Log.d(TAG, "🎤 Starting recording...")
            recordingStartTime = System.currentTimeMillis()
            _liveTranscript.value = ""

            val currentQuestion = currentQuestions[currentQuestionIndex]
            runAnywhereSDK.setInterviewContext(
                domain = currentDomain ?: InterviewDomain.TECH,
                question = currentQuestion.question,
                qNum = currentQuestionIndex + 1,
                total = currentQuestions.size
            )

            _uiState.value = InterviewUiState.Recording(
                question = currentQuestion,
                questionNumber = currentQuestionIndex + 1,
                totalQuestions = currentQuestions.size
            )

            // Start real-time speech recognition
            speechRecognizer.startListening()
        }
    }

    fun stopRecording() {
        viewModelScope.launch {
            Log.d(TAG, "🛑 Stopping recording...")

            // Stop speech recognizer
            speechRecognizer.stopListening()

            val duration = System.currentTimeMillis() - recordingStartTime
            val transcript = _liveTranscript.value

            Log.d(TAG, "📝 User transcript: \"$transcript\"")
            Log.d(TAG, "⏱️ Duration: ${duration}ms")

            _uiState.value = InterviewUiState.Analyzing

            // Analyze using SDK with real transcript
            try {
                val analysisResult = runAnywhereSDK.analyzeTranscript(transcript, duration)

                // Store the response
                responses.add(
                    QuestionResponse(
                        question = currentQuestions[currentQuestionIndex],
                        analysisResult = analysisResult
                    )
                )

                Log.d(TAG, "✅ Analysis complete")

                // Show feedback screen
                _uiState.value = InterviewUiState.Feedback(
                    question = currentQuestions[currentQuestionIndex],
                    analysisResult = analysisResult,
                    hasMoreQuestions = currentQuestionIndex < currentQuestions.size - 1
                )

                // AI speaks the feedback
                delay(500) // Small delay for UI transition
                speakFeedback(analysisResult)

            } catch (e: Exception) {
                Log.e(TAG, "❌ Analysis failed: ${e.message}", e)
                _uiState.value = InterviewUiState.Error("Analysis failed: ${e.message}")
            }
        }
    }

    private fun speakFeedback(result: AnalysisResult) {
        viewModelScope.launch {
            _isSpeaking.value = true

            // Speak the main feedback
            result.aiFeedback?.let { feedback ->
                Log.d(TAG, "🔊 AI speaking feedback...")
                ttsService.speak(feedback)
            }

            // Optionally speak the follow-up question
            if (result.aiFollowUpQuestion != null && currentQuestionIndex < currentQuestions.size - 1) {
                delay(500)
                Log.d(TAG, "🔊 AI speaking follow-up...")
                ttsService.speak(result.aiFollowUpQuestion)
            }

            _isSpeaking.value = false
            Log.d(TAG, "✅ AI finished speaking feedback")
        }
    }

    fun nextQuestion() {
        currentQuestionIndex++
        if (currentQuestionIndex < currentQuestions.size) {
            val nextQuestion = currentQuestions[currentQuestionIndex]
            _uiState.value = InterviewUiState.Question(
                question = nextQuestion,
                questionNumber = currentQuestionIndex + 1,
                totalQuestions = currentQuestions.size
            )

            // AI speaks the next question
            speakQuestion(nextQuestion.question)
        } else {
            showSessionSummary()
        }
    }

    fun endSession() {
        showSessionSummary()
    }

    private fun showSessionSummary() {
        Log.d(TAG, "📊 Generating session summary...")

        val avgConfidence = if (responses.isNotEmpty()) {
            responses.map { it.analysisResult.confidenceScore }.average().toFloat()
        } else 0f

        val totalFillers = responses.sumOf { it.analysisResult.fillerCount }

        val avgSpeechRate = if (responses.isNotEmpty()) {
            responses.map { it.analysisResult.speechRate }.average().toInt()
        } else 0

        val improvementAreas = mutableListOf<String>()
        if (avgConfidence < 0.6f) {
            improvementAreas.add("Work on speaking with more confidence")
        }
        if (totalFillers > responses.size * 3) {
            improvementAreas.add("Reduce filler words (um, like, actually)")
        }
        if (avgSpeechRate < 100) {
            improvementAreas.add("Try speaking a bit faster")
        } else if (avgSpeechRate > 170) {
            improvementAreas.add("Try slowing down your speech")
        }
        if (improvementAreas.isEmpty()) {
            improvementAreas.add("Great job! Keep practicing to maintain your skills")
        }

        val session = InterviewSession(
            domain = currentDomain ?: InterviewDomain.TECH,
            questionsAnswered = responses.size,
            averageConfidence = avgConfidence,
            totalFillerWords = totalFillers,
            averageSpeechRate = avgSpeechRate,
            improvementAreas = improvementAreas,
            endTime = System.currentTimeMillis()
        )

        _uiState.value = InterviewUiState.Summary(session)

        // AI speaks summary
        speakSummary(session)
    }

    private fun speakSummary(session: InterviewSession) {
        viewModelScope.launch {
            _isSpeaking.value = true

            val summaryText = buildString {
                append("Great work! You answered ${session.questionsAnswered} questions. ")
                append("Your average confidence was ${(session.averageConfidence * 100).toInt()} percent. ")

                if (session.averageConfidence >= 0.7f) {
                    append("That's excellent! ")
                } else if (session.averageConfidence >= 0.5f) {
                    append("That's a good start. ")
                } else {
                    append("Keep practicing to build more confidence. ")
                }
            }

            Log.d(TAG, "🔊 AI speaking summary...")
            ttsService.speak(summaryText)
            _isSpeaking.value = false
        }
    }

    fun saveSessionToFirebase(session: InterviewSession) {
        viewModelScope.launch {
            Log.d(TAG, "💾 Saving session to Firebase...")

            // Save to local database
            interviewRepository.insertSession(session)

            // Save to Firebase
            val result = interviewRepository.saveSessionToFirebase(session)
            if (result.isSuccess) {
                Log.d(TAG, "✅ Session saved successfully")
                _uiState.value = InterviewUiState.SaveSuccess

                // Speak success message
                ttsService.speak("Your progress has been saved successfully!")

                delay(2000)
                _uiState.value = InterviewUiState.Initial
            } else {
                Log.e(TAG, "❌ Failed to save session")
                _uiState.value = InterviewUiState.Error("Failed to save to Firebase")
            }
        }
    }

    fun resetToInitial() {
        Log.d(TAG, "🔄 Resetting to initial state...")

        // Stop any ongoing speech
        ttsService.stop()
        speechRecognizer.cancel()

        _uiState.value = InterviewUiState.Initial
        _liveTranscript.value = ""
        _audioLevel.value = 0f
        _isSpeaking.value = false

        currentDomain = null
        currentQuestions = emptyList()
        currentQuestionIndex = 0
        responses.clear()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "🧹 Cleaning up ViewModel...")
        runAnywhereSDK.release()
        ttsService.shutdown()
        speechRecognizer.release()
    }
}

sealed class InterviewUiState {
    object Initial : InterviewUiState()
    data class Question(
        val question: InterviewQuestion,
        val questionNumber: Int,
        val totalQuestions: Int
    ) : InterviewUiState()

    data class Recording(
        val question: InterviewQuestion,
        val questionNumber: Int,
        val totalQuestions: Int
    ) : InterviewUiState()

    object Analyzing : InterviewUiState()
    data class Feedback(
        val question: InterviewQuestion,
        val analysisResult: AnalysisResult,
        val hasMoreQuestions: Boolean
    ) : InterviewUiState()

    data class Summary(val session: InterviewSession) : InterviewUiState()
    object SaveSuccess : InterviewUiState()
    data class Error(val message: String) : InterviewUiState()
}
