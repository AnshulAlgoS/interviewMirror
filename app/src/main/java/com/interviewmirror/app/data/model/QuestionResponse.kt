package com.interviewmirror.app.data.model

/**
 * Represents a single question-answer pair with analysis
 */
data class QuestionResponse(
    val question: InterviewQuestion,
    val analysisResult: AnalysisResult,
    val timestamp: Long = System.currentTimeMillis()
)
