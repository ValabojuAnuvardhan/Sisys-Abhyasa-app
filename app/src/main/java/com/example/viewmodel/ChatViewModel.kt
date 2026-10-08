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
