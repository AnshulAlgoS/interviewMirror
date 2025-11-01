package com.interviewmirror.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.interviewmirror.app.data.model.InterviewSession
import kotlinx.coroutines.flow.Flow

@Dao
interface InterviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: InterviewSession): Long

    @Query("SELECT * FROM interview_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<InterviewSession>>

    @Query("SELECT * FROM interview_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): InterviewSession?

    @Query("DELETE FROM interview_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Long)
}
