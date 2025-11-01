package com.interviewmirror.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DeepOcean,           // #26658C - primary buttons, headers
    secondary = MediumAqua,         // #54ACBF - secondary actions
    tertiary = AiryBlue,            // #A7EBF2 - soft accents
    background = DarkMidnight,      // #011C40 - main background
    surface = RichNavy,             // #023859 - cards, surfaces
    onPrimary = Color.White,        // Text on primary color
    onSecondary = DarkMidnight,     // Text on secondary (aqua needs dark text)
    onTertiary = DarkMidnight,      // Text on tertiary (light blue needs dark text)
    onBackground = AiryBlue,        // Text on dark background (use light blue for contrast)
    onSurface = Color.White,        // Text on surfaces
)

@Composable
fun AIInterviewMirrorTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DarkMidnight.toArgb()  // Use darkest color for status bar
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
