package com.interviewmirror.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for AI Interview Mirror
 * Initializes Hilt for dependency injection
 */
@HiltAndroidApp
class InterviewMirrorApp : Application()
