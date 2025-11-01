package com.interviewmirror.app.service

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.app.AlertDialog
import android.widget.EditText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real-time Speech Recognizer with live transcription
 * Provides continuous feedback while user is speaking
 * On emulator: Uses text input dialog
 */
@Singleton
class RealTimeSpeechRecognizer @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "🎤 REALTIME_SPEECH"
    }

    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    // Real-time transcription state
    private val _transcriptionState = MutableStateFlow<TranscriptionState>(TranscriptionState.Idle)
    val transcriptionState: StateFlow<TranscriptionState> = _transcriptionState.asStateFlow()

    // Audio level state for visualizer
    private val _audioLevel = MutableStateFlow(0f)
    val audioLevel: StateFlow<Float> = _audioLevel.asStateFlow()

    sealed class TranscriptionState {
        object Idle : TranscriptionState()
        object Listening : TranscriptionState()
        data class PartialResult(val text: String) : TranscriptionState()
        data class FinalResult(val text: String) : TranscriptionState()
        data class Error(val message: String) : TranscriptionState()
    }

    /**
     * Check if running on emulator
     */
    private fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk" == Build.PRODUCT)
    }

    /**
     * Start listening with real-time feedback
     */
    fun startListening() {
        if (isListening) {
            Log.w(TAG, "⚠️ Already listening")
            return
        }

        // Check if running on emulator
        if (isEmulator()) {
            Log.w(TAG, "⚠️ EMULATOR DETECTED - Using text input dialog")
            showTextInputDialog()
            return
        }

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.e(TAG, "❌ Speech recognition not available")
            _transcriptionState.value = TranscriptionState.Error("Speech recognition not available")
            return
        }

        Log.d(TAG, "🚀 Starting real-time speech recognition...")

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true) // Enable partial results
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000L)
            putExtra(
                RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
                3000L
            )
        }

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d(TAG, "✅ Ready for speech")
                isListening = true
                _transcriptionState.value = TranscriptionState.Listening
            }

            override fun onBeginningOfSpeech() {
                Log.d(TAG, "🗣️ User started speaking")
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Update audio level for visualizer (normalize to 0-1 range)
                val normalizedLevel = (rmsdB + 2f) / 12f
                _audioLevel.value = normalizedLevel.coerceIn(0f, 1f)
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // Audio data received
            }

            override fun onEndOfSpeech() {
                Log.d(TAG, "🛑 User stopped speaking")
            }

            override fun onError(error: Int) {
                val errorMsg = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech input"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                    else -> "Unknown error: $error"
                }

                Log.e(TAG, "❌ Recognition error: $errorMsg")

                // Don't treat timeout as error - just use what we have
                if (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT || error == SpeechRecognizer.ERROR_NO_MATCH) {
                    // Keep the last partial result if available
                    val currentState = _transcriptionState.value
                    if (currentState is TranscriptionState.PartialResult) {
                        _transcriptionState.value =
                            TranscriptionState.FinalResult(currentState.text)
                    } else {
                        _transcriptionState.value = TranscriptionState.FinalResult("")
                    }
                } else {
                    _transcriptionState.value = TranscriptionState.Error(errorMsg)
                }

                isListening = false
                _audioLevel.value = 0f
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val confidence = results?.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)

                val bestMatch = matches?.firstOrNull() ?: ""

                Log.d(TAG, "✅ Final result: \"$bestMatch\"")
                if (confidence != null && confidence.isNotEmpty()) {
                    Log.d(TAG, "   Confidence: ${(confidence[0] * 100).toInt()}%")
                }

                _transcriptionState.value = TranscriptionState.FinalResult(bestMatch)
                isListening = false
                _audioLevel.value = 0f
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches =
                    partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val partialText = matches?.firstOrNull() ?: ""

                if (partialText.isNotEmpty()) {
                    Log.d(TAG, "📝 Partial: \"$partialText\"")
                    _transcriptionState.value = TranscriptionState.PartialResult(partialText)
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                // Custom events
            }
        })

        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to start listening: ${e.message}")
            _transcriptionState.value = TranscriptionState.Error("Failed to start: ${e.message}")
            isListening = false
        }
    }

    /**
     * Show text input dialog for emulator testing
     */
    private fun showTextInputDialog() {
        Handler(Looper.getMainLooper()).post {
            isListening = true
            _transcriptionState.value = TranscriptionState.Listening

            // Simulate audio levels for UI
            simulateAudioLevels()

            val input = EditText(context).apply {
                hint = "Type your interview answer..."
                setPadding(50, 40, 50, 40)
                minLines = 5
                maxLines = 10

                // Show partial updates as user types
                addTextChangedListener(object : android.text.TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        val text = s?.toString() ?: ""
                        if (text.isNotEmpty()) {
                            _transcriptionState.value = TranscriptionState.PartialResult(text)
                        }
                    }

                    override fun afterTextChanged(s: android.text.Editable?) {}
                })
            }

            AlertDialog.Builder(context)
                .setTitle("🎤 Your Interview Answer")
                .setMessage("Since you're on an emulator, type your answer below.\nAI will analyze it just like spoken input!")
                .setView(input)
                .setPositiveButton("Submit Answer") { dialog, _ ->
                    val answer = input.text.toString().trim()
                    Log.i(TAG, "✅ User typed: \"$answer\"")
                    _transcriptionState.value = TranscriptionState.FinalResult(answer)
                    isListening = false
                    _audioLevel.value = 0f
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    Log.w(TAG, "❌ User cancelled input")
                    _transcriptionState.value = TranscriptionState.FinalResult("")
                    isListening = false
                    _audioLevel.value = 0f
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
        }
    }

    /**
     * Simulate audio levels for visual feedback on emulator
     */
    private fun simulateAudioLevels() {
        val handler = Handler(Looper.getMainLooper())
        var counter = 0

        val runnable = object : Runnable {
            override fun run() {
                if (isListening) {
                    // Simulate varying audio levels
                    val level = 0.3f + (Math.sin(counter * 0.1) * 0.3f).toFloat()
                    _audioLevel.value = level.coerceIn(0f, 1f)
                    counter++
                    handler.postDelayed(this, 100)
                }
            }
        }
        handler.post(runnable)
    }

    /**
     * Stop listening
     */
    fun stopListening() {
        Log.d(TAG, "🛑 Stopping speech recognition...")
        speechRecognizer?.stopListening()
        isListening = false
        _audioLevel.value = 0f
    }

    /**
     * Cancel and reset
     */
    fun cancel() {
        Log.d(TAG, "🚫 Cancelling speech recognition...")
        speechRecognizer?.cancel()
        speechRecognizer?.destroy()
        speechRecognizer = null
        isListening = false
        _audioLevel.value = 0f
        _transcriptionState.value = TranscriptionState.Idle
    }

    /**
     * Release resources
     */
    fun release() {
        cancel()
    }

    /**
     * Check if currently listening
     */
    fun isCurrentlyListening(): Boolean = isListening
}
