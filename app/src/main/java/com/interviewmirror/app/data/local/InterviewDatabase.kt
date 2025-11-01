package com.interviewmirror.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.interviewmirror.app.data.model.InterviewSession

@Database(
    entities = [InterviewSession::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class InterviewDatabase : RoomDatabase() {
    abstract fun interviewDao(): InterviewDao
}
