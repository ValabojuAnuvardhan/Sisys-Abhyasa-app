package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.ChatSender
import com.example.viewmodel.ChatMessageItem
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.SisyaUiState

@Composable
fun SisyaChatScreen(
    state: SisyaUiState,
    chatViewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val chatState by chatViewModel.chatUiState.collectAsStateWithLifecycle()
    var showModelMenu by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Automatically scroll to latest message when messages change
    LaunchedEffect(chatState.messages.size) {
        if (chatState.messages.isNotEmpty()) {
            listState.animateScrollToItem(chatState.messages.size - 1)
        }
    }

    val chatBg = Color(0xFFFBFBFC)
    val sidebarBg = Color(0xFFF9F9FA)
    val pillActiveBg = Color(0xFFFFFFFF)
    val pillInactiveBg = Color(0xFFF0F1F3)

    val liveUserSummary = remember(state) {
        "User: ${state.userProfile.name} (${state.userProfile.targetRole}, ${state.userProfile.institution}, GPA 8.7). " +
        "Project: ${state.project.title}. " +
        "Task: Task #${state.selectedTask.number}: ${state.selectedTask.title} (Status: ${state.selectedTask.status.label}, Branch: ${state.selectedTask.branchName}, PR #${state.selectedTask.prNumber ?: 18}). " +
        "Job Readiness Score: ${state.backendReadiness.score}/100. Current screen: ${state.currentScreen.title}."
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(chatBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar matching reference: Model dropdown, bell, search, drawer toggle, new chat
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(chatBg)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { chatViewModel.toggleDrawer(!chatState.isDrawerOpen) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Toggle Sidebar",
                            tint = PrimaryText,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Model Selector Dropdown
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { showModelMenu = true }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = chatState.selectedModel,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = PrimaryText
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = "Choose Model",
                                tint = SecondaryText,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showModelMenu,
                            onDismissRequest = { showModelMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Śiṣya Socratic 3.5 (Default • Socratic Guidance)") },
                                onClick = {
                                    chatViewModel.setSelectedModel("Śiṣya Socratic 3.5")
                                    showModelMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("AbhyāsBot (Task & PR Grounded)") },
                                onClick = {
                                    chatViewModel.setSelectedModel("AbhyāsBot")
                                    showModelMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Contextual @Mentor (Deep PR Review)") },
                                onClick = {
                                    chatViewModel.setSelectedModel("Contextual @Mentor")
                                    showModelMenu = false
                                }
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { /* Notifications */ },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Alerts", tint = SecondaryText, modifier = Modifier.size(20.dp))
                    }
                    IconButton(
                        onClick = { /* Search chats */ },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Outlined.Search, contentDescription = "Search", tint = SecondaryText, modifier = Modifier.size(20.dp))
                    }
                    IconButton(
                        onClick = {
                            chatViewModel.clearMessages()
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.AddBox, contentDescription = "New Chat", tint = SecondaryText, modifier = Modifier.size(20.dp))
                    }
                }
            }

            // Top Segmented Pill ("Chat" | "Work")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(pillInactiveBg)
                        .padding(3.dp)
                ) {
                    Row {
                        val isChat = chatState.activeTab == "Chat"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isChat) pillActiveBg else Color.Transparent)
                                .clickable { chatViewModel.setActiveTab("Chat") }
                                .padding(horizontal = 24.dp, vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Chat",
                                fontSize = 13.sp,
                                fontWeight = if (isChat) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isChat) PrimaryText else SecondaryText
                            )
                        }

                        val isWork = chatState.activeTab == "Work"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isWork) pillActiveBg else Color.Transparent)
                                .clickable { chatViewModel.setActiveTab("Work") }
                                .padding(horizontal = 24.dp, vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Work",
                                fontSize = 13.sp,
                                fontWeight = if (isWork) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isWork) PrimaryText else SecondaryText
                            )
                        }
                    }
                }
            }

            // Messages List or Empty State ("Ready when you are.")
            if (chatState.messages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ready when you are.",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Normal,
                            fontSize = 32.sp
                        ),
                        color = PrimaryText
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
                ) {
                    items(chatState.messages, key = { it.id }) { message ->
                        CleanMessageBubble(message = message)
                    }

                    if (chatState.isGenerating) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = PrimaryDarkTeal
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Śiṣya is formulating Socratic guidance...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SecondaryText
                                )
                            }
                        }
                    }
                }
            }

            // Centralized Floating Input Box & Quick Action Pills
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .imePadding()
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = SurfaceWhite,
                    shape = RoundedCornerShape(28.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E4E8)),
                    tonalElevation = 2.dp,
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 54.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { /* Add files / repo context */ },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add context",
                                tint = SecondaryText,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        TextField(
                            value = chatState.draftInput,
                            onValueChange = { chatViewModel.updateDraftInput(it) },
                            placeholder = {
                                Text(
                                    text = "Ask ${chatState.selectedModel}...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MutedText
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_input_field"),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = PrimaryDarkTeal
                            ),
                            maxLines = 4
                        )

                        // Model tag pill
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF1F3F5))
                                .clickable { showModelMenu = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Socratic Sol",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = SecondaryText
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = SecondaryText,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = { chatViewModel.transcribeAudioInput() },
                            modifier = Modifier.size(34.dp)
                        ) {
                            if (chatState.isTranscribing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = PrimaryDarkTeal
                                )
                            } else {
                                Icon(
                                    Icons.Default.Mic,
                                    contentDescription = "Transcribe Audio (gemini-3.5-transcribe)",
                                    tint = PrimaryDarkTeal,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Up-arrow Send Button
                        val hasText = chatState.draftInput.isNotBlank()
                        IconButton(
                            onClick = {
                                if (hasText) {
                                    chatViewModel.sendMessage(
                                        userText = chatState.draftInput,
                                        userContextSummary = liveUserSummary
                                    )
                                }
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (hasText) PrimaryDarkTeal else Color(0xFFC7CDD3))
                                .testTag("btn_send_chat")
                        ) {
                            Icon(
                                Icons.Default.ArrowUpward,
                                contentDescription = "Send",
                                tint = SurfaceWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Suggestion prompts helping with user data, features, and flow
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                chatViewModel.sendMessage(
                                    userText = "What is my profile, readiness score, and current project tasks?",
                                    userContextSummary = liveUserSummary
                                )
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Outlined.Person, contentDescription = null, tint = SecondaryText, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "My profile & data", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                chatViewModel.sendMessage(
                                    userText = "What should I do next in the 9-stage engineering flow?",
                                    userContextSummary = liveUserSummary
                                )
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Outlined.AltRoute, contentDescription = null, tint = SecondaryText, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Next flow step", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                chatViewModel.sendMessage(
                                    userText = "Explain all components and features of Śiṣya Abhyāsa",
                                    userContextSummary = liveUserSummary
                                )
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Outlined.Dashboard, contentDescription = null, tint = SecondaryText, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "App features", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                    }
                }
            }
        }

        // Sliding Navigation Sidebar
        AnimatedVisibility(
            visible = chatState.isDrawerOpen,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f))
                    .clickable { chatViewModel.toggleDrawer(false) }
            ) {
                Surface(
                    color = sidebarBg,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(300.dp)
                        .clickable(enabled = false) {},
                    tonalElevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryDarkTeal),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.School, contentDescription = null, tint = SurfaceWhite, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Śiṣya Workspace",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = PrimaryText
                                )
                            }
                            IconButton(onClick = { chatViewModel.toggleDrawer(false) }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = SecondaryText)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    chatViewModel.toggleDrawer(false)
                                    chatViewModel.clearMessages()
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Outlined.Edit, contentDescription = null, tint = PrimaryText, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "New chat", style = MaterialTheme.typography.titleSmall, color = PrimaryText)
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Color(0xFFE6E8EC))
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Pinned",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MutedText
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        SidebarHistoryItem(title = "Continue internship startup", icon = Icons.Outlined.ChatBubbleOutline) {
                            chatViewModel.toggleDrawer(false)
                            chatViewModel.sendMessage("Resume work on internship startup backend")
                        }
                        SidebarHistoryItem(title = "Startup Idea for Internships", icon = Icons.Outlined.ChatBubbleOutline) {
                            chatViewModel.toggleDrawer(false)
                            chatViewModel.sendMessage("Review startup idea for engineering internships")
                        }

                        SidebarFolderItem(
                            folderName = "internship",
                            items = listOf(
                                "Write Antigravity Prompt",
                                "Learning And Building",
                                "Debug Error Root Cause",
                                "Create internship task DOCX",
                                "Continue Claude Conversation"
                            )
                        ) { selectedTitle ->
                            chatViewModel.toggleDrawer(false)
                            chatViewModel.sendMessage("Open folder context: $selectedTitle")
                        }

                        SidebarSimpleFolder(name = "python")
                        SidebarSimpleFolder(name = "Intern")
                        SidebarSimpleFolder(name = "Os")

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Projects",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MutedText
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        SidebarFolderItem(
                            folderName = "sisya abhyasa core",
                            items = listOf(
                                "Identify required tasks",
                                "Auth & JWT Rotation PR",
                                "PostgreSQL Migrations",
                                "Micro-Viva Defense Transcripts"
                            )
                        ) { projectItem ->
                            chatViewModel.toggleDrawer(false)
                            chatViewModel.sendMessage("Switch project context to $projectItem")
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE5989B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "VA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = state.userProfile.name, style = MaterialTheme.typography.titleSmall, color = PrimaryText)
                                Text(text = state.userProfile.targetRole, style = MaterialTheme.typography.labelSmall, color = SecondaryText)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CleanMessageBubble(message: ChatMessageItem) {
    val isUser = message.sender == ChatSender.USER

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message.senderDisplayName,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isUser) SecondaryText else PrimaryDarkTeal
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = message.timestamp, style = MaterialTheme.typography.labelSmall, color = MutedText)
            if (message.trustBadge != null) {
                Spacer(modifier = Modifier.width(6.dp))
                TrustBadge(badgeType = message.trustBadge)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .widthIn(max = 330.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(if (isUser) Color(0xFFF0F2F5) else SurfaceWhite)
                .border(
                    width = 1.dp,
                    color = if (isUser) Color(0xFFE4E7EB) else BorderColor,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(14.dp)
        ) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                color = PrimaryText
            )
        }
    }
}

@Composable
fun SidebarHistoryItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 6.dp)
    ) {
        Icon(icon, contentDescription = null, tint = SecondaryText, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = PrimaryText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SidebarFolderItem(
    folderName: String,
    items: List<String>,
    onItemClick: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { expanded = !expanded }
                .padding(horizontal = 6.dp, vertical = 6.dp)
        ) {
            Icon(Icons.Outlined.Folder, contentDescription = null, tint = SecondaryText, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = folderName,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = PrimaryText
            )
        }

        if (expanded) {
            Column(modifier = Modifier.padding(start = 24.dp)) {
                items.forEach { subItem ->
                    Text(
                        text = subItem,
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onItemClick(subItem) }
                            .padding(vertical = 4.dp, horizontal = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SidebarSimpleFolder(name: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Icon(Icons.Outlined.Folder, contentDescription = null, tint = SecondaryText, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = name, style = MaterialTheme.typography.bodySmall, color = PrimaryText)
    }
}
