package com.interviewmirror.app.data.model

/**
 * Result from RunAnywhere SDK on-device audio analysis + AI Interviewer Feedback
 */
data class AnalysisResult(
    val tone: Tone = Tone.NEUTRAL,
    val confidenceScore: Float = 0.5f, // 0.0 to 1.0
    val speechRate: Int = 0, // words per minute
    val fillerWords: List<FillerWord> = emptyList(),
    val totalWords: Int = 0,
    val duration: Long = 0L, // milliseconds
    val transcript: String = "",
    // 🔥 NEW: Real AI Interviewer Feedback
    val aiFeedback: String? = null,
    val aiConfidenceAssessment: String? = null,
    val aiStrengths: List<String> = emptyList(),
    val aiImprovements: List<String> = emptyList(),
    val aiFollowUpQuestion: String? = null
) {
    val fillerCount: Int
        get() = fillerWords.size

    val fillerPercentage: Float
        get() = if (totalWords > 0) (fillerCount.toFloat() / totalWords) * 100 else 0f

    enum class Tone {
        CONFIDENT,
        CALM,
        NEUTRAL,
        NERVOUS,
        ANXIOUS
    }
}

data class FillerWord(
    val word: String,
    val timestamp: Long
)
