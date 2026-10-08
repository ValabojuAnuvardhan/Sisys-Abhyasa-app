package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FirestoreRepository
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
    val isPublicProofPreviewOpen: Boolean = false,
    val isAuthenticated: Boolean = true
)

class SisyaViewModel(application: Application) : AndroidViewModel(application) {

    private val firestoreRepository = FirestoreRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(SisyaUiState())
    val uiState: StateFlow<SisyaUiState> = _uiState.asStateFlow()

    init {
        // Observe real-time tasks from Firestore
        viewModelScope.launch {
            firestoreRepository.observeTasks().collect { firestoreTasks ->
                if (firestoreTasks.isNotEmpty()) {
                    val updatedMilestones = _uiState.value.project.milestones.map { m ->
                        if (m.id == "m_01") m.copy(tasks = firestoreTasks) else m
                    }
                    _uiState.value = _uiState.value.copy(
                        project = _uiState.value.project.copy(milestones = updatedMilestones)
                    )
                }
            }
        }
    }

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

        viewModelScope.launch {
            try {
                firestoreRepository.updateTaskStatus(updatedTask, newStatus)
            } catch (e: Exception) {
                // Ignore transient errors
            }
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
                verifiedDate = "Oct 08, 2026"
            )

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

            viewModelScope.launch {
                try {
                    firestoreRepository.saveEvidence(newEvidence)
                    firestoreRepository.updateTaskStatus(updatedTask, TaskStatus.DONE)
                } catch (e: Exception) {
                    // Ignore
                }
            }
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

    fun declineJoinRequest(requestId: String) {
        val updated = _uiState.value.activeJoinRequests.filter { it.id != requestId }
        _uiState.value = _uiState.value.copy(activeJoinRequests = updated)
    }

    fun adoptProject(idea: IdeaRecommendation) {
        val newMilestone = MilestoneModel(
            id = "m_adopted_${System.currentTimeMillis()}",
            number = 1,
            title = "Core Architecture & Scaffolding",
            objective = idea.problem,
            status = "In Progress",
            tasks = listOf(
                TaskModel(
                    id = "task_adopted_1",
                    number = 1,
                    milestoneId = "m_adopted",
                    title = "Setup ${idea.title} Foundations",
                    objective = idea.whyItFits,
                    description = "Deconstruct ${idea.title} requirements into verifiable engineering units.",
                    acceptanceCriteria = idea.engineeringChallenges,
                    version = 1,
                    status = TaskStatus.TODO,
                    requiredSkill = idea.skillsGained.firstOrNull() ?: "Architecture",
                    learningChecklist = listOf(
                        LearningChecklistItem("lc_ad_1", "Study architecture boundaries", false, "System Design")
                    ),
                    branchName = "feat/scaffolding-core",
                    assigneeName = _uiState.value.userProfile.name
                )
            )
        )

        val updatedProj = _uiState.value.project.copy(
            title = idea.title,
            description = idea.problem,
            techStack = idea.technologies,
            milestones = listOf(newMilestone) + _uiState.value.project.milestones
        )

        _uiState.value = _uiState.value.copy(
            project = updatedProj,
            currentScreen = AppNavScreen.PROJECTS
        )
    }

    fun submitCollaborationPitch(projectTitle: String) {
        val newReq = JoinRequest(
            id = "jr_${System.currentTimeMillis()}",
            studentName = _uiState.value.userProfile.name,
            targetRole = _uiState.value.userProfile.targetRole,
            pitch = "I'd like to contribute backend concurrency patterns and automated testing pipelines to $projectTitle.",
            projectTitle = projectTitle,
            requestedDate = "Just now",
            status = "Pitch Submitted"
        )
        _uiState.value = _uiState.value.copy(
            activeJoinRequests = _uiState.value.activeJoinRequests + newReq
        )
    }

    fun togglePublicProofPreview(open: Boolean) {
        _uiState.value = _uiState.value.copy(isPublicProofPreviewOpen = open)
    }
}
