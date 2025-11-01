package com.interviewmirror.app.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.interviewmirror.app.data.local.InterviewDao
import com.interviewmirror.app.data.local.InterviewDatabase
import com.interviewmirror.app.service.SpeechToTextService
import com.interviewmirror.app.service.TextToSpeechService
import com.interviewmirror.app.service.RealTimeSpeechRecognizer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideInterviewDatabase(
        @ApplicationContext context: Context
    ): InterviewDatabase {
        return Room.databaseBuilder(
            context,
            InterviewDatabase::class.java,
            "interview_mirror_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideInterviewDao(database: InterviewDatabase): InterviewDao {
        return database.interviewDao()
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideSpeechToTextService(@ApplicationContext context: Context): SpeechToTextService {
        return SpeechToTextService(context)
    }

    @Provides
    @Singleton
    fun provideTextToSpeechService(@ApplicationContext context: Context): TextToSpeechService {
        return TextToSpeechService(context)
    }

    @Provides
    @Singleton
    fun provideRealTimeSpeechRecognizer(@ApplicationContext context: Context): RealTimeSpeechRecognizer {
        return RealTimeSpeechRecognizer(context)
    }
}
