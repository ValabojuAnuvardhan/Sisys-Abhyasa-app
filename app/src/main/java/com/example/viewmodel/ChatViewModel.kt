package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiApiClient
import com.example.data.FirestoreRepository
import com.example.data.MockDataSource
import com.example.model.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ChatSender {
    USER,
    SISYA_SOCRATIC,
    ABHYAS_BOT,
    MENTOR
}

data class ChatMessageItem(
    val id: String = "msg_${System.currentTimeMillis()}",
    val sender: ChatSender = ChatSender.USER,
    val senderDisplayName: String = "You",
    val content: String,
    val timestamp: String = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
    val contextTag: String? = null,
    val trustBadge: String? = null,
    val isStreaming: Boolean = false
)

data class ChatUiState(
    val messages: List<ChatMessageItem> = emptyList(),
    val isGenerating: Boolean = false,
    val isTranscribing: Boolean = false,
    val selectedModel: String = "gemini-3.5-flash",
    val activeTab: String = "Chat", // "Chat" vs "Work"
    val activeContextTag: String = "Task #12: JWT Auth",
    val isDrawerOpen: Boolean = false,
    val draftInput: String = "",
    val useGoogleSearchGrounding: Boolean = true
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val firestoreRepository = FirestoreRepository(application.applicationContext)
    private val database = com.example.data.local.AppDatabase.getDatabase(application.applicationContext)
    val projectRepository = com.example.data.local.ProjectRepository(database.projectDao(), database.photoEvidenceDao())

    val activeProjectEntity: StateFlow<com.example.data.local.ProjectEntity?> = projectRepository.activeProject
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        // Pre-populate Room if empty
        viewModelScope.launch {
            val initial = com.example.data.local.ProjectEntity(
                id = "proj_01",
                title = "Autonomous Campus Placement Engine",
                description = "A resilient microservices-based placement management platform handling 10,000+ simultaneous student applications, automated eligibility filters, and zero-trust audit verification.",
                problemStatement = "Campus placement offices struggle with manual Excel sheets, missed eligibility criteria, fraud applications, and unverified student academic claims.",
                architectureSummary = "Hexagonal architecture with Kotlin/Ktor service, PostgreSQL storage, Redis token blacklist, and GitHub Actions CI pipelines.",
                techStackCsv = "Kotlin, PostgreSQL, JWT/Auth, Docker, Redis, REST APIs, CI/CD",
                targetRole = "Backend Systems Engineer",
                ownerId = "usr_anu_01",
                ownerName = "Anuvardhan Valaboju",
                repositoryName = "sisya-abhyasa/campus-placement-engine",
                progressPercentage = 35,
                activeMilestoneTitle = "Core Placement Architecture & Secure Identity",
                activeTaskNumber = 12,
                activeTaskTitle = "JWT Authentication & Refresh Token Rotation",
                activeTaskStatus = "IN_PROGRESS",
                completedTasksCount = 1,
                totalTasksCount = 4,
                readinessScore = 78,
                lastUpdatedTimestamp = System.currentTimeMillis()
            )
            projectRepository.saveProject(initial)
        }
    }

    /**
     * Updates the project progress in the Room database directly from ChatScreen
     */
    fun updateProjectProgress(newProgress: Int, completedTasks: Int, readinessScore: Int) {
        viewModelScope.launch {
            val current = activeProjectEntity.value
            val pId = current?.id ?: "proj_01"
            projectRepository.updateProgress(
                projectId = pId,
                progress = newProgress,
                completedTasks = completedTasks,
                readinessScore = readinessScore
            )
        }
    }

    /**
     * Completes current task progress milestone in Room DB from Chat commands
     */
    fun advanceProjectTaskProgress() {
        viewModelScope.launch {
            val current = activeProjectEntity.value ?: return@launch
            projectRepository.incrementProgress(current.id, current)
        }
    }

    /**
     * Updates the active task title and number in Room DB
     */
    fun updateActiveProjectTask(taskNumber: Int, taskTitle: String, taskStatus: String) {
        viewModelScope.launch {
            val current = activeProjectEntity.value
            val pId = current?.id ?: "proj_01"
            projectRepository.updateActiveTask(
                projectId = pId,
                taskNumber = taskNumber,
                taskTitle = taskTitle,
                taskStatus = taskStatus
            )
        }
    }

    private val _chatUiState = MutableStateFlow(
        ChatUiState(
            messages = MockDataSource.initialChatMessages.map { m ->
                ChatMessageItem(
                    id = m.id,
                    sender = when (m.sender) {
                        "user" -> ChatSender.USER
                        "mentor" -> ChatSender.MENTOR
                        "abhyasbot" -> ChatSender.ABHYAS_BOT
                        else -> ChatSender.SISYA_SOCRATIC
                    },
                    senderDisplayName = when (m.sender) {
                        "user" -> "You"
                        "mentor" -> "@Mentor Review"
                        "abhyasbot" -> "AbhyāsBot"
                        else -> "Śiṣya Socratic"
                    },
                    content = m.content,
                    timestamp = m.timestamp,
                    contextTag = m.contextTag,
                    trustBadge = m.trustBadge
                )
            }
        )
    )
    val chatUiState: StateFlow<ChatUiState> = _chatUiState.asStateFlow()

    fun updateDraftInput(newText: String) {
        _chatUiState.update { it.copy(draftInput = newText) }
    }

    fun setSelectedModel(modelName: String) {
        _chatUiState.update { it.copy(selectedModel = modelName) }
    }

    fun toggleSearchGrounding() {
        _chatUiState.update { it.copy(useGoogleSearchGrounding = !it.useGoogleSearchGrounding) }
    }

    fun setActiveTab(tab: String) {
        _chatUiState.update { it.copy(activeTab = tab) }
    }

    fun toggleDrawer(isOpen: Boolean) {
        _chatUiState.update { it.copy(isDrawerOpen = isOpen) }
    }

    fun setContextTag(tag: String) {
        _chatUiState.update { it.copy(activeContextTag = tag) }
    }

    fun clearMessages() {
        _chatUiState.update { it.copy(messages = emptyList()) }
    }

    fun sendMessage(
        userText: String,
        explicitContext: String? = null,
        userContextSummary: String? = null
    ) {
        val trimmed = userText.trim()
        if (trimmed.isBlank()) return

        val currentTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        val context = explicitContext ?: _chatUiState.value.activeContextTag
        val msgId = "user_${System.currentTimeMillis()}"

        val userMessage = ChatMessageItem(
            id = msgId,
            sender = ChatSender.USER,
            senderDisplayName = "You",
            content = trimmed,
            timestamp = currentTime,
            contextTag = context
        )

        _chatUiState.update { currentState ->
            currentState.copy(
                messages = currentState.messages + userMessage,
                isGenerating = true,
                draftInput = ""
            )
        }

        viewModelScope.launch {
            // Save user message to Firestore
            try {
                firestoreRepository.saveChatMessage(
                    messageId = msgId,
                    sender = "user",
                    content = trimmed,
                    timestamp = currentTime,
                    contextTag = context,
                    trustBadge = null
                )
            } catch (e: Exception) {
                // Ignore transient network errors
            }

            // Call Gemini API with Google Search Grounding and task context
            val model = _chatUiState.value.selectedModel
            val useSearch = _chatUiState.value.useGoogleSearchGrounding

            val aiResponse = GeminiApiClient.generateChatResponse(
                prompt = trimmed,
                contextTag = context,
                useSearchGrounding = useSearch,
                modelName = model,
                userContextSummary = userContextSummary
            )

            val aiMsgId = "ai_${System.currentTimeMillis()}"
            val aiMessage = ChatMessageItem(
                id = aiMsgId,
                sender = ChatSender.SISYA_SOCRATIC,
                senderDisplayName = "Śiṣya Socratic ($model)",
                content = aiResponse,
                timestamp = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
                contextTag = context,
                trustBadge = if (useSearch) "FACT (SEARCH GROUNDED)" else "SOCRATIC GUIDANCE"
            )

            _chatUiState.update { currentState ->
                currentState.copy(
                    messages = currentState.messages + aiMessage,
                    isGenerating = false
                )
            }

            // Persist AI response to Firestore
            try {
                firestoreRepository.saveChatMessage(
                    messageId = aiMsgId,
                    sender = "sisya",
                    content = aiResponse,
                    timestamp = aiMessage.timestamp,
                    contextTag = context,
                    trustBadge = aiMessage.trustBadge
                )
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    /**
     * Transcribes microphone audio using gemini-3.5-transcribe
     */
    fun transcribeAudioInput(base64AudioData: String = "") {
        viewModelScope.launch {
            _chatUiState.update { it.copy(isTranscribing = true) }
            val transcribedText = GeminiApiClient.transcribeAudio(base64AudioData)
            _chatUiState.update {
                it.copy(
                    isTranscribing = false,
                    draftInput = transcribedText
                )
            }
        }
    }
}
