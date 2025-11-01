package com.interviewmirror.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.interviewmirror.app.data.local.InterviewDao
import com.interviewmirror.app.data.model.InterviewSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterviewRepository @Inject constructor(
    private val interviewDao: InterviewDao,
    private val firestore: FirebaseFirestore
) {
    fun getAllSessions(): Flow<List<InterviewSession>> {
        return interviewDao.getAllSessions()
    }

    suspend fun insertSession(session: InterviewSession): Long {
        return interviewDao.insertSession(session)
    }

    suspend fun saveSessionToFirebase(session: InterviewSession): Result<String> {
        return try {
            val docRef = firestore.collection("interview_sessions")
                .add(session.toFirebaseMap())
                .await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSessionById(sessionId: Long): InterviewSession? {
        return interviewDao.getSessionById(sessionId)
    }
}
