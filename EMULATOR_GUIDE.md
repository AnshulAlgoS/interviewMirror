# 🖥️ Emulator Usage Guide

## Running Interview Mirror on Android Emulator

Interview Mirror now includes **full emulator support** with automatic detection and text input
fallback!

---

## ✅ What Works on Emulator

### Fully Functional Features:

- ✅ **All UI animations** - Smooth 60 FPS performance
- ✅ **Live waveform visualization** - Simulated audio levels
- ✅ **Text input for answers** - Type instead of speak
- ✅ **Real-time transcript updates** - As you type
- ✅ **Full AI analysis** - Gemini 1.5 Flash powered
- ✅ **Text-to-Speech feedback** - AI speaks back to you
- ✅ **Complete interview flow** - End-to-end experience
- ✅ **Firebase integration** - Save progress to cloud

### What's Different:

- 🎤 **No real microphone** → Text input dialog appears
- 📝 **Type instead of speak** → Same AI analysis
- 🌊 **Simulated audio waves** → Visual feedback while typing

---

## 🚀 How to Use on Emulator

### 1. **Start Interview Normally**

- Launch app
- Select domain (Tech/HR/Product/Design)
- Tap "Record Answer"

### 2. **Text Input Dialog Appears**

You'll see:

```
🎤 Your Interview Answer

Since you're on an emulator, type your answer below.
AI will analyze it just like spoken input!

[Text input box with 5-10 lines]

[Cancel]  [Submit Answer]
```

### 3. **Type Your Answer**

- Type naturally as if speaking
- Watch the live transcript update in the background
- See the waveform animate with simulated audio
- Submit when done

### 4. **Get AI Feedback**

- AI analyzes your typed text
- Gemini provides honest feedback
- AI speaks feedback via TTS
- Continue with next question

---

## 🎨 Visual Experience

### Recording Screen on Emulator:

```
┌────────────────────────────────┐
│  Question 1/5           ● REC  │
│  ▬▬▬▬▬▬▬▬▬▬▬▬▬░░░░░░           │
│                                │
│         ◉ ← Pulsing mic        │
│                                │
│      Listening...              │
│   Speak clearly and naturally  │
│                                │
│  ┌──────────────────────────┐ │
│  │  🎤 45%                   │ │
│  │  ~ ~ ~ ~ ~ (animated)    │ │
│  └──────────────────────────┘ │
│                                │
│  ┌──────────────────────────┐ │
│  │ 🎤 Your Response          │ │
│  │ ─────────────────────     │ │
│  │ [Live typed text shows]   │ │
│  │ "I have experience with"  │ │  ← Updates as you type!
│  │ "Android development..."  │ │
│  └──────────────────────────┘ │
│                                │
│  [  Stop & Analyze  ]          │
└────────────────────────────────┘
```

---

## 💡 Tips for Emulator Testing

### Writing Good Answers:

1. **Type as if speaking** - Use natural language
2. **Include examples** - "When I built X, I used Y"
3. **Add metrics** - "Improved performance by 60%"
4. **20-50 words ideal** - Not too short, not too long
5. **Watch live updates** - See transcript appear in real-time

### Testing Different Scenarios:

```kotlin
// Short answer (AI will criticize)
"I have experience."

// Vague answer (AI will ask for specifics)
"I worked on various projects and learned a lot."

// Good answer (AI will praise)
"When I built the payment service for our e-commerce app, 
I chose PostgreSQL for ACID transactions. I designed the 
schema with proper indexing which reduced query time by 60% 
and handled 1000 transactions per second."
```

---

## 🔧 Technical Details

### Emulator Detection Logic:

```kotlin
private fun isEmulator(): Boolean {
    return (Build.FINGERPRINT.startsWith("generic")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            || "google_sdk" == Build.PRODUCT)
}
```

### Flow on Emulator:

```
User taps "Record Answer"
        ↓
Detect environment (Emulator!)
        ↓
Show text input dialog
        ↓
Simulate audio levels (0.3-0.6)
        ↓
User types → Partial results emit
        ↓
User submits → Final result emit
        ↓
SDK analyzes transcript
        ↓
Gemini provides feedback
        ↓
TTS speaks feedback
```

