package com.interviewmirror.app.service

import android.app.AlertDialog
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
import android.widget.EditText
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Speech-to-Text Service - Converts user speech to text
 * On EMULATOR: Shows text input dialog (no mic available)
 * On REAL DEVICE: Uses Android Speech Recognition
 */
@Singleton
class SpeechToTextService @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "🎙️ SPEECH_TO_TEXT"
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
     * Convert audio to text
     * On emulator: Shows text input dialog for typing answer
     * On device: Uses real speech recognition
     */
    suspend fun transcribeAudio(audioBytes: ByteArray, durationMs: Long): String {
        Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        Log.d(TAG, "🎙️ TRANSCRIBING USER SPEECH")
        Log.d(TAG, "Audio data: ${audioBytes.size} bytes, Duration: ${durationMs}ms")
        Log.d(TAG, "Running on: ${if (isEmulator()) "EMULATOR" else "REAL DEVICE"}")

        return if (isEmulator()) {
            Log.w(TAG, "⚠️ EMULATOR DETECTED - Using TEXT INPUT DIALOG")
            showTextInputDialog()
        } else {
            Log.d(TAG, "📱 REAL DEVICE - Using speech recognition")
            transcribeWithSpeechRecognizer()
        }
    }

    /**
     * Show text input dialog for emulator testing
     * This allows users to TYPE their answer and get real AI feedback!
     */
    private suspend fun showTextInputDialog(): String =
        suspendCancellableCoroutine { continuation ->
            Handler(Looper.getMainLooper()).post {
                val input = EditText(context).apply {
                    hint = "Type your answer here..."
                    setPadding(50, 40, 50, 40)
                    minLines = 4
                    maxLines = 8
            }

            AlertDialog.Builder(context)
                .setTitle("🎤 Type Your Answer")
                .setMessage("Since we're on emulator, type your interview answer:")
                .setView(input)
                .setPositiveButton("Submit") { dialog, _ ->
                    val answer = input.text.toString().trim()
                    Log.i(TAG, "✅ User typed: \"$answer\"")
                    Log.i(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                    dialog.dismiss()
                    continuation.resume(answer)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    Log.w(TAG, "❌ User cancelled input")
                    dialog.dismiss()
                    continuation.resume("")
                }
                .setCancelable(false)
                .show()
        }

        continuation.invokeOnCancellation {
            Log.d(TAG, "🚫 Dialog cancelled")
        }
    }

    /**
     * Use real Android Speech Recognition (for real devices)
     */
    private suspend fun transcribeWithSpeechRecognizer(): String =
        suspendCancellableCoroutine { continuation ->
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                Log.e(TAG, "❌ Speech recognition not available on this device")
                continuation.resume("")
                return@suspendCancellableCoroutine
            }

            val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d(TAG, "🎤 Ready for speech")
            }

            override fun onBeginningOfSpeech() {
                Log.d(TAG, "🗣️ User started speaking")
            }

            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                Log.d(TAG, "🛑 User stopped speaking")
            }

            override fun onError(error: Int) {
                val errorMsg = when (error) {
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
                    else -> "Error $error"
                }
                Log.e(TAG, "❌ Speech recognition error: $errorMsg")
                continuation.resume("")
                speechRecognizer.destroy()
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val transcript = matches?.firstOrNull() ?: ""

                Log.d(TAG, "✅ TRANSCRIPTION SUCCESSFUL!")
                Log.d(TAG, "📝 User said: \"$transcript\"")
                Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

                continuation.resume(transcript)
                speechRecognizer.destroy()
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        continuation.invokeOnCancellation {
            speechRecognizer.cancel()
            speechRecognizer.destroy()
        }

        speechRecognizer.startListening(recognizerIntent)
    }
}
