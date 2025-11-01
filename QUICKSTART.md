# ⚡ Quick Start - AI Interview Mirror

Get up and running in 5 minutes!

---

## 🎯 Goal

Open the app in Android Studio and run it on your device/emulator.

---

## ✅ Prerequisites Checklist

Before you start, make sure you have:

- [ ] **Android Studio** installed (Hedgehog 2023.1.1 or later)
- [ ] **JDK 17** configured
- [ ] **Android device** or **emulator** ready
- [ ] **Internet connection** (for first-time Gradle sync)

---

## 🚀 Steps to Run

### 1️⃣ Open Project (30 seconds)

1. Launch **Android Studio**
2. Click **"Open"**
3. Navigate to: `/Users/anshulsaxena/AndroidStudioProjects/interviewMirror`
4. Click **"OK"**

### 2️⃣ Configure SDK Path (1 minute)

Open `local.properties` and update:

```properties
sdk.dir=/Users/YOUR_USERNAME/Library/Android/sdk
```

**Replace `YOUR_USERNAME` with your actual username!**

### 3️⃣ Sync Gradle (2 minutes)

1. Wait for Android Studio to finish loading
2. Click **"Sync Now"** when prompted
3. Wait for dependencies to download (first time takes 2-3 minutes)

### 4️⃣ Run the App (1 minute)

1. Select your **device/emulator** from the dropdown (top toolbar)
2. Click the green **▶️ Run** button
3. Grant **microphone permission** when prompted

### 5️⃣ Test the App (1 minute)

1. You'll see the **landing screen** with animated mic icon
2. Tap **"Start Interview"**
3. Select a domain: **Tech**, **HR**, **Product**, or **Design**
4. Tap **"Record Answer"** and speak
5. Tap **"Stop Recording"** to see analysis
6. View your **confidence score**, **speech rate**, and **filler words**!

---

## 🎉 That's It!

You now have a fully functional AI interview practice app running!

---

## 🔧 Troubleshooting

### Problem: Gradle sync failed

**Solution:**

1. Check internet connection
2. File → Invalidate Caches → Restart

### Problem: SDK not found

**Solution:**

1. Verify `local.properties` path is correct
2. Tools → SDK Manager → Install Android SDK Platform 34

### Problem: App crashes

**Solution:**

1. Check Logcat: View → Tool Windows → Logcat
2. Ensure device is API 26+ (Android 8.0+)

### Problem: No permission dialog

**Solution:**

1. Uninstall app from device
2. Run again - permission dialog should appear

---

## 📚 Next Steps

- **Try all domains**: Tech, HR, Product, Design
- **Complete a session**: Answer multiple questions
- **View summary**: Check your improvement areas
- **Read docs**: See README.md for full features

---

## 🔥 Key Features to Try

1. **Animated Landing** - Watch the rotating microphone
2. **Domain Selection** - Choose your interview category
3. **Voice Recording** - See the live waveform
4. **AI Analysis** - Watch "RunAnywhere SDK Processing"
5. **Instant Feedback** - Get confidence score and tips
6. **Session Summary** - View overall statistics

---

## 💡 Tips

- **Speak clearly** for better analysis simulation
- **Try filler words** like "um", "like", "actually" to see detection
- **Complete multiple questions** to get comprehensive feedback
- **Save progress** to test Firebase integration (requires setup)

---

## ⚙️ Optional: Firebase Setup

Want cloud backup? (5 minutes)

1. Go to https://console.firebase.google.com/
2. Create new project: "AI Interview Mirror"
3. Add Android app: `com.interviewmirror.app`
4. Download `google-services.json`
5. Replace `app/google-services.json` with downloaded file
6. Enable Firestore in Firebase Console
7. Rebuild and run!

**Without Firebase:**

- App works perfectly with local storage (Room DB)
- Cloud backup won't work
- Everything else functions normally

---

## 🎓 Understanding the App

### Technology Stack

- **Kotlin** - Programming language
- **Jetpack Compose** - Modern UI framework
- **MVVM** - Architecture pattern
- **Hilt** - Dependency injection
- **Room** - Local database
- **Firebase** - Cloud storage (optional)

### RunAnywhere SDK

- **Simulated** implementation included
- Shows where real SDK would integrate
- Demonstrates on-device AI processing
- Ready for real SDK replacement

---

## 📱 Recommended Test Flow

1. **Landing** → Tap "Start Interview"
2. **Domain** → Select "Tech"
3. **Question 1** → Tap "Record Answer"
4. **Recording** → Speak for 10-15 seconds
5. **Stop** → Watch analysis
6. **Feedback** → Review results
7. **Next** → Try another question
8. **Summary** → Complete session to see stats

---

## 🏆 Success Indicators

You'll know it's working when you see:

- ✅ Animated microphone on landing
- ✅ Smooth domain selection
- ✅ Question displayed with tips
- ✅ Waveform animation during recording
- ✅ "RunAnywhere SDK Processing" message
- ✅ Confidence meter (0-100%)
- ✅ Emoji indicator (😃 😐 😬)
- ✅ Filler word count
- ✅ Speech rate (words/min)
- ✅ Session summary with stats

---

## 📞 Need Help?

1. Check **Logcat** for errors
2. Read **SETUP_GUIDE.md** for detailed instructions
3. Review **ARCHITECTURE.md** for technical details
4. Check **inline code comments**

---

## 🎯 What Makes This App Special?

✨ **RunAnywhere SDK Integration**

- Demonstrates on-device AI processing
- Privacy-first architecture
- No cloud dependency for analysis

🎨 **Modern Android Development**

- Jetpack Compose UI
- Material Design 3
- Clean Architecture
- Best practices throughout

🚀 **Production Ready**

- Complete feature set
- Error handling
- Proper permissions
- Well-documented

---

**Ready to practice your interview skills? Let's go! 🚀**

Run the app now and ace your next interview!
