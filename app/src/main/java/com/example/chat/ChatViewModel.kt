package com.example.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiApiClient
import com.example.model.ChatMessage
import com.example.model.SenderType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel managing the state of chat messages using StateFlow
 * to ensure the UI updates automatically whenever messages are added.
 */
class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "init_1",
                senderType = SenderType.AI,
                content = "Welcome to Śiṣya Socratic Chat. I have full context of your profile (Anuvardhan Valaboju), your target role (Backend Systems Engineer), and active project 'Autonomous Campus Placement Engine'. What engineering decision or task can we explore today?",
                timestamp = "10:00 AM",
                contextTag = "Task #12: JWT Auth",
                trustBadge = "SOCRATIC GUIDANCE"
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    /**
     * Appends a user message and automatically schedules an AI response,
     * ensuring subscribers receive immediate UI updates via StateFlow.
     */
    fun sendMessage(content: String, contextTag: String? = "Task #12: JWT Auth") {
        val trimmedContent = content.trim()
        if (trimmedContent.isBlank()) return

        val now = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        val userMsg = ChatMessage(
            id = "user_${System.currentTimeMillis()}",
            senderType = SenderType.USER,
            content = trimmedContent,
            timestamp = now,
            contextTag = contextTag,
            trustBadge = null
        )

        // Add user message to StateFlow
        _messages.update { currentList -> currentList + userMsg }

        // Generate response asynchronously
        viewModelScope.launch {
            _isGenerating.value = true
            delay(400) // Processing delay

            val aiResponseContent = GeminiApiClient.getIntelligentAdaptiveResponse(
                query = trimmedContent,
                contextTag = contextTag ?: "Task #12: JWT Auth"
            )
            val aiMsg = ChatMessage(
                id = "ai_${System.currentTimeMillis()}",
                senderType = SenderType.AI,
                content = aiResponseContent,
                timestamp = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
                contextTag = contextTag,
                trustBadge = "SOCRATIC GUIDANCE"
            )

            _messages.update { currentList -> currentList + aiMsg }
            _isGenerating.value = false
        }
    }

    /**
     * Clears all messages in the thread.
     */
    fun clearMessages() {
        _messages.value = emptyList()
    }
}
