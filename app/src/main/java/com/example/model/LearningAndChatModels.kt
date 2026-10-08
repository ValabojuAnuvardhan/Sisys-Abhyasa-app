package com.example.model

data class ChatMessage(
    val id: String,
    val sender: String, // "user", "sisya", "mentor", "abhyasbot"
    val content: String,
    val timestamp: String,
    val contextTag: String? = null, // e.g. "Task #12", "PR #18", "Evidence"
    val trustBadge: String? = null // "FACT", "AI INTERPRETATION", "SOCRATIC GUIDANCE"
)

data class IdeaRecommendation(
    val id: String,
    val title: String,
    val problem: String,
    val whyItFits: String,
    val difficulty: String,
    val duration: String,
    val technologies: List<String>,
    val skillsGained: List<String>,
    val engineeringChallenges: List<String>,
    val evidenceOpportunities: List<String>
)

data class CourseModel(
    val id: String,
    val title: String,
    val level: String,
    val duration: String,
    val progressPercent: Int,
    val skills: List<String>,
    val currentLessonTitle: String,
    val connectedTask: String
)

data class PracticeProblem(
    val id: String,
    val title: String,
    val type: String, // "Architecture", "Debugging", "Code Review", "Scenario"
    val difficulty: String,
    val objective: String,
    val question: String,
    val initialCodeOrScenario: String,
    val expectedOutcome: String,
    val hints: List<String>
)
