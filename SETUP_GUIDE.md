# 🚀 Setup Guide - AI Interview Mirror

This guide will help you set up and run the AI Interview Mirror app on your local machine.

---

## ⚙️ Prerequisites

Before you begin, ensure you have the following installed:

### 1. **Android Studio**

- Version: Hedgehog (2023.1.1) or later
- Download: https://developer.android.com/studio

### 2. **Java Development Kit (JDK)**

- Version: JDK 17 (required)
- Android Studio includes JDK, but you can also download from: https://adoptium.net/

### 3. **Android SDK**

- Minimum SDK: API 26 (Android 8.0)
- Target SDK: API 34 (Android 14)
- These will be installed through Android Studio SDK Manager

---

## 📥 Installation Steps

### Step 1: Open Project in Android Studio

1. Launch Android Studio
2. Click "Open" or "Open an Existing Project"
3. Navigate to: `/Users/anshulsaxena/AndroidStudioProjects/interviewMirror`
4. Click "OK"

### Step 2: Configure Android SDK Path

1. Open `local.properties` file in the project root
2. Update the SDK path for your system:

**For macOS:**

```properties
sdk.dir=/Users/YOUR_USERNAME/Library/Android/sdk
```

**For Windows:**

```properties
sdk.dir=C\:\\Users\\YOUR_USERNAME\\AppData\\Local\\Android\\sdk
```

**For Linux:**

```properties
sdk.dir=/home/YOUR_USERNAME/Android/Sdk
```

Replace `YOUR_USERNAME` with your actual username.

### Step 3: Configure Firebase

#### Option A: Use Your Own Firebase Project (Recommended)

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add Project" or select existing project
3. Enter project name: "AI Interview Mirror" (or any name)
4. Follow setup wizard (disable Google Analytics if not needed)
5. Click "Add App" → Android (🤖)
6. Enter package name: `com.interviewmirror.app`
7. Download `google-services.json`
8. Replace `app/google-services.json` with downloaded file
9. In Firebase Console, enable Firestore:
    - Go to Build → Firestore Database
    - Click "Create database"
    - Start in "Test mode" (for development)
    - Select location and create

#### Option B: Use Placeholder (Limited Functionality)

The project includes a placeholder `google-services.json` that allows the app to build, but Firebase
features won't work:

- Session data won't be saved to cloud
- Local storage (Room DB) will still work

### Step 4: Sync Gradle

1. Android Studio will prompt to sync Gradle files
2. Click "Sync Now" in the notification banner
3. Or manually: File → Sync Project with Gradle Files
4. Wait for sync to complete (first sync may take a few minutes)

### Step 5: Setup Emulator or Device

#### Option A: Android Emulator

1. Open AVD Manager: Tools → Device Manager
2. Click "Create Device"
3. Select device: Pixel 6 or similar
4. Select system image: API 34 (Android 14) - Download if needed
5. Name it and finish
6. Click ▶️ to start emulator

#### Option B: Physical Device

1. Enable Developer Options on your Android device:
    - Go to Settings → About Phone
    - Tap "Build Number" 7 times
2. Enable USB Debugging:
    - Settings → Developer Options → USB Debugging
3. Connect device via USB
4. Allow USB Debugging when prompted on device

### Step 6: Grant Permissions

The app requires microphone permission. On first run:

1. App will request RECORD_AUDIO permission
2. Click "Allow" to enable voice recording feature

---

## ▶️ Running the App

### From Android Studio:

1. Select your device/emulator from the device dropdown (top toolbar)
2. Click the green ▶️ "Run" button
3. Or press `Shift + F10` (Windows/Linux) or `Control + R` (macOS)

### From Terminal:

```bash
# Install debug build
./gradlew installDebug

# Run on connected device
adb shell am start -n com.interviewmirror.app/.MainActivity
```

---

## 🧪 Verify Installation

### 1. App Launches Successfully

- You should see the landing screen with animated microphone icon
- Title: "AI Interview Mirror"
- Tagline: "Your pocket interviewer that never sleeps."

### 2. Navigation Works

- Tap "Start Interview"
- Select a domain (Tech, HR, Product, Design)
- Question screen appears

### 3. Recording Works

- Tap "Record Answer"
- Grant microphone permission if prompted
- Animated waveform appears
- Tap "Stop Recording"

### 4. Analysis Displays

- "RunAnywhere SDK Processing..." appears
- Feedback screen shows:
    - Confidence meter
    - Speech rate
    - Filler word count
    - Emoji indicator

### 5. Session Summary

