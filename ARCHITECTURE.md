# 🏗️ Architecture - AI Interview Mirror

This document provides a detailed overview of the application architecture, design patterns, and
technical decisions.

---

## 📐 Architecture Pattern: MVVM + Clean Architecture

The app follows **MVVM (Model-View-ViewModel)** pattern with **Clean Architecture** principles:

```
┌─────────────────────────────────────────────────────────┐
│                     Presentation Layer                   │
│  ┌────────────┐  ┌──────────────┐  ┌────────────────┐  │
│  │  Screens   │←→│  ViewModel   │←→│  UI State      │  │
│  │ (Compose)  │  │ (StateFlow)  │  │  (Sealed)      │  │
│  └────────────┘  └──────────────┘  └────────────────┘  │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│                     Domain Layer                         │
│  ┌────────────┐  ┌──────────────┐  ┌────────────────┐  │
│  │   Models   │  │  Repository  │  │  Use Cases     │  │
│  │  (Data)    │  │ (Interface)  │  │  (Business)    │  │
│  └────────────┘  └──────────────┘  └────────────────┘  │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│                      Data Layer                          │
│  ┌────────────┐  ┌──────────────┐  ┌────────────────┐  │
│  │    Room    │  │   Firebase   │  │  RunAnywhere   │  │
│  │  Database  │  │  Firestore   │  │      SDK       │  │
│  └────────────┘  └──────────────┘  └────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

---

## 📦 Module Structure

### **Presentation Layer** (`ui/`)

#### Screens (`ui/screens/`)

- **LandingScreen**: Entry point with domain selection
- **QuestionScreen**: Displays interview question
- **RecordingScreen**: Audio recording with waveform
- **AnalyzingScreen**: Shows processing state
- **FeedbackScreen**: Displays analysis results
- **SummaryScreen**: Session statistics and summary

#### Navigation (`ui/navigation/`)

- **InterviewNavigation**: Central navigation controller
- State-based navigation using sealed classes
- No NavHost complexity - direct screen composition

#### Theme (`ui/theme/`)

- **Color.kt**: Color palette
- **Theme.kt**: Material3 theme configuration
- **Type.kt**: Typography definitions

#### ViewModel (`ui/viewmodel/`)

- **InterviewViewModel**: Main business logic controller
- **InterviewUiState**: Sealed class for all UI states
- StateFlow for reactive state management

---

## 🎯 Domain Layer

### Models (`data/model/`)

```kotlin
// Domain types
enum class InterviewDomain { TECH, HR, PRODUCT, DESIGN }

// Interview question
data class InterviewQuestion(
    val id: Int,
    val domain: InterviewDomain,
    val question: String,
    val tips: String
)

// Analysis result from SDK
data class AnalysisResult(
    val tone: Tone,
    val confidenceScore: Float,
    val speechRate: Int,
    val fillerWords: List<FillerWord>,
    val totalWords: Int,
    val duration: Long,
    val transcript: String
)

// Session data
@Entity
data class InterviewSession(
    @PrimaryKey val id: Long,
    val domain: InterviewDomain,
    val questionsAnswered: Int,
    val averageConfidence: Float,
    val totalFillerWords: Int,
    val averageSpeechRate: Int,
    val improvementAreas: List<String>
)
```

### Repositories (`data/repository/`)

#### InterviewRepository

- Manages interview session data
- Coordinates between Room DB and Firebase
- Provides data to ViewModel

#### QuestionRepository

- Provides domain-specific questions
- In-memory question bank
- Can be extended to fetch from API

---

## 💾 Data Layer

### Local Storage (`data/local/`)

#### Room Database

```kotlin
@Database(entities = [InterviewSession::class])
abstract class InterviewDatabase : RoomDatabase() {
    abstract fun interviewDao(): InterviewDao
}

@Dao
interface InterviewDao {
    @Insert suspend fun insertSession(session: InterviewSession): Long
    @Query fun getAllSessions(): Flow<List<InterviewSession>>
}
```

**Type Converters:**

- `InterviewDomain` ↔ String
- `List<String>` ↔ JSON String

### Cloud Storage

#### Firebase Firestore

- Collection: `interview_sessions`
- Document fields match `InterviewSession`
- Automatic timestamp on creation
- Queryable by user, date, domain

---

## 🤖 RunAnywhere SDK Integration

### SDK Structure (`sdk/RunAnywhereSDK.kt`)

```kotlin
@Singleton
class RunAnywhereSDK @Inject constructor(
    private val context: Context
) {
    // Initialize ML models
    suspend fun initialize(): Boolean
    
    // Analyze audio on-device
    suspend fun analyzeAudio(
        audioData: ByteArray,
        durationMs: Long
    ): AnalysisResult
    
    // Release resources
    fun release()
}
```

### On-Device AI Pipeline

```
Audio Input (PCM bytes)
    ↓
