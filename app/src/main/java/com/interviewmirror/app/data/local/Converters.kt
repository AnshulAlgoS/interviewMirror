package com.interviewmirror.app.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.interviewmirror.app.data.model.InterviewDomain

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromInterviewDomain(domain: InterviewDomain): String {
        return domain.name
    }

    @TypeConverter
    fun toInterviewDomain(value: String): InterviewDomain {
        return InterviewDomain.valueOf(value)
    }

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }
}
