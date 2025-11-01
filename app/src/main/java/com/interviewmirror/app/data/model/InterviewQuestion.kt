package com.interviewmirror.app.data.model

data class InterviewQuestion(
    val id: Int,
    val domain: InterviewDomain,
    val question: String,
    val tips: String = ""
)
