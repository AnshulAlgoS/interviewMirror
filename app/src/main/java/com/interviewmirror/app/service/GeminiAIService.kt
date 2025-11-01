package com.interviewmirror.app.service

import android.content.Context
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
 * Gemini AI Service for generating real-time interview responses
 * This replaces hardcoded responses with actual AI-generated content
 */
@Singleton
class GeminiAIService @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "🎤 TRANSCRIPT_GEN"
    }

    private val apiKey: String = BuildConfig.GEMINI_API_KEY

    private val generativeModel by lazy {
        Log.d(TAG, "🚀 Initializing Gemini model for transcript generation...")
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey,
            generationConfig = generationConfig {
                temperature = 0.7f
                topK = 40
                topP = 0.95f
                maxOutputTokens = 500
            },
            safetySettings = listOf(
                SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE),
            )
        )
    }

    /**
     * Generate a realistic interview response based on the question and domain
     * This simulates what a candidate would say in an interview
     */
    suspend fun generateInterviewResponse(
        question: String,
        domain: InterviewDomain,
        includeFillers: Boolean = true
    ): String = withContext(Dispatchers.IO) {
        Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        Log.d(TAG, "🎤 GENERATING CANDIDATE TRANSCRIPT")
        Log.d(TAG, "Question: $question")
        Log.d(TAG, "Domain: ${domain.name}")
        Log.d(TAG, "Include fillers: $includeFillers")

        try {
            if (apiKey.isEmpty() || apiKey == "your_gemini_api_key_here") {
                Log.w(TAG, "⚠️ API key not configured! Using fallback transcript.")
                return@withContext generateFallbackResponse(question, domain, includeFillers)
            }

            Log.d(TAG, "✅ API Key configured. Calling Gemini...")

            val prompt = buildPrompt(question, domain, includeFillers)
            Log.d(TAG, "📤 Sending prompt to Gemini...")

            val startTime = System.currentTimeMillis()
            val response = generativeModel.generateContent(prompt)
            val endTime = System.currentTimeMillis()

            val transcript = response.text?.trim()

            if (transcript != null) {
                Log.d(TAG, "✅ Generated transcript in ${endTime - startTime}ms:")
                Log.d(TAG, "   \"$transcript\"")
                Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                transcript
            } else {
                Log.w(TAG, "⚠️ Gemini returned null. Using fallback.")
                generateFallbackResponse(question, domain, includeFillers)
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ ERROR generating transcript: ${e.message}", e)
            Log.w(TAG, "⚠️ Using fallback transcript")
            // Fallback to simulated response if API fails
            generateFallbackResponse(question, domain, includeFillers)
        }
    }

    /**
     * Build an appropriate prompt for the AI based on interview context
     */
    private fun buildPrompt(
        question: String,
        domain: InterviewDomain,
        includeFillers: Boolean
    ): String {
        val domainContext = when (domain) {
            InterviewDomain.TECH -> "software development, programming, and technical skills"
            InterviewDomain.HR -> "HR, human resources, workplace culture, and soft skills"
            InterviewDomain.PRODUCT -> "product management, product strategy, and user experience"
            InterviewDomain.DESIGN -> "design, UI/UX, creative process, and visual design"
        }

        val fillerInstruction = if (includeFillers) {
            "Include natural speech patterns with occasional filler words like 'um', 'uh', 'like', 'actually', 'you know' to make it sound realistic."
        } else {
            "Speak clearly and professionally without filler words."
        }

        return """
            You are simulating a job candidate being interviewed for a ${domain.name.lowercase()} position.
            
            Interview Question: "$question"
            
            Context: This is a ${domain.name.lowercase()} interview focusing on $domainContext.
            
            Instructions:
            - Provide a realistic, natural-sounding answer that a candidate would give in an interview
            - Keep the response between 50-100 words (about 30-45 seconds of speech)
            - $fillerInstruction
            - Be conversational and authentic
            - Show enthusiasm but also nervousness appropriate for a real interview
            - DO NOT include quotation marks or metadata, just the raw speech
            
            Generate the candidate's response:
        """.trimIndent()
    }

    /**
     * Fallback response generator when API is unavailable or not configured
     */
    private fun generateFallbackResponse(
        question: String,
        domain: InterviewDomain,
        includeFillers: Boolean
    ): String {
        val responses = when (domain) {
            InterviewDomain.TECH -> listOf(
                "Um, I have like over five years of experience in software development, you know, mainly focused on Android and Kotlin. I've worked on several production apps with millions of users.",
                "So basically, I'm passionate about clean architecture and actually enjoy solving complex technical problems. My strength is, uh, writing maintainable code.",
                "I think my experience with, um, modern Android development including Jetpack Compose and, like, MVVM architecture makes me a strong fit for this role.",
                "In my previous role, I was responsible for, uh, developing and maintaining the core features of our mobile application using Kotlin and, you know, modern Android best practices."
            )

            InterviewDomain.HR -> listOf(
                "Um, I believe that, like, good communication and teamwork are essential for success in any organization. I've always been, you know, a team player.",
                "So basically, my approach to conflict resolution is, uh, to listen actively and find common ground. I think empathy is, like, really important.",
                "I'm passionate about, um, creating a positive work culture where everyone feels, you know, valued and heard. That's been my focus in previous roles.",
                "In my experience, uh, building strong relationships with colleagues and, like, fostering open communication leads to better outcomes for everyone."
            )

            InterviewDomain.PRODUCT -> listOf(
                "Um, I believe in a data-driven approach to product development, you know, combined with user empathy. I've led several successful product launches.",
                "So my process typically involves, uh, gathering user feedback, analyzing metrics, and, like, iterating based on what we learn. It's very important to stay agile.",
                "I think prioritization is, um, one of the most critical skills for a PM. You need to, like, balance user needs with business goals, you know.",
                "In my last role, I was responsible for, uh, the product roadmap and worked closely with, like, engineering and design teams to deliver features that users loved."
            )

            InterviewDomain.DESIGN -> listOf(
                "Um, my design process always starts with, like, understanding the user's needs and pain points, you know. Research is crucial before jumping into solutions.",
                "So I'm really passionate about, uh, creating intuitive interfaces that are both beautiful and, like, functional. User experience comes first.",
                "I think good design is, um, invisible in a way. Users should be able to, you know, accomplish their goals without friction or confusion.",
                "In my previous projects, I've worked extensively with, uh, Figma and design systems to, like, maintain consistency across the product."
            )
        }

        return if (includeFillers) {
            responses.random()
        } else {
            responses.random().replace("Um, ", "").replace("um, ", "")
                .replace("uh, ", "").replace("like, ", "")
                .replace(", you know", "").replace("you know, ", "")
                .replace("So basically, ", "").replace("actually ", "")
        }
    }

    /**
     * Check if API is properly configured
     */
    fun isConfigured(): Boolean {
        return apiKey.isNotEmpty() && apiKey != "your_gemini_api_key_here"
    }
}
