package com.example.model

enum class EvidenceState(val label: String, val level: Int) {
    SELF_DECLARED("Self Declared", 1),
    PRACTICED("Practiced", 2),
    OBSERVED("Observed", 3),
    DEMONSTRATED("Demonstrated", 4),
    REPEATEDLY_DEMONSTRATED("Repeatedly Demonstrated", 5)
}

enum class TaskStatus(val label: String) {
    BACKLOG("Backlog"),
    TODO("To Do"),
    IN_PROGRESS("In Progress"),
    IN_REVIEW("In Review"),
    DONE("Done")
}

enum class ProjectRole(val label: String) {
    OWNER("Project Owner"),
    CONTRIBUTOR("Contributor"),
    MENTOR("Mentor"),
    REVIEWER("Reviewer")
}

data class UserProfile(
    val id: String = "usr_anu_01",
    val name: String = "Anuvardhan Valaboju",
    val email: String = "valabojuanuvardhan@gmail.com",
    val institution: String = "JNTU Institute of Engineering",
    val branch: String = "Computer Science & Engineering",
    val year: String = "3rd Year (Class of 2027)",
    val targetRole: String = "Backend Engineer",
    val targetReadinessScore: Int = 78,
    val githubHandle: String = "anu-valaboju",
    val isGithubConnected: Boolean = true,
    val bio: String = "Focused on scalable distributed backends, REST API design, idempotency & PostgreSQL concurrency.",
    val isPublicProofPublished: Boolean = true
)

data class LearningChecklistItem(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false,
    val conceptTag: String
)

data class TaskModel(
    val id: String,
    val number: Int,
    val milestoneId: String,
    val title: String,
    val objective: String,
    val description: String,
    val acceptanceCriteria: List<String>,
    val version: Int = 1,
    val status: TaskStatus = TaskStatus.TODO,
    val requiredSkill: String,
    val learningChecklist: List<LearningChecklistItem>,
    val branchName: String,
    val prNumber: Int? = null,
    val prTitle: String? = null,
    val prStatus: String? = null, // "Merged", "Open", "Under Review"
    val ciStatus: String? = null, // "Passed", "Running", "Failed"
    val defenseStatus: String? = null, // "Pending", "Completed", "Required"
    val assigneeName: String = "Anuvardhan",
    val dueDate: String = "Oct 18, 2026"
)

data class MilestoneModel(
    val id: String,
    val number: Int,
    val title: String,
    val objective: String,
    val status: String,
    val tasks: List<TaskModel>
)

data class ProjectModel(
    val id: String,
    val title: String,
    val problemStatement: String,
    val description: String,
    val architectureSummary: String,
    val techStack: List<String>,
    val targetRole: String,
    val ownerId: String,
    val ownerName: String,
    val membersCount: Int,
    val milestones: List<MilestoneModel>,
    val repositoryName: String = "sisya-abhyasa/campus-placement-engine",
    val isPublic: Boolean = true
)

data class SkillEvidenceItem(
    val id: String,
    val skillName: String,
    val state: EvidenceState,
    val projectName: String,
    val taskNumber: Int,
    val taskTitle: String,
    val prNumber: Int,
    val prSha: String,
    val ciStatus: String,
    val defenseTranscriptSnippet: String,
    val verifiedDate: String
)

data class MicroVivaTurn(
    val question: String,
    val studentAnswer: String? = null,
    val followUp: String? = null,
    val isAiEvaluated: Boolean = false,
    val feedback: String? = null
)

data class DefenseSession(
    val taskId: String,
    val taskTitle: String,
    val skillName: String,
    val turns: List<MicroVivaTurn>,
    val isCompleted: Boolean = false,
    val outcomeState: EvidenceState = EvidenceState.DEMONSTRATED,
    val transcriptSummary: String = ""
)

data class JobReadinessMetric(
    val roleName: String,
    val score: Int,
    val strengths: List<String>,
    val gaps: List<String>,
    val nextAction: String
)

data class JoinRequest(
    val id: String,
    val studentName: String,
    val targetRole: String,
    val pitch: String,
    val projectTitle: String,
    val requestedDate: String,
    val status: String = "Pending"
)
