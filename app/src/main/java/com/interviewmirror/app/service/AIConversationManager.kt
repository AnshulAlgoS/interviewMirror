package com.interviewmirror.app.service

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import com.interviewmirror.app.BuildConfig
import com.interviewmirror.app.data.model.InterviewDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AI Conversation Manager - Real-time Interview Intelligence
 *
 * This manager acts as a REAL AI interviewer that:
 * - Analyzes user responses in real-time
 * - Generates contextual follow-up questions
 * - Provides personalized feedback
 * - Maintains conversation history
 */
@Singleton
class AIConversationManager @Inject constructor() {

    companion object {
        private const val TAG = "🔥 AI_INTERVIEWER"
    }

    private val apiKey: String = BuildConfig.GEMINI_API_KEY

    private val conversationModel by lazy {
        Log.d(TAG, "🚀 Initializing Gemini 1.5 Flash model...")
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey,
            generationConfig = generationConfig {
                temperature = 0.9f  // Higher for more creative follow-ups
                topK = 40
                topP = 0.95f
                maxOutputTokens = 800
            },
            safetySettings = listOf(
                SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE),
            )
        )
    }

    // Conversation history for context
    private val conversationHistory = mutableListOf<ConversationTurn>()

    data class ConversationTurn(
        val question: String,
        val userAnswer: String,
        val feedback: String? = null,
        val followUp: String? = null
    )

    data class InterviewerResponse(
        val feedback: String,
        val confidenceAssessment: String,
        val strengthsIdentified: List<String>,
        val areasToImprove: List<String>,
        val followUpQuestion: String?,
        val shouldContinue: Boolean
    )

    /**
     * 🔥 REAL AI ANALYSIS: Analyze user's actual spoken answer
     */
    suspend fun analyzeUserResponse(
        originalQuestion: String,
        userTranscript: String,
        domain: InterviewDomain,
        questionNumber: Int,
        totalQuestions: Int
    ): InterviewerResponse = withContext(Dispatchers.IO) {

        Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        Log.d(TAG, "📝 ANALYZING USER RESPONSE")
        Log.d(TAG, "Question #$questionNumber/$totalQuestions: $originalQuestion")
        Log.d(TAG, "User Answer: $userTranscript")
        Log.d(TAG, "Domain: ${domain.name}")
        Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

        if (apiKey.isEmpty() || apiKey == "your_gemini_api_key_here") {
            Log.w(TAG, "⚠️ WARNING: API key not configured! Using fallback analysis.")
            return@withContext generateFallbackAnalysis(userTranscript, originalQuestion, domain)
        }

        Log.d(TAG, "✅ API Key configured: ${apiKey.take(10)}...")
        Log.d(TAG, "🤖 Calling Gemini 1.5 Flash API...")

        try {
            val prompt = buildInterviewerPrompt(
                originalQuestion = originalQuestion,
                userAnswer = userTranscript,
                domain = domain,
                questionNumber = questionNumber,
                totalQuestions = totalQuestions,
                conversationHistory = conversationHistory
            )

            Log.d(TAG, "📤 SENDING PROMPT TO GEMINI:")
            Log.d(TAG, prompt.take(200) + "...")

            val startTime = System.currentTimeMillis()
            val response = conversationModel.generateContent(prompt)
            val endTime = System.currentTimeMillis()

            val aiResponse = response.text ?: run {
                Log.e(TAG, "❌ ERROR: Gemini returned null response!")
                return@withContext generateFallbackAnalysis(
                    userTranscript,
                    originalQuestion,
                    domain
                )
            }

            Log.d(TAG, "✅ RECEIVED AI RESPONSE in ${endTime - startTime}ms:")
            Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            Log.d(TAG, aiResponse)
            Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

            // Store in conversation history
            conversationHistory.add(
                ConversationTurn(
                    question = originalQuestion,
                    userAnswer = userTranscript
                )
            )

            Log.d(TAG, "💾 Conversation history size: ${conversationHistory.size}")

            val parsedResponse = parseAIResponse(aiResponse, questionNumber, totalQuestions)

            Log.d(TAG, "✨ PARSED AI FEEDBACK:")
            Log.d(TAG, "  Feedback: ${parsedResponse.feedback.take(100)}...")
            Log.d(TAG, "  Confidence: ${parsedResponse.confidenceAssessment}")
            Log.d(TAG, "  Strengths: ${parsedResponse.strengthsIdentified}")
            Log.d(TAG, "  Improvements: ${parsedResponse.areasToImprove}")
            Log.d(TAG, "  Follow-up: ${parsedResponse.followUpQuestion?.take(100)}")
            Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

            parsedResponse

        } catch (e: Exception) {
            Log.e(TAG, "❌ ERROR calling Gemini API: ${e.message}", e)
            Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
            Log.w(TAG, "⚠️ Falling back to local analysis")
            generateFallbackAnalysis(userTranscript, originalQuestion, domain)
        }
    }

    /**
     * Build interviewer prompt for realistic analysis
     */
    private fun buildInterviewerPrompt(
        originalQuestion: String,
        userAnswer: String,
        domain: InterviewDomain,
        questionNumber: Int,
        totalQuestions: Int,
        conversationHistory: List<ConversationTurn>
    ): String {
        val domainContext = when (domain) {
            InterviewDomain.TECH -> "technical interview for a software engineering position"
            InterviewDomain.HR -> "HR behavioral interview"
            InterviewDomain.PRODUCT -> "product management interview"
            InterviewDomain.DESIGN -> "design and UX interview"
        }

        val historyContext = if (conversationHistory.isNotEmpty()) {
            "Previous conversation:\n" + conversationHistory.takeLast(2).joinToString("\n") {
                "Q: ${it.question}\nA: ${it.userAnswer}"
            } + "\n\n"
        } else ""

        return """
You are an experienced job interviewer conducting a $domainContext.

$historyContext
Current Question Asked: "$originalQuestion"
Candidate's Answer: "$userAnswer"

This is question $questionNumber of $totalQuestions in the interview.

CRITICAL INSTRUCTIONS:
- BE HONEST. If the answer is weak, vague, or incomplete, SAY SO.
- DO NOT give fake praise. Only compliment when truly deserved.
- If the answer is too short (under 10 words), point out they need to elaborate.
- If they say generic things like "I have experience", challenge them: "What specific experience?"
- If they're clearly nervous or hesitant, acknowledge it but push them to speak up.
- If they give a strong, detailed answer with examples, THEN praise genuinely.
- Always engage like a real interviewer - curious, probing, sometimes skeptical.

Analyze the candidate's response and provide structured feedback in this format:

FEEDBACK: [2-3 sentences of HONEST feedback. If it's weak, say "That's quite vague - can you give me a specific example?" If it's strong, say "Great answer with good details!" Be real, not robotic.]

CONFIDENCE: [Honest assessment. If they sound uncertain, say "You seem hesitant - try to speak more assertively." If confident, say "Good confident delivery."]

STRENGTHS: [List 1-2 ACTUAL strengths IF they exist. If the answer was too short or vague, say "Attempted to answer" or "Shows willingness to engage". Be honest.]

IMPROVEMENTS: [List 1-2 SPECIFIC areas that NEED improvement. Don't sugarcoat. Examples: "Answer was too brief - need at least 30 seconds" or "Too many filler words" or "No concrete examples given"]

FOLLOWUP: [Ask ONE intelligent follow-up that:
- If answer was weak: "Can you give me a SPECIFIC example?"
- If answer was vague: "What EXACTLY did you do?"
- If answer was good: Dig deeper - "How would you handle X situation?"
- If last question, say "NONE"]

REMEMBER:
- Short answers (under 10 words) = weak answer, needs more detail
- Vague answers without examples = challenge them to be specific
- Generic statements = probe for concrete details
- Confident, detailed answers with examples = genuinely praise
- Empty or silent = "I need you to actually answer the question"

Be conversational but HONEST. Act like a real interviewer who wants substance, not fluff.
""".trimIndent()
    }

    /**
     * Parse AI response into structured format
     */
    private fun parseAIResponse(
        aiText: String,
        questionNumber: Int,
        totalQuestions: Int
    ): InterviewerResponse {
        val feedbackMatch =
            Regex("FEEDBACK:\\s*(.+?)(?=\\n\\n|CONFIDENCE:)", RegexOption.DOT_MATCHES_ALL)
                .find(aiText)?.groupValues?.get(1)?.trim() ?: "Thank you for your response."

        val confidenceMatch =
            Regex("CONFIDENCE:\\s*(.+?)(?=\\n\\n|STRENGTHS:)", RegexOption.DOT_MATCHES_ALL)
                .find(aiText)?.groupValues?.get(1)?.trim() ?: "Good delivery"

        val strengthsMatch =
            Regex("STRENGTHS:\\s*(.+?)(?=\\n\\n|IMPROVEMENTS:)", RegexOption.DOT_MATCHES_ALL)
                .find(aiText)?.groupValues?.get(1)?.trim()
        val strengths = strengthsMatch?.split("|")?.map { it.trim() }?.filter { it.isNotEmpty() }
            ?: listOf("Good communication")

        val improvementsMatch =
            Regex("IMPROVEMENTS:\\s*(.+?)(?=\\n\\n|FOLLOWUP:)", RegexOption.DOT_MATCHES_ALL)
                .find(aiText)?.groupValues?.get(1)?.trim()
        val improvements =
            improvementsMatch?.split("|")?.map { it.trim() }?.filter { it.isNotEmpty() }
                ?: listOf("Consider adding more specific examples")

        val followUpMatch = Regex("FOLLOWUP:\\s*(.+?)$", RegexOption.DOT_MATCHES_ALL)
            .find(aiText)?.groupValues?.get(1)?.trim()
        val followUp = followUpMatch?.takeIf { it != "NONE" && questionNumber < totalQuestions }

        return InterviewerResponse(
            feedback = feedbackMatch,
            confidenceAssessment = confidenceMatch,
            strengthsIdentified = strengths,
            areasToImprove = improvements,
            followUpQuestion = followUp,
            shouldContinue = questionNumber < totalQuestions
        )
    }

    /**
     * Fallback analysis when AI is unavailable
     */
    private fun generateFallbackAnalysis(
        userTranscript: String,
        question: String,
        domain: InterviewDomain
    ): InterviewerResponse {
        val wordCount = userTranscript.split(" ").size
        val hasFillers =
            userTranscript.contains(Regex("\\b(um|uh|like|actually)\\b", RegexOption.IGNORE_CASE))

        val feedback = when {
            wordCount < 20 -> "Your answer was quite brief. Try to elaborate more with specific examples."
            wordCount > 100 -> "Good detailed response! Make sure to stay focused on the key points."
            hasFillers -> "Solid answer! Try to reduce filler words for more polished delivery."
            else -> "Great response! You communicated clearly and stayed on topic."
        }

        return InterviewerResponse(
            feedback = feedback,
            confidenceAssessment = if (hasFillers) "Moderate confidence with some hesitation" else "Confident delivery",
            strengthsIdentified = listOf("Clear communication", "Relevant content"),
            areasToImprove = listOf("Add specific examples", "Reduce filler words"),
            followUpQuestion = null,
            shouldContinue = false
        )
    }

    /**
     * Reset conversation history for new session
     */
    fun resetConversation() {
        conversationHistory.clear()
    }

    /**
     * Check if API is configured
     */
    fun isConfigured(): Boolean {
        return apiKey.isNotEmpty() && apiKey != "your_gemini_api_key_here"
    }
}
