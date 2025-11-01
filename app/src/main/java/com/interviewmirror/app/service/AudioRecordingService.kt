package com.interviewmirror.app.service

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for recording audio from microphone
 */
@Singleton
class AudioRecordingService @Inject constructor() {
    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private val audioData = ByteArrayOutputStream()

    companion object {
        private const val SAMPLE_RATE = 44100
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }

    private val bufferSize = AudioRecord.getMinBufferSize(
        SAMPLE_RATE,
        CHANNEL_CONFIG,
        AUDIO_FORMAT
    )

    /**
     * Start recording audio
     */
    suspend fun startRecording(): Boolean = withContext(Dispatchers.IO) {
        try {
            audioData.reset()

            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                return@withContext false
            }

            audioRecord?.startRecording()
            isRecording = true

            // Read audio data in background
            val buffer = ByteArray(bufferSize)
            while (isRecording) {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (read > 0) {
                    audioData.write(buffer, 0, read)
                }
            }

            true
        } catch (e: SecurityException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Stop recording and return audio data
     */
    suspend fun stopRecording(): ByteArray = withContext(Dispatchers.IO) {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null

        audioData.toByteArray()
    }

    /**
     * Get current recording status
     */
    fun isRecording(): Boolean = isRecording
}
