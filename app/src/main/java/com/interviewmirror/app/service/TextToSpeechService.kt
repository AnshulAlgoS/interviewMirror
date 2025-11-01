package com.interviewmirror.app.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Text-to-Speech Service - AI speaks back to the user
 * Provides natural voice feedback during interviews
 */
@Singleton
class TextToSpeechService @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "🔊 TTS_SERVICE"
    }

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    /**
     * Initialize TTS engine
     */
    suspend fun initialize(): Boolean = suspendCancellableCoroutine { continuation ->
        Log.d(TAG, "🚀 Initializing Text-to-Speech...")

        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setPitch(1.0f)
                tts?.setSpeechRate(0.95f) // Slightly slower for clarity
                isInitialized = true
                Log.d(TAG, "✅ TTS initialized successfully")
                continuation.resume(true)
            } else {
                Log.e(TAG, "❌ TTS initialization failed")
                continuation.resume(false)
            }
        }
    }

    /**
     * Speak text and wait for completion
     */
    suspend fun speak(text: String, utteranceId: String = UUID.randomUUID().toString()): Boolean =
        suspendCancellableCoroutine { continuation ->
            if (!isInitialized || tts == null) {
                Log.e(TAG, "❌ TTS not initialized")
                continuation.resume(false)
                return@suspendCancellableCoroutine
            }

            Log.d(TAG, "🔊 Speaking: ${text.take(50)}...")

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    Log.d(TAG, "🎤 Started speaking")
                }

                override fun onDone(utteranceId: String?) {
                    Log.d(TAG, "✅ Finished speaking")
                    continuation.resume(true)
                }

                override fun onError(utteranceId: String?) {
                    Log.e(TAG, "❌ TTS error")
                    continuation.resume(false)
                }
            })

            val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)

            if (result != TextToSpeech.SUCCESS) {
                Log.e(TAG, "❌ Failed to start speaking")
                continuation.resume(false)
            }
        }

    /**
     * Stop speaking immediately
     */
    fun stop() {
        tts?.stop()
    }

    /**
     * Check if currently speaking
     */
    fun isSpeaking(): Boolean {
        return tts?.isSpeaking == true
    }

    /**
     * Release TTS resources
     */
    fun shutdown() {
        Log.d(TAG, "🛑 Shutting down TTS")
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