---

## 🎯 Recommended Emulator Setup

### Best Emulators:

1. **Pixel 4** (API 34/35) - ✅ Tested and working
2. **Pixel 7** (API 34/35) - ✅ Recommended
3. **Medium Phone** (API 34+) - ✅ Works great

### Settings:

```
RAM: 2GB minimum (4GB recommended)
Storage: 4GB internal
Graphics: Hardware (GLES 2.0)
Internet: Connected (for Gemini API)
```

### Enabling TTS (for AI voice feedback):

1. Open emulator Settings
2. System → Languages & input
3. Text-to-speech output
4. Preferred engine: Google Text-to-speech
5. Test: "The quick brown fox" should speak

---

## 🐛 Troubleshooting

### "No dialog appears when recording"

**Solution**: Check logcat for emulator detection

```bash
adb logcat | grep "REALTIME_SPEECH"
# Should see: "EMULATOR DETECTED - Using text input dialog"
```

### "Waveform not animating"

**Solution**: Simulated audio levels should work automatically

- Check if `isListening` state is true
- Look for `simulateAudioLevels()` logs

### "Can't see typed text in transcript"

**Solution**: TextWatcher should update `PartialResult`

- Type slowly to see updates
- Check logcat for `📝 Partial:` logs

### "AI says 'no speech detected'"

**Solution**: Make sure you're typing something!

- Empty submission = silent response
- Type at least 3-5 words minimum

---

## 📊 Testing Checklist

Use this checklist to verify emulator functionality:

- [ ] App launches successfully
- [ ] Domain selection works
- [ ] "Record Answer" opens text dialog
- [ ] Dialog shows proper title and message
- [ ] Typing updates live transcript in background
- [ ] Waveform animates (simulated levels)
- [ ] Audio level percentage shows (e.g., "🎤 45%")
- [ ] Submit button sends answer
- [ ] AI analyzes typed text
- [ ] Feedback screen shows results
- [ ] TTS speaks feedback (if TTS engine installed)
- [ ] Follow-up questions work
- [ ] Session summary calculates correctly
- [ ] Firebase save works

---

## 🎬 Example Session on Emulator

### Complete Flow:

1. **Launch** → Pixel 4 emulator starts
2. **Select** → "💻 Tech" domain
3. **Question** → AI speaks: "Tell me about your experience..."
4. **Record** → Dialog appears
5. **Type**:
   ```
   I have 5 years of Android development experience, 
   specializing in Kotlin and Jetpack Compose. Most 
   recently, I built a fintech app that handles 50K 
   daily transactions. I implemented MVVM architecture 
   with clean code principles and achieved 95% test coverage.
   ```
6. **Submit** → AI analyzes (2-3 seconds)
7. **Feedback** →
   ```
   💬 "Great answer with excellent specifics! You mentioned 
   concrete numbers and technologies which shows real depth."
   
   🎯 Confidence: Excellent delivery with strong examples
   
   ✨ Strengths:
   • Quantified experience (5 years, 50K transactions)
   • Specific technologies mentioned
   
   ❓ Follow-up: "Tell me about a specific technical 
   challenge you faced in that fintech app"
   ```
8. **Continue** → Next question or end session

---

## 🌟 Benefits of Emulator Testing

### For Developers:

- ✅ Rapid iteration without physical device
- ✅ Consistent test environment
- ✅ Easy debugging with Android Studio
- ✅ Screenshot/screen recording built-in

### For Users:

- ✅ Practice typing interview answers
- ✅ Perfect for non-native English speakers
- ✅ Time to think and edit before submitting
- ✅ Same AI quality as voice input

---

## 📝 Notes

- **Typing vs Speaking**: AI analysis is identical - it only cares about content quality
- **Simulated Audio**: Visual feedback only - no actual sound waves recorded
- **Performance**: Emulator may be slower than real device for AI calls
- **TTS Quality**: Depends on emulator's installed TTS engine

---

**Ready to practice on emulator? Just build, install, and start typing! 🚀**
