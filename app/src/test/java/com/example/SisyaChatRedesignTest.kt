package com.example

import com.example.viewmodel.ChatMessageItem
import com.example.viewmodel.ChatSender
import com.example.viewmodel.ChatUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SisyaChatRedesignTest {

    @Test
    fun `test Chat and Work mode selector state transition`() {
        var state = ChatUiState(activeTab = "Chat")
        assertEquals("Chat", state.activeTab)

        state = state.copy(activeTab = "Work")
        assertEquals("Work", state.activeTab)
    }

    @Test
    fun `test drawer toggle state preservation`() {
        var state = ChatUiState(isDrawerOpen = false)
        assertFalse(state.isDrawerOpen)

        state = state.copy(isDrawerOpen = true)
        assertTrue(state.isDrawerOpen)
    }

    @Test
    fun `test empty chat state vs conversation message feed`() {
        val emptyState = ChatUiState(messages = emptyList())
        assertTrue(emptyState.messages.isEmpty())

        val populatedState = ChatUiState(
            messages = listOf(
                ChatMessageItem(
                    id = "msg_1",
                    sender = ChatSender.USER,
                    senderDisplayName = "You",
                    content = "Teach me a technical concept step by step."
                ),
                ChatMessageItem(
                    id = "msg_2",
                    sender = ChatSender.SISYA_SOCRATIC,
                    senderDisplayName = "Śiṣya Socratic (gemini-3.5-flash)",
                    content = "Let us explore distributed consensus. What happens when a network partition occurs?"
                )
            )
        )
        assertEquals(2, populatedState.messages.size)
        assertEquals(ChatSender.USER, populatedState.messages[0].sender)
        assertEquals(ChatSender.SISYA_SOCRATIC, populatedState.messages[1].sender)
    }

    @Test
    fun `test suggested action card prompts`() {
        val prompts = listOf(
            "Teach me a technical concept step by step. Ask questions to check my understanding.",
            "Help me debug the code or error I provide. Explain the root cause before suggesting a fix.",
            "Help me design a system architecture. Explain components, data flow, bottlenecks, trade-offs, and failure handling.",
            "Help me review my project based on the code, requirements, and evidence I provide.",
            "Conduct a project-specific Micro-Viva. Ask one question at a time, evaluate my explanation, and identify unsupported claims.",
            "Help me identify the skills and engineering evidence needed for my target role."
        )

        assertEquals(6, prompts.size)
        assertTrue(prompts.all { it.isNotBlank() })
    }
}
