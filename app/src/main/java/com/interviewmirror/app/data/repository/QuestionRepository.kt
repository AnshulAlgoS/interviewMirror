package com.interviewmirror.app.data.repository

import com.interviewmirror.app.data.model.InterviewDomain
import com.interviewmirror.app.data.model.InterviewQuestion
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for interview questions
 */
@Singleton
class QuestionRepository @Inject constructor() {

    private val questions = mapOf(
        InterviewDomain.TECH to listOf(
            InterviewQuestion(
                id = 1,
                domain = InterviewDomain.TECH,
                question = "Tell me about yourself and your experience in software development.",
                tips = "Focus on relevant technical skills and recent projects"
            ),
            InterviewQuestion(
                id = 2,
                domain = InterviewDomain.TECH,
                question = "What is your experience with Kotlin and Android development?",
                tips = "Mention specific projects, architectures, and best practices"
            ),
            InterviewQuestion(
                id = 3,
                domain = InterviewDomain.TECH,
                question = "Describe a challenging technical problem you solved recently.",
                tips = "Use the STAR method: Situation, Task, Action, Result"
            ),
            InterviewQuestion(
                id = 4,
                domain = InterviewDomain.TECH,
                question = "How do you stay updated with the latest technology trends?",
                tips = "Mention blogs, courses, conferences, and open source contributions"
            ),
            InterviewQuestion(
                id = 5,
                domain = InterviewDomain.TECH,
                question = "What are your thoughts on code quality and testing?",
                tips = "Discuss unit testing, code reviews, and clean code principles"
            )
        ),
        InterviewDomain.HR to listOf(
            InterviewQuestion(
                id = 11,
                domain = InterviewDomain.HR,
                question = "Tell me about yourself.",
                tips = "Share your background, achievements, and career goals"
            ),
            InterviewQuestion(
                id = 12,
                domain = InterviewDomain.HR,
                question = "What are your greatest strengths and weaknesses?",
                tips = "Be honest but strategic; show self-awareness"
            ),
            InterviewQuestion(
                id = 13,
                domain = InterviewDomain.HR,
                question = "Where do you see yourself in 5 years?",
                tips = "Align your goals with the company's growth"
            ),
            InterviewQuestion(
                id = 14,
                domain = InterviewDomain.HR,
                question = "Why do you want to work for our company?",
                tips = "Show you've researched the company and align with their values"
            ),
            InterviewQuestion(
                id = 15,
                domain = InterviewDomain.HR,
                question = "Describe a time when you worked in a team.",
                tips = "Highlight collaboration, communication, and problem-solving"
            )
        ),
        InterviewDomain.PRODUCT to listOf(
            InterviewQuestion(
                id = 21,
                domain = InterviewDomain.PRODUCT,
                question = "How do you prioritize features in a product roadmap?",
                tips = "Discuss frameworks like RICE, user impact, and business value"
            ),
            InterviewQuestion(
                id = 22,
                domain = InterviewDomain.PRODUCT,
                question = "Tell me about a product you recently used and liked.",
                tips = "Analyze UX, features, and what makes it successful"
            ),
            InterviewQuestion(
                id = 23,
                domain = InterviewDomain.PRODUCT,
                question = "How would you improve our product?",
                tips = "Show you understand the product and can think critically"
            ),
            InterviewQuestion(
                id = 24,
                domain = InterviewDomain.PRODUCT,
                question = "How do you handle conflicting stakeholder requirements?",
                tips = "Emphasize communication, data-driven decisions, and compromise"
            ),
            InterviewQuestion(
                id = 25,
                domain = InterviewDomain.PRODUCT,
                question = "What metrics would you use to measure product success?",
                tips = "Mention user engagement, retention, satisfaction, and business KPIs"
            )
        ),
        InterviewDomain.DESIGN to listOf(
            InterviewQuestion(
                id = 31,
                domain = InterviewDomain.DESIGN,
                question = "Walk me through your design process.",
                tips = "Cover research, ideation, prototyping, testing, and iteration"
            ),
            InterviewQuestion(
                id = 32,
                domain = InterviewDomain.DESIGN,
                question = "How do you handle design feedback and criticism?",
                tips = "Show openness to feedback and ability to iterate"
            ),
            InterviewQuestion(
                id = 33,
                domain = InterviewDomain.DESIGN,
                question = "Tell me about a design project you're most proud of.",
                tips = "Focus on problem-solving, user impact, and creative solutions"
            ),
            InterviewQuestion(
                id = 34,
                domain = InterviewDomain.DESIGN,
                question = "How do you balance aesthetics with usability?",
                tips = "Discuss user-centered design principles and testing"
            ),
            InterviewQuestion(
                id = 35,
                domain = InterviewDomain.DESIGN,
                question = "What design tools and methods do you use?",
                tips = "Mention Figma, prototyping tools, and design systems"
            )
        )
    )

    fun getQuestionsForDomain(domain: InterviewDomain): List<InterviewQuestion> {
        return questions[domain] ?: emptyList()
    }

    fun getRandomQuestion(domain: InterviewDomain): InterviewQuestion? {
        return questions[domain]?.randomOrNull()
    }
}