┌──────────────────────────────┐
│   Speech-to-Text Model       │
│   (Simulated: generates      │
│    sample transcripts)       │
└──────────────┬───────────────┘
               ↓
┌──────────────────────────────┐
│   Filler Word Detection      │
│   (Pattern matching on       │
│    transcript)               │
└──────────────┬───────────────┘
               ↓
┌──────────────────────────────┐
│   Speech Rate Calculation    │
│   (words / minute)           │
└──────────────┬───────────────┘
               ↓
┌──────────────────────────────┐
│   Confidence Scoring         │
│   (Based on rate + fillers)  │
└──────────────┬───────────────┘
               ↓
┌──────────────────────────────┐
│   Tone Classification        │
│   (Confident/Calm/Neutral    │
│    /Nervous/Anxious)         │
└──────────────┬───────────────┘
               ↓
         AnalysisResult
```

**Note:** Current implementation simulates SDK behavior. In production:

- Load actual TensorFlow Lite models
- Run real STT inference
- Use trained sentiment analysis
- Implement audio feature extraction

---

## 🎙️ Audio Recording Service

### AudioRecordingService (`service/`)

```kotlin
@Singleton
class AudioRecordingService @Inject constructor() {
    // Start recording from mic
    suspend fun startRecording(): Boolean
    
    // Stop and return audio data
    suspend fun stopRecording(): ByteArray
    
    // Check recording status
    fun isRecording(): Boolean
}
```

**Audio Configuration:**

- Sample Rate: 44100 Hz
- Channel: Mono
- Encoding: PCM 16-bit
- Buffer: Minimum buffer size

---

## 🔧 Dependency Injection (Hilt)

### App Module (`di/AppModule.kt`)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton
    fun provideInterviewDatabase(
        @ApplicationContext context: Context
    ): InterviewDatabase
    
    @Provides @Singleton
    fun provideInterviewDao(
        database: InterviewDatabase
    ): InterviewDao
    
    @Provides @Singleton
    fun provideFirestore(): FirebaseFirestore
    
    @Provides @Singleton
    fun provideContext(
        @ApplicationContext context: Context
    ): Context
}
```

**Dependency Graph:**

```
Application
    ↓
Hilt Container
    ├─→ InterviewDatabase (Singleton)
    ├─→ InterviewDao (Singleton)
    ├─→ FirebaseFirestore (Singleton)
    ├─→ RunAnywhereSDK (Singleton)
    ├─→ AudioRecordingService (Singleton)
    ├─→ InterviewRepository (Singleton)
    ├─→ QuestionRepository (Singleton)
    └─→ InterviewViewModel (ViewModelScoped)
```

---

## 🔄 State Management

### UI State Pattern

```kotlin
sealed class InterviewUiState {
    object Initial : InterviewUiState()
    
    data class Question(
        val question: InterviewQuestion,
        val questionNumber: Int,
        val totalQuestions: Int
    ) : InterviewUiState()
    
    data class Recording(
        val question: InterviewQuestion,
        val questionNumber: Int,
        val totalQuestions: Int
    ) : InterviewUiState()
    
    object Analyzing : InterviewUiState()
    
    data class Feedback(
        val question: InterviewQuestion,
        val analysisResult: AnalysisResult,
        val hasMoreQuestions: Boolean
    ) : InterviewUiState()
    
    data class Summary(
        val session: InterviewSession
    ) : InterviewUiState()
    
    data class Error(val message: String) : InterviewUiState()
}
```

### State Flow

```kotlin
// In ViewModel
private val _uiState = MutableStateFlow<InterviewUiState>(Initial)
val uiState: StateFlow<InterviewUiState> = _uiState.asStateFlow()

// In Composable
val uiState by viewModel.uiState.collectAsState()
when (uiState) {
    is Initial -> LandingScreen()
    is Question -> QuestionScreen()
    is Recording -> RecordingScreen()
    // ...
}
```

---

## 🎨 UI Architecture (Jetpack Compose)

### Composable Structure