- Complete a question or tap "End Session"
- Summary screen shows statistics
- "Save Progress" button available (requires Firebase)

---

## 🛠️ Troubleshooting

### Issue: Gradle Sync Failed

**Solution:**

1. Check internet connection
2. File → Invalidate Caches → Invalidate and Restart
3. Update Gradle wrapper: `./gradlew wrapper --gradle-version 8.2`

### Issue: SDK Not Found

**Solution:**

1. Verify `local.properties` has correct `sdk.dir` path
2. Open SDK Manager: Tools → SDK Manager
3. Install Android SDK Platform 34 and SDK Build-Tools

### Issue: App Crashes on Launch

**Solution:**

1. Check Logcat for error messages: View → Tool Windows → Logcat
2. Ensure minimum SDK version on device is API 26+
3. Clean and rebuild: Build → Clean Project → Rebuild Project

### Issue: Microphone Not Working

**Solution:**

1. Check permission granted in Settings → Apps → AI Interview Mirror → Permissions
2. On emulator, check that microphone is enabled in AVD settings
3. Try on physical device

### Issue: Firebase Errors

**Solution:**

1. Verify `google-services.json` is in `app/` directory
2. Check package name matches: `com.interviewmirror.app`
3. Firestore database is created and in test mode
4. Internet permission is granted

### Issue: Build Errors

**Solution:**

```bash
# Clean build
./gradlew clean

# Rebuild
./gradlew build

# Or in Android Studio:
# Build → Clean Project
# Build → Rebuild Project
```

---

## 📊 Build Variants

### Debug Build (Default)

- Used for development
- Includes debug symbols
- Not optimized

```bash
./gradlew assembleDebug
```

### Release Build

- Optimized and minified
- Requires signing configuration

```bash
./gradlew assembleRelease
```

---

## 🔍 Debugging

### Enable Verbose Logging

Add to `gradle.properties`:

```properties
org.gradle.logging.level=debug
```

### View Logs

In Android Studio:

1. View → Tool Windows → Logcat
2. Filter by package: `com.interviewmirror.app`
3. Filter by tag: `InterviewMirror`

### Debug Mode

1. Set breakpoints in code
2. Click 🐞 "Debug" button instead of Run
3. Use step-through debugger

---

## 📱 Testing on Different Devices

### Recommended Test Devices:

- **Phone**: Pixel 6, Samsung Galaxy S21+
- **Tablet**: Pixel Tablet (for landscape testing)
- **API Levels**: 26, 29, 31, 34

### Screen Orientations:

- Portrait (primary)
- Landscape (Compose handles automatically)

---

## 🎯 Next Steps After Setup

1. **Explore the App**
    - Try all interview domains
    - Complete multiple sessions
    - Check session summary

2. **Modify Questions**
    - Edit `QuestionRepository.kt`
    - Add your own domain-specific questions

3. **Customize UI**
    - Modify colors in `Color.kt`
    - Adjust typography in `Type.kt`
    - Update theme in `Theme.kt`

4. **Integrate Real RunAnywhere SDK**
    - Replace simulated SDK in `RunAnywhereSDK.kt`
    - Add actual ML models
    - Implement real STT and analysis

5. **Deploy**
    - Create signing key
    - Build release APK
    - Test on multiple devices
    - Publish to Play Store (optional)

---

## 📚 Additional Resources

- [Android Developer Documentation](https://developer.android.com/)
- [Jetpack Compose Guide](https://developer.android.com/jetpack/compose)
- [Firebase Documentation](https://firebase.google.com/docs)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Material Design 3](https://m3.material.io/)

---

## 💬 Get Help

If you encounter issues:

1. Check Logcat for error messages
2. Review this guide's troubleshooting section
3. Verify all prerequisites are installed
4. Check Firebase configuration
5. Try on a different device/emulator

---

## ✅ Setup Checklist

- [ ] Android Studio installed (Hedgehog 2023.1.1+)
- [ ] JDK 17 configured
- [ ] Android SDK installed (API 26-34)
- [ ] Project opened in Android Studio
- [ ] `local.properties` configured with SDK path
- [ ] Firebase `google-services.json` added (or using placeholder)
- [ ] Gradle sync completed successfully
- [ ] Emulator created or device connected
- [ ] App builds without errors
- [ ] App runs and launches successfully
- [ ] Microphone permission granted
- [ ] All screens navigate correctly
- [ ] Recording and analysis work

---

**🎉 Congratulations! You're ready to start using AI Interview Mirror!**

Need help? Check the troubleshooting section or review the README.md for more information.
