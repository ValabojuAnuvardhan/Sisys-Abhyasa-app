package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MockDataSource
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppNavScreen(val title: String) {
    ONBOARDING("Onboarding"),
    DASHBOARD("Dashboard"),
    LEARN("Learning"),
    PROJECTS("Projects"),
    TASK_WORKSPACE("Task Workspace"),
    COLLABORATE("Collaborate"),
    PROVE("Evidence & Proof"),
    MICRO_VIVA("Micro-Viva Defense"),
    CAREER("Career Readiness"),
    CHAT("ŚiṣyaChat")
}

data class SisyaUiState(
    val currentScreen: AppNavScreen = AppNavScreen.DASHBOARD,
    val userProfile: UserProfile = MockDataSource.sampleUser,
    val project: ProjectModel = MockDataSource.sampleProject,
    val selectedTask: TaskModel = MockDataSource.sampleTasks[0],
    val evidenceList: List<SkillEvidenceItem> = MockDataSource.sampleEvidenceItems,
    val backendReadiness: JobReadinessMetric = MockDataSource.sampleBackendReadiness,
    val courses: List<CourseModel> = MockDataSource.sampleCourses,
    val practiceProblems: List<PracticeProblem> = MockDataSource.samplePracticeProblems,
    val ideaRecommendations: List<IdeaRecommendation> = MockDataSource.sampleIdeaRecommendations,
    val chatMessages: List<ChatMessage> = MockDataSource.initialChatMessages,
    val activeVivaTurns: List<MicroVivaTurn> = MockDataSource.sampleDefenseQuestions,
    val currentVivaIndex: Int = 0,
    val isVivaCompleted: Boolean = false,
    val vivaAnswerInput: String = "",
    val activeJoinRequests: List<JoinRequest> = listOf(
        JoinRequest(
            id = "jr_1",
            studentName = "Rahul Sharma",
            targetRole = "DevOps Engineer",
            pitch = "I've written GitHub Actions pipelines with matrix tests and Docker image scanning. I'd like to own Milestone 3 CI/CD automation.",
            projectTitle = "Autonomous Campus Placement Engine",
            requestedDate = "Yesterday"
        )
    ),
    val aiArchitectInputIdea: String = "",
    val isGeneratingArchitectPlan: Boolean = false,
    val isPublicProofPreviewOpen: Boolean = false
)

class SisyaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SisyaUiState())
    val uiState: StateFlow<SisyaUiState> = _uiState.asStateFlow()

    fun navigateTo(screen: AppNavScreen) {
        _uiState.value = _uiState.value.copy(currentScreen = screen)
    }

    fun selectTask(task: TaskModel) {
        _uiState.value = _uiState.value.copy(
            selectedTask = task,
            currentScreen = AppNavScreen.TASK_WORKSPACE
        )
    }

    fun toggleLearningChecklist(itemId: String) {
        val currentTask = _uiState.value.selectedTask
        val updatedChecklist = currentTask.learningChecklist.map { item ->
            if (item.id == itemId) item.copy(isCompleted = !item.isCompleted) else item
        }
        val updatedTask = currentTask.copy(learningChecklist = updatedChecklist)
        
        // Update both in selectedTask and in the project's milestones
        val updatedMilestones = _uiState.value.project.milestones.map { milestone ->
            val updatedTasks = milestone.tasks.map { if (it.id == updatedTask.id) updatedTask else it }
            milestone.copy(tasks = updatedTasks)
        }

        _uiState.value = _uiState.value.copy(
            selectedTask = updatedTask,
            project = _uiState.value.project.copy(milestones = updatedMilestones)
        )
    }

    fun advanceTaskStatus(task: TaskModel, newStatus: TaskStatus) {
        val updatedTask = task.copy(status = newStatus)
        val updatedMilestones = _uiState.value.project.milestones.map { milestone ->
            val updatedTasks = milestone.tasks.map { if (it.id == updatedTask.id) updatedTask else it }
            milestone.copy(tasks = updatedTasks)
        }
        _uiState.value = _uiState.value.copy(
            selectedTask = updatedTask,
            project = _uiState.value.project.copy(milestones = updatedMilestones)
        )
    }

    fun sendChatMessage(userText: String, contextTag: String? = "Task #12") {
        if (userText.isBlank()) return

        val userMsg = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            sender = "user",
            content = userText.trim(),
            timestamp = "Just now",
            contextTag = contextTag
        )

        val currentList = _uiState.value.chatMessages + userMsg
        _uiState.value = _uiState.value.copy(chatMessages = currentList)

        // Socratic AI response generation
        viewModelScope.launch {
            val responseText = generateSocraticResponse(userText)
            val aiMsg = ChatMessage(
                id = "ai_${System.currentTimeMillis()}",
                sender = "sisya",
                content = responseText,
                timestamp = "Just now",
                contextTag = contextTag,
                trustBadge = "SOCRATIC GUIDANCE"
            )
            _uiState.value = _uiState.value.copy(chatMessages = _uiState.value.chatMessages + aiMsg)
        }
    }

    private fun generateSocraticResponse(query: String): String {
        val q = query.lowercase()
        return when {
            q.contains("token") || q.contains("jwt") || q.contains("auth") ->
                "Good question on authentication. If we store the refresh token in memory or localStorage, what is our defense against XSS token exfiltration? What happens if you inspect how RFC 6749 defines HttpOnly and SameSite cookie policies?"
            q.contains("test") || q.contains("mock") || q.contains("ci") ->
                "When validating your acceptance criteria for token rejection, how do you verify both the clock skew leeway AND the blacklisted token revocation without relying on a slow live database in your CI runner?"
            q.contains("db") || q.contains("postgres") || q.contains("sql") ->
                "Think about database isolation levels. If two students submit applications for the same last remaining seat at the exact same millisecond, what PostgreSQL concurrency lock prevents double-assignment?"
            else ->
                "Let's trace this back to your acceptance criteria for Task #12. What is the fundamental invariant your service must guarantee before and after this operation?"
        }
    }

    fun updateVivaInput(text: String) {
        _uiState.value = _uiState.value.copy(vivaAnswerInput = text)
    }

    fun submitVivaAnswer() {
        val input = _uiState.value.vivaAnswerInput
        if (input.isBlank()) return

        val currentIndex = _uiState.value.currentVivaIndex
        val turns = _uiState.value.activeVivaTurns.toMutableList()
        val currentTurn = turns[currentIndex]

        turns[currentIndex] = currentTurn.copy(
            studentAnswer = input.trim(),
            isAiEvaluated = true,
            feedback = "Defense Accepted. Student demonstrates clear understanding of cryptographic trade-offs, revocation mechanics, and explicit acceptance criteria satisfaction."
        )

        val nextIndex = currentIndex + 1
        val isDone = nextIndex >= turns.size

        if (isDone) {
            // Upgrade evidence state for Authentication
            val newEvidence = SkillEvidenceItem(
                id = "ev_${System.currentTimeMillis()}",
                skillName = _uiState.value.selectedTask.requiredSkill,
                state = EvidenceState.REPEATEDLY_DEMONSTRATED,
                projectName = _uiState.value.project.title,
                taskNumber = _uiState.value.selectedTask.number,
                taskTitle = _uiState.value.selectedTask.title,
                prNumber = _uiState.value.selectedTask.prNumber ?: 18,
                prSha = "9c4b2a8f",
                ciStatus = "Passed",
                defenseTranscriptSnippet = "Q: Token revocation trade-off? A: $input",
                verifiedDate = "Oct 06, 2026"
            )
            
            // Advance task to DONE and elevate readiness score
            val updatedTask = _uiState.value.selectedTask.copy(
                status = TaskStatus.DONE,
                defenseStatus = "Completed"
            )
            val updatedMilestones = _uiState.value.project.milestones.map { m ->
                m.copy(tasks = m.tasks.map { if (it.id == updatedTask.id) updatedTask else it })
            }

            val updatedReadiness = _uiState.value.backendReadiness.copy(
                score = 84,
                strengths = listOf("Authentication & JWT (Repeatedly Demonstrated)", "REST API Architecture (Demonstrated)", "Relational Schema Design (Observed)"),
                nextAction = "Ready to proceed to Task #13: PostgreSQL Schema Migration & Indexing."
            )

            _uiState.value = _uiState.value.copy(
                activeVivaTurns = turns,
                isVivaCompleted = true,
                vivaAnswerInput = "",
                evidenceList = listOf(newEvidence) + _uiState.value.evidenceList.filter { it.skillName != newEvidence.skillName },
                selectedTask = updatedTask,
                project = _uiState.value.project.copy(milestones = updatedMilestones),
                backendReadiness = updatedReadiness
            )
        } else {
            _uiState.value = _uiState.value.copy(
                activeVivaTurns = turns,
                currentVivaIndex = nextIndex,
                vivaAnswerInput = ""
            )
        }
    }

    fun startVivaDefense(task: TaskModel) {
        _uiState.value = _uiState.value.copy(
            selectedTask = task,
            currentVivaIndex = 0,
            isVivaCompleted = false,
            vivaAnswerInput = "",
            currentScreen = AppNavScreen.MICRO_VIVA
        )
    }

    fun acceptJoinRequest(requestId: String) {
        val updated = _uiState.value.activeJoinRequests.map {
            if (it.id == requestId) it.copy(status = "Accepted as Contributor") else it
        }
        _uiState.value = _uiState.value.copy(activeJoinRequests = updated)
    }

    fun togglePublicProofPreview(open: Boolean) {
        _uiState.value = _uiState.value.copy(isPublicProofPreviewOpen = open)
    }
}