```
InterviewNavigation
    ├─→ LandingScreen
    │   └─→ DomainSelectionContent
    ├─→ QuestionScreen
    ├─→ RecordingScreen
    │   └─→ AnimatedWaveform (Canvas)
    ├─→ AnalyzingScreen
    │   └─→ LoadingAnimation
    ├─→ FeedbackScreen
    │   ├─→ ConfidenceMeter
    │   ├─→ SpeechRateCard
    │   └─→ FillerWordsCard
    └─→ SummaryScreen
        ├─→ StatsCard (reusable)
        └─→ ImprovementsList
```

### Reusable Components

- **StatsCard**: Display metric with icon
- **DomainButton**: Domain selection button
- **ConfidenceMeter**: Progress indicator
- **GradientBackground**: Consistent theming

---

## 🔐 Security & Privacy

### Data Protection

1. **On-Device Processing**
    - Audio analyzed locally
    - No cloud upload of recordings
    - RunAnywhere SDK runs offline

2. **Minimal Data Collection**
    - Only anonymized stats saved
    - No PII (Personally Identifiable Information)
    - User control over cloud backup

3. **Permissions**
    - Runtime permission for RECORD_AUDIO
    - Clear permission rationale
    - Graceful degradation if denied

---

## ⚡ Performance Optimizations

### 1. Lazy Loading

- Questions loaded on-demand
- Composables recomposed efficiently

### 2. Coroutines

- Non-blocking audio recording
- Async SDK processing
- Structured concurrency

### 3. State Management

- Single source of truth (StateFlow)
- Minimal recompositions
- Remember computations

### 4. Resource Management

- Proper cleanup in ViewModel.onCleared()
- Release SDK resources
- Cancel coroutines

---

## 🧪 Testing Strategy

### Unit Tests

- ViewModel logic
- Repository functions
- SDK analysis algorithms
- Data transformations

### Integration Tests

- Database operations
- Firebase interactions
- Navigation flow

### UI Tests (Compose)

- Screen rendering
- User interactions
- State transitions

---

## 📊 Analytics & Monitoring

### Firebase Analytics

- Session start/end events
- Domain selection tracking
- Feature usage metrics
- Error tracking

### Crashlytics (Future)

- Crash reporting
- Non-fatal exceptions
- Custom logs

---

## 🚀 Scalability Considerations

### Current Limitations

- Single user (no authentication)
- 5 questions per domain
- Simulated SDK processing

### Future Enhancements

- Multi-user support with Firebase Auth
- Dynamic question loading from API
- Real ML model integration
- Cloud Functions for processing
- Analytics dashboard

---

## 🔄 Data Flow Example

### Complete Interview Flow

```
1. User Action: Tap "Start Interview"
   ↓
2. ViewModel: selectDomain(TECH)
   ↓
3. QuestionRepository: getQuestionsForDomain(TECH)
   ↓
4. ViewModel: Update state to Question
   ↓
5. UI: Render QuestionScreen
   ↓
6. User Action: Tap "Record Answer"
   ↓
7. ViewModel: startRecording()
   ↓
8. AudioRecordingService: Start capturing audio
   ↓
9. ViewModel: Update state to Recording
   ↓
10. UI: Show animated waveform
    ↓
11. User Action: Tap "Stop Recording"
    ↓
12. AudioRecordingService: Return audio bytes
    ↓
13. ViewModel: Update state to Analyzing
    ↓
14. RunAnywhereSDK: analyzeAudio()
    ↓
15. SDK: Return AnalysisResult
    ↓
16. ViewModel: Store result, update to Feedback
    ↓
17. UI: Display feedback with metrics
    ↓
18. User Action: "Next Question" or "End Session"
    ↓
19. ViewModel: Calculate session summary
    ↓
20. ViewModel: Update state to Summary
    ↓
21. UI: Show session stats
    ↓
22. User Action: "Save Progress"
    ↓
23. InterviewRepository: insertSession() [Room]
    ↓
24. InterviewRepository: saveToFirebase() [Firestore]
    ↓
25. ViewModel: Update state to SaveSuccess
    ↓
26. UI: Show success, navigate to Initial
```

---

## 📚 Design Patterns Used

1. **Singleton**: Repositories, SDK, Services
2. **Repository Pattern**: Data abstraction
3. **Observer Pattern**: StateFlow observing
4. **Factory Pattern**: Hilt dependency creation
5. **Strategy Pattern**: Different analysis algorithms
6. **State Pattern**: UI state management
7. **Dependency Injection**: Hilt framework

---

## 🎓 Key Takeaways

- ✅ Clean separation of concerns
- ✅ Unidirectional data flow
- ✅ Reactive state management
- ✅ Testable architecture
- ✅ Scalable and maintainable
- ✅ Modern Android best practices
- ✅ Privacy-first design

---

**For more details, see the source code and inline comments.**
