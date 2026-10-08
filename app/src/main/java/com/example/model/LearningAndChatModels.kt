package com.example.model

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
