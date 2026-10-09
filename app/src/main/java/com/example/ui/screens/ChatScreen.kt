package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.viewmodel.AppNavScreen
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.SisyaUiState

/**
 * ChatScreen delegates directly to the redesigned [SisyaChatScreen] to display
 * the modern ChatGPT-inspired workspace layout with persistent dark sidebar,
 * centered empty state with suggested action cards, Chat/Work mode selector,
 * and rounded multiline composer while retaining full Room DB and Gemini API integration.
 */
@Composable
fun ChatScreen(
    state: SisyaUiState,
    chatViewModel: ChatViewModel,
    onNavigate: ((AppNavScreen) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    SisyaChatScreen(
        state = state,
        chatViewModel = chatViewModel,
        onNavigate = onNavigate,
        modifier = modifier
    )
}
