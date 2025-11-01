package com.interviewmirror.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents a complete interview session
 */
@Entity(tableName = "interview_sessions")
data class InterviewSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String = "Anonymous",
    val domain: InterviewDomain,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val questionsAnswered: Int = 0,
    val averageConfidence: Float = 0f,
    val totalFillerWords: Int = 0,
    val averageSpeechRate: Int = 0,
    val improvementAreas: List<String> = emptyList()
) {
    fun toFirebaseMap(): Map<String, Any?> = mapOf(
        "username" to username,
        "domain" to domain.name,
        "startTime" to startTime,
        "endTime" to endTime,
        "questionsAnswered" to questionsAnswered,
        "averageConfidence" to averageConfidence,
        "totalFillerWords" to totalFillerWords,
        "averageSpeechRate" to averageSpeechRate,
        "improvementAreas" to improvementAreas,
        "date" to Date(startTime)
    )
}
