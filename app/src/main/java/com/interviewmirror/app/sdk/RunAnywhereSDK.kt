package com.interviewmirror.app.sdk

import android.content.Context
import android.util.Log
import com.interviewmirror.app.data.model.AnalysisResult
import com.interviewmirror.app.data.model.FillerWord
import com.interviewmirror.app.data.model.InterviewDomain
import com.interviewmirror.app.service.AIConversationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * RunAnywhere SDK Integration - REAL SPEECH + REAL AI ANALYSIS!
 *
 * This class now:
 * 1. Accepts REAL transcribed speech from real-time recognizer
 * 2. Analyzes that REAL speech with AI interviewer
 * 3. Provides honest, intelligent feedback (not fake praise!)
 *
 * Features:
 * - Real speech analysis from transcript
 * - Honest AI analysis (praises when deserved, criticizes when needed)
 * - Two-way conversation with memory
 * - Context-aware follow-up questions
 */
@Singleton
class RunAnywhereSDK @Inject constructor(
    private val context: Context,
    private val aiConversationManager: AIConversationManager
) {
    companion object {
        private const val TAG = "🚀 SDK_MAIN"
    }

    private var isInitialized = false
    private val fillerWordPatterns =
        listOf("um", "uh", "like", "actually", "so", "you know", "basically", "literally")

    // Store current interview context
    private var currentDomain: InterviewDomain = InterviewDomain.TECH
    private var currentQuestion: String = ""
    private var questionNumber: Int = 1
    private var totalQuestions: Int = 5

    /**
     * Initialize the RunAnywhere SDK with required ML models
     */
    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🚀 Initializing RunAnywhere SDK with REAL Speech + AI...")
            delay(500)
            aiConversationManager.resetConversation()
            isInitialized = true
            Log.d(TAG, "✅ SDK initialized successfully!")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to initialize SDK: ${e.message}")
            false
        }
    }

    /**
     * Set the current interview context for AI response generation
     */
    fun setInterviewContext(
        domain: InterviewDomain,
        question: String,
        qNum: Int = 1,
        total: Int = 5
    ) {
        currentDomain = domain
        currentQuestion = question
        questionNumber = qNum
        totalQuestions = total

        Log.d(TAG, "📝 Interview context set: Q$qNum/$total - ${domain.name}")
        Log.d(TAG, "   Question: $question")
    }

    /**
     * 🔥 Analyze user speech with REAL AI using provided transcript
     *
     * @param transcript The actual transcribed user speech
     * @param durationMs Recording duration in milliseconds
     * @return AnalysisResult with HONEST AI feedback based on real user input
     */
    suspend fun analyzeTranscript(transcript: String, durationMs: Long): AnalysisResult =
        withContext(Dispatchers.IO) {
            if (!isInitialized) {
                throw IllegalStateException("RunAnywhere SDK not initialized")
            }

            Log.d(TAG, "═══════════════════════════════════════════════")
            Log.d(TAG, "🎯 ANALYZING REAL USER SPEECH")
            Log.d(TAG, "Duration: ${durationMs}ms")
            Log.d(TAG, "═══════════════════════════════════════════════")

            Log.d(TAG, "")
            Log.d(TAG, "🎤 TRANSCRIBED TEXT:")
            Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            Log.d(TAG, "\"$transcript\"")
            Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            Log.d(TAG, "Length: ${transcript.length} chars")

            // Check if user was silent or gave very short response
            if (transcript.isBlank()) {
                Log.w(TAG, "")
                Log.w(TAG, "⚠️ USER WAS SILENT - No speech detected")
                Log.w(TAG, "   Creating silence analysis response...")
                return@withContext createSilenceAnalysis()
            }

            val words = transcript.split(" ").filter { it.isNotBlank() }
            val totalWords = words.size

            Log.d(TAG, "")
            Log.d(TAG, "📊 Speech metrics: $totalWords words")

            // Very short answers get flagged
            if (totalWords < 3) {
                Log.w(TAG, "⚠️ VERY SHORT ANSWER: Only $totalWords words")
                Log.w(TAG, "   This will likely result in critical feedback from AI")
            } else if (totalWords < 10) {
                Log.w(TAG, "⚠️ SHORT ANSWER: $totalWords words")
                Log.w(TAG, "   AI may ask for more detail")
            } else if (totalWords >= 20) {
                Log.i(TAG, "✅ GOOD LENGTH: $totalWords words")
                Log.i(TAG, "   This should get positive AI feedback")
            }

            val speechRate = if (durationMs > 0) ((totalWords * 60000L) / durationMs).toInt() else 0

            // Detect filler words
            val fillerWords = detectFillerWords(transcript, durationMs)
            Log.d(
                TAG,
                "💬 Filler words detected: ${fillerWords.size} (${fillerWords.joinToString { it.word }})"
            )

            // Calculate confidence based on filler percentage and speech rate
            val fillerPercentage =
                if (totalWords > 0) (fillerWords.size.toFloat() / totalWords) else 0f
            val confidenceScore = calculateConfidenceScore(speechRate, fillerPercentage, totalWords)
            Log.d(TAG, "🎯 Confidence score: ${(confidenceScore * 100).toInt()}%")

            // Determine tone from confidence and speech patterns
            val tone = determineTone(confidenceScore, speechRate)
            Log.d(TAG, "😊 Tone assessed: ${tone.name}")

            // 🔥 Get REAL AI INTERVIEWER ANALYSIS of the REAL transcript
            Log.d(TAG, "")
            Log.d(TAG, "📍 Getting AI interviewer analysis of USER'S REAL answer...")
            val aiResponse = aiConversationManager.analyzeUserResponse(
                originalQuestion = currentQuestion,
                userTranscript = transcript,
                domain = currentDomain,
                questionNumber = questionNumber,
                totalQuestions = totalQuestions
            )
            Log.d(TAG, "✅ AI analysis complete!")

            Log.d(TAG, "")
            Log.d(TAG, "═══════════════════════════════════════════════")
            Log.d(TAG, "✨ FINAL ANALYSIS RESULT:")
            Log.d(TAG, "   User said: \"${transcript.take(50)}...\"")
            Log.d(TAG, "   Tone: ${tone.name}")
            Log.d(TAG, "   Confidence: ${(confidenceScore * 100).toInt()}%")
            Log.d(TAG, "   Speech Rate: $speechRate WPM")
            Log.d(TAG, "   Filler Count: ${fillerWords.size}")
            Log.d(TAG, "   AI Feedback: ${aiResponse.feedback.take(50)}...")
            Log.d(
                TAG,
                "   Follow-up Q: ${if (aiResponse.followUpQuestion != null) "✅ Generated" else "❌ None"}"
            )
            Log.d(TAG, "═══════════════════════════════════════════════")

            AnalysisResult(
                tone = tone,
                confidenceScore = confidenceScore,
                speechRate = speechRate,
                fillerWords = fillerWords,
                totalWords = totalWords,
                duration = durationMs,
                transcript = transcript,
                // 🔥 Real AI Interviewer Feedback based on REAL user input
                aiFeedback = aiResponse.feedback,
                aiConfidenceAssessment = aiResponse.confidenceAssessment,
                aiStrengths = aiResponse.strengthsIdentified,
                aiImprovements = aiResponse.areasToImprove,
                aiFollowUpQuestion = aiResponse.followUpQuestion
            )
        }

    /**
     * Legacy method - kept for compatibility
     * Now internally calls analyzeTranscript with empty transcript
     */
    @Deprecated("Use analyzeTranscript with real transcript instead")
    suspend fun analyzeAudio(audioData: ByteArray, durationMs: Long): AnalysisResult {
        // For backward compatibility, analyze with empty transcript
        return analyzeTranscript("", durationMs)
    }

    /**
     * Handle case where user was completely silent
     */
    private fun createSilenceAnalysis(): AnalysisResult {
        Log.d(TAG, "Creating silence analysis...")
        return AnalysisResult(
            tone = AnalysisResult.Tone.ANXIOUS,
            confidenceScore = 0.1f,
            speechRate = 0,
            fillerWords = emptyList(),
            totalWords = 0,
            duration = 0L,
            transcript = "",
            aiFeedback = "I didn't hear anything. Take your time - want me to repeat the question? Remember, there's no rush. Just speak naturally and share your thoughts.",
            aiConfidenceAssessment = "No speech detected. It's okay to be nervous!",
            aiStrengths = listOf("Taking time to think"),
            aiImprovements = listOf(
                "Try to verbalize your thoughts",
                "Take a deep breath and start with something simple"
            ),
            aiFollowUpQuestion = "Would you like to try again, or shall I rephrase the question?"
        )
    }

    /**
     * Detect filler words in transcript
     */
    private fun detectFillerWords(transcript: String, durationMs: Long): List<FillerWord> {
        val words = transcript.lowercase().split(" ", ",", ".")
        val fillers = mutableListOf<FillerWord>()
        var currentTime = 0L

        words.forEach { word ->
            if (word.trim() in fillerWordPatterns) {
                fillers.add(
                    FillerWord(
                        word = word.trim(),
                        timestamp = currentTime
                    )
                )
            }
            currentTime += (durationMs / words.size)
        }

        return fillers
    }

    /**
     * Calculate confidence score based on speech metrics
     * Now considers word count - very short answers get penalized
     */
    private fun calculateConfidenceScore(
        speechRate: Int,
        fillerPercentage: Float,
        wordCount: Int
    ): Float {
        // Penalize very short answers
        val lengthScore = when {
            wordCount == 0 -> 0.0f
            wordCount < 5 -> 0.3f  // Very short
            wordCount < 10 -> 0.5f // Short
            wordCount < 20 -> 0.7f // Okay
            wordCount in 20..100 -> 1.0f // Good length
            else -> 0.8f // Too long
        }

        // Ideal speech rate: 120-150 words per minute
        val rateScore = when {
            speechRate == 0 -> 0.2f
            speechRate in 120..150 -> 1.0f
            speechRate in 100..170 -> 0.8f
            speechRate in 80..190 -> 0.6f
            else -> 0.4f
        }

        // Penalize high filler word usage
        val fillerScore = when {
            fillerPercentage < 0.05f -> 1.0f
            fillerPercentage < 0.10f -> 0.8f
            fillerPercentage < 0.15f -> 0.6f
            else -> 0.4f
        }

        // Combine all factors
        return ((lengthScore * 0.4f + rateScore * 0.3f + fillerScore * 0.3f)).coerceIn(0f, 1f)
    }

    /**
     * Determine tone from confidence and speech patterns
     */
    private fun determineTone(confidence: Float, speechRate: Int): AnalysisResult.Tone {
        return when {
            confidence >= 0.8f && speechRate in 120..150 -> AnalysisResult.Tone.CONFIDENT
            confidence >= 0.6f && speechRate in 100..160 -> AnalysisResult.Tone.CALM
            confidence >= 0.5f -> AnalysisResult.Tone.NEUTRAL
            confidence >= 0.3f -> AnalysisResult.Tone.NERVOUS
            else -> AnalysisResult.Tone.ANXIOUS
        }
    }

    /**
     * Release SDK resources
     */
    fun release() {
        isInitialized = false
        aiConversationManager.resetConversation()
    }
}
