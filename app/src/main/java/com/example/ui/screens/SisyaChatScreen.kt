package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.TrustBadge
import com.example.ui.theme.*
import com.example.viewmodel.AppNavScreen
import com.example.viewmodel.ChatMessageItem
import com.example.viewmodel.ChatSender
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.SisyaUiState

// Design tokens matching reference screenshot exactly
private val SidebarDarkBg = Color(0xFF071328)
private val SidebarDarkSurface = Color(0xFF0F1E38)
private val SidebarDarkSurfaceActive = Color(0xFF1B2B4A)
private val SidebarBorder = Color(0xFF162540)
private val SidebarTextPrimary = Color(0xFFF1F5F9)
private val SidebarTextSecondary = Color(0xFF94A3B8)
private val SidebarTextMuted = Color(0xFF64748B)

private val CanvasBackground = Color(0xFFFAFBFC)
private val CanvasSurfaceWhite = Color(0xFFFFFFFF)
private val CanvasBorder = Color(0xFFE5E7EB)
private val CanvasTextPrimary = Color(0xFF0F172A)
private val CanvasTextSecondary = Color(0xFF64748B)
private val AccentBrandBlue = Color(0xFF4F46E5) // Indigo/Violet AI accent

/**
 * Suggested action card configuration matching screenshot
 */
data class SuggestedActionCard(
    val id: String,
    val title: String,
    val description: String,
    val prompt: String,
    val icon: ImageVector,
    val iconTint: Color,
    val iconBg: Color,
    val cardBg: Color
)

@Composable
fun SisyaChatScreen(
    state: SisyaUiState,
    chatViewModel: ChatViewModel,
    onNavigate: ((AppNavScreen) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val chatState by chatViewModel.chatUiState.collectAsStateWithLifecycle()
    val activeProjectEntity by chatViewModel.activeProjectEntity.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    var searchQuery by remember { mutableStateOf("") }
    var pinnedExpanded by remember { mutableStateOf(true) }
    var todayExpanded by remember { mutableStateOf(true) }
    var thisWeekExpanded by remember { mutableStateOf(true) }
    var thisMonthExpanded by remember { mutableStateOf(true) }
    var showProjectDropdown by remember { mutableStateOf(false) }

    // Scroll to latest message
    LaunchedEffect(chatState.messages.size) {
        if (chatState.messages.isNotEmpty()) {
            listState.animateScrollToItem(chatState.messages.size - 1)
        }
    }

    val liveUserSummary = remember(state, activeProjectEntity) {
        val projTitle = activeProjectEntity?.title ?: state.project.title
        val progressPct = activeProjectEntity?.progressPercentage ?: 35
        val completedCount = activeProjectEntity?.completedTasksCount ?: 1
        val totalCount = activeProjectEntity?.totalTasksCount ?: 4
        val activeTaskNum = activeProjectEntity?.activeTaskNumber ?: state.selectedTask.number
        val activeTaskName = activeProjectEntity?.activeTaskTitle ?: state.selectedTask.title
        val readScore = activeProjectEntity?.readinessScore ?: state.backendReadiness.score
        val photoEvidenceCount = state.photoEvidenceList.size

        "User: ${state.userProfile.name} (${state.userProfile.targetRole}, ${state.userProfile.institution}). " +
        "Project: '$projTitle' ($progressPct% done, $completedCount/$totalCount tasks). " +
        "Task #$activeTaskNum: $activeTaskName (Status: ${state.selectedTask.status.label}). " +
        "Readiness: $readScore/100. Photo Evidence Entries: $photoEvidenceCount. Mode: ${chatState.activeTab}."
    }

    // Reference Suggested Action Cards
    val suggestedActions = remember {
        listOf(
            SuggestedActionCard(
                id = "learn_concept",
                title = "Learn a concept",
                description = "Get clear explanations with examples",
                prompt = "Teach me a technical concept step by step. Ask questions to check my understanding.",
                icon = Icons.Outlined.School,
                iconTint = Color(0xFF059669),
                iconBg = Color(0xFFD1FAE5),
                cardBg = Color(0xFFECFDF5)
            ),
            SuggestedActionCard(
                id = "debug_code",
                title = "Debug my code",
                description = "Find and fix issues in your code",
                prompt = "Help me debug the code or error I provide. Explain the root cause before suggesting a fix.",
                icon = Icons.Outlined.Code,
                iconTint = Color(0xFF2563EB),
                iconBg = Color(0xFFDBEAFE),
                cardBg = Color(0xFFEFF6FF)
            ),
            SuggestedActionCard(
                id = "explain_architecture",
                title = "Explain an architecture",
                description = "Discuss system design and trade-offs",
                prompt = "Help me design a system architecture. Explain components, data flow, bottlenecks, trade-offs, and failure handling.",
                icon = Icons.Outlined.AccountTree,
                iconTint = Color(0xFF7C3AED),
                iconBg = Color(0xFFEDE9FE),
                cardBg = Color(0xFFF5F3FF)
            ),
            SuggestedActionCard(
                id = "review_project",
                title = "Review my project",
                description = "Get feedback on your implementation",
                prompt = "Help me review my project based on the code, requirements, and evidence I provide.",
                icon = Icons.Outlined.Folder,
                iconTint = Color(0xFFD97706),
                iconBg = Color(0xFFFEF3C7),
                cardBg = Color(0xFFFFFBEB)
            ),
            SuggestedActionCard(
                id = "prepare_viva",
                title = "Prepare for Micro-Viva",
                description = "Practice project defense with real questions",
                prompt = "Conduct a project-specific Micro-Viva. Ask one question at a time, evaluate my explanation, and identify unsupported claims.",
                icon = Icons.Outlined.GraphicEq,
                iconTint = Color(0xFFDC2626),
                iconBg = Color(0xFFFEE2E2),
                cardBg = Color(0xFFFEF2F2)
            ),
            SuggestedActionCard(
                id = "career_guidance",
                title = "Career guidance",
                description = "Explore roles, skills and next steps",
                prompt = "Help me identify the skills and engineering evidence needed for my target role.",
                icon = Icons.Outlined.TrackChanges,
                iconTint = Color(0xFF0284C7),
                iconBg = Color(0xFFE0F2FE),
                cardBg = Color(0xFFF0F9FF)
            )
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
    ) {
        val isWideScreen = maxWidth >= 840.dp
        val showSidebarInline = isWideScreen && chatState.isDrawerOpen

        Row(modifier = Modifier.fillMaxSize()) {
            // Persistent / Inline Sidebar for Tablet/Desktop
            if (showSidebarInline) {
                ChatSidebarContent(
                    state = state,
                    activeProjectTitle = activeProjectEntity?.title ?: state.project.title,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    pinnedExpanded = pinnedExpanded,
                    onTogglePinned = { pinnedExpanded = !pinnedExpanded },
                    todayExpanded = todayExpanded,
                    onToggleToday = { todayExpanded = !todayExpanded },
                    thisWeekExpanded = thisWeekExpanded,
                    onToggleThisWeek = { thisWeekExpanded = !thisWeekExpanded },
                    thisMonthExpanded = thisMonthExpanded,
                    onToggleThisMonth = { thisMonthExpanded = !thisMonthExpanded },
                    onNewChat = {
                        chatViewModel.clearMessages()
                    },
                    onSelectConversation = { convTitle ->
                        chatViewModel.sendMessage(
                            userText = "Resume conversation: $convTitle",
                            userContextSummary = liveUserSummary
                        )
                    },
                    onCollapseSidebar = { chatViewModel.toggleDrawer(false) },
                    modifier = Modifier.width(280.dp)
                )
            }

            // Main Canvas Workspace
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(CanvasBackground)
            ) {
                // Top Workspace Bar matching reference
                ChatWorkspaceTopBar(
                    state = state,
                    chatState = chatState,
                    isSidebarOpen = chatState.isDrawerOpen,
                    activeProjectTitle = activeProjectEntity?.title ?: state.project.title,
                    showProjectDropdown = showProjectDropdown,
                    onToggleProjectDropdown = { showProjectDropdown = it },
                    onToggleSidebar = { chatViewModel.toggleDrawer(!chatState.isDrawerOpen) },
                    onSelectMode = { mode -> chatViewModel.setActiveTab(mode) },
                    onNavigate = onNavigate
                )

                HorizontalDivider(color = CanvasBorder, thickness = 1.dp)

                // Central Chat Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    if (chatState.messages.isEmpty()) {
                        // Empty Chat Welcome View
                        EmptyChatWelcomeView(
                            suggestedActions = suggestedActions,
                            onActionSelected = { action ->
                                chatViewModel.sendMessage(
                                    userText = action.prompt,
                                    userContextSummary = liveUserSummary
                                )
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp)
                        )
                    } else {
                        // Conversation Message Feed
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(top = 20.dp, bottom = 20.dp)
                        ) {
                            items(chatState.messages, key = { it.id }) { msg ->
                                ModernMessageRow(
                                    message = msg,
                                    onExploreAction = { screen -> onNavigate?.invoke(screen) }
                                )
                            }

                            if (chatState.isGenerating) {
                                item {
                                    ModernGeneratingIndicator(selectedModel = chatState.selectedModel)
                                }
                            }
                        }
                    }
                }

                // Bottom Rounded Message Composer
                ModernMessageComposer(
                    draftText = chatState.draftInput,
                    isGenerating = chatState.isGenerating,
                    isTranscribing = chatState.isTranscribing,
                    onDraftChange = { chatViewModel.updateDraftInput(it) },
                    onSend = { text ->
                        chatViewModel.sendMessage(
                            userText = text,
                            userContextSummary = liveUserSummary
                        )
                    },
                    onTranscribeVoice = {
                        chatViewModel.transcribeAudioInput()
                    },
                    onAttachCodeSnippet = {
                        val template = "```kotlin\n// Code snippet\nfun handleEvent() {\n    // TODO\n}\n```\n"
                        chatViewModel.updateDraftInput(chatState.draftInput + if (chatState.draftInput.isNotBlank()) "\n" + template else template)
                    },
                    onAttachContext = {
                        val taskContext = "Context: Task #${state.selectedTask.number}: ${state.selectedTask.title}\n"
                        chatViewModel.updateDraftInput(taskContext + chatState.draftInput)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (isWideScreen) 48.dp else 16.dp)
                        .padding(bottom = 12.dp)
                        .imePadding()
                )

                // Sub-footer: Security & AI Integrity Note
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = CanvasTextSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Powered by Gemini • Focused on real learning, real engineering, real proof.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = CanvasTextSecondary
                    )
                }
            }
        }

        // Overlay Drawer for Mobile / Compact Screens
        if (!isWideScreen && chatState.isDrawerOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { chatViewModel.toggleDrawer(false) }
            ) {
                ChatSidebarContent(
                    state = state,
                    activeProjectTitle = activeProjectEntity?.title ?: state.project.title,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    pinnedExpanded = pinnedExpanded,
                    onTogglePinned = { pinnedExpanded = !pinnedExpanded },
                    todayExpanded = todayExpanded,
                    onToggleToday = { todayExpanded = !todayExpanded },
                    thisWeekExpanded = thisWeekExpanded,
                    onToggleThisWeek = { thisWeekExpanded = !thisWeekExpanded },
                    thisMonthExpanded = thisMonthExpanded,
                    onToggleThisMonth = { thisMonthExpanded = !thisMonthExpanded },
                    onNewChat = {
                        chatViewModel.clearMessages()
                        chatViewModel.toggleDrawer(false)
                    },
                    onSelectConversation = { convTitle ->
                        chatViewModel.toggleDrawer(false)
                        chatViewModel.sendMessage(
                            userText = "Resume conversation: $convTitle",
                            userContextSummary = liveUserSummary
                        )
                    },
                    onCollapseSidebar = { chatViewModel.toggleDrawer(false) },
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(300.dp)
                        .clickable(enabled = false) {}
                )
            }
        }
    }
}

/**
 * Dark Sidebar component strictly matching reference screenshot
 */
@Composable
private fun ChatSidebarContent(
    state: SisyaUiState,
    activeProjectTitle: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    pinnedExpanded: Boolean,
    onTogglePinned: () -> Unit,
    todayExpanded: Boolean,
    onToggleToday: () -> Unit,
    thisWeekExpanded: Boolean,
    onToggleThisWeek: () -> Unit,
    thisMonthExpanded: Boolean,
    onToggleThisMonth: () -> Unit,
    onNewChat: () -> Unit,
    onSelectConversation: (String) -> Unit,
    onCollapseSidebar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SidebarDarkBg,
        modifier = modifier.fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            // Brand Header with Collapse Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Logo Icon with subtle graduation book star motif
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "ŚiṣyaChat Logo",
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "ŚiṣyaChat",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SidebarTextPrimary
                            )
                        )
                        Text(
                            text = "by Śiṣya Abhyāsa",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = SidebarTextMuted
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onCollapseSidebar,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Collapse Sidebar",
                        tint = SidebarTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // "+ New Chat" Button with Ctrl N shortcut badge
            Surface(
                color = SidebarDarkSurface,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SidebarBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNewChat)
                    .testTag("btn_sidebar_new_chat")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = SidebarTextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "New Chat",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = SidebarTextPrimary
                            )
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SidebarDarkSurfaceActive)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Ctrl  N",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = SidebarTextMuted
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search conversations input
            Surface(
                color = SidebarDarkSurface,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SidebarBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = SidebarTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodySmall.copy(color = SidebarTextPrimary),
                        cursorBrush = SolidColor(SidebarTextPrimary),
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search conversations...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SidebarTextMuted
                                )
                            }
                            innerTextField()
                        }
                    )
                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = SidebarTextMuted,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { onSearchQueryChange("") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scrollable Conversation Groups
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // PINNED SECTION
                item {
                    SidebarGroupHeader(
                        title = "Pinned",
                        icon = Icons.Default.PushPin,
                        isExpanded = pinnedExpanded,
                        onToggle = onTogglePinned
                    )
                }
                if (pinnedExpanded) {
                    val pinnedItems = listOf(
                        "Backend System Design",
                        "Micro-Viva Preparation",
                        "My Final Year Project"
                    ).filter { it.contains(searchQuery, ignoreCase = true) }

                    items(pinnedItems) { title ->
                        SidebarConversationRow(
                            title = title,
                            onClick = { onSelectConversation(title) }
                        )
                    }
                }

                // TODAY SECTION
                item {
                    SidebarGroupHeader(
                        title = "Today",
                        icon = Icons.Outlined.CalendarToday,
                        isExpanded = todayExpanded,
                        onToggle = onToggleToday
                    )
                }
                if (todayExpanded) {
                    val todayItems = listOf(
                        "Explain Kubernetes",
                        "Debug my Python code",
                        "Design database schema"
                    ).filter { it.contains(searchQuery, ignoreCase = true) }

                    items(todayItems) { title ->
                        SidebarConversationRow(
                            title = title,
                            onClick = { onSelectConversation(title) }
                        )
                    }
                }

                // THIS WEEK SECTION
                item {
                    SidebarGroupHeader(
                        title = "This Week",
                        icon = Icons.Outlined.DateRange,
                        isExpanded = thisWeekExpanded,
                        onToggle = onToggleThisWeek
                    )
                }
                if (thisWeekExpanded) {
                    val thisWeekItems = listOf(
                        "System architecture review",
                        "FastAPI deployment",
                        "Data pipeline project"
                    ).filter { it.contains(searchQuery, ignoreCase = true) }

                    items(thisWeekItems) { title ->
                        SidebarConversationRow(
                            title = title,
                            onClick = { onSelectConversation(title) }
                        )
                    }
                }

                // THIS MONTH SECTION
                item {
                    SidebarGroupHeader(
                        title = "This Month",
                        icon = Icons.Outlined.Folder,
                        isExpanded = thisMonthExpanded,
                        onToggle = onToggleThisMonth
                    )
                }
                if (thisMonthExpanded) {
                    val thisMonthItems = listOf(
                        "ML model evaluation",
                        "Career guidance",
                        "Mock interview questions"
                    ).filter { it.contains(searchQuery, ignoreCase = true) }

                    items(thisMonthItems) { title ->
                        SidebarConversationRow(
                            title = title,
                            onClick = { onSelectConversation(title) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = SidebarBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Sidebar Footer: Settings & Real User Profile
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { /* Settings action */ }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    tint = SidebarTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.bodyMedium.copy(color = SidebarTextPrimary)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // User Profile Row (uses actual student data: Anuvardhan Valaboju)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { /* Profile dialog */ }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar bubble with initials
                    val initials = state.userProfile.name
                        .split(" ")
                        .take(2)
                        .mapNotNull { it.firstOrNull()?.toString() }
                        .joinToString("")
                        .ifEmpty { "AV" }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = state.userProfile.name,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = SidebarTextPrimary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Student • ${state.userProfile.targetRole}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = SidebarTextMuted
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = SidebarTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SidebarGroupHeader(
    title: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onToggle)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                contentDescription = null,
                tint = SidebarTextMuted,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = SidebarTextMuted
                )
            )
        }
        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = SidebarTextMuted,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun SidebarConversationRow(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Outlined.ChatBubbleOutline,
            contentDescription = null,
            tint = SidebarTextSecondary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(color = SidebarTextSecondary),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Top Workspace Bar matching reference:
 * [Sidebar Toggle] [Chat / Work Selector] [Current Project Dropdown] [Bell with badge] [Menu]
 */
@Composable
private fun ChatWorkspaceTopBar(
    state: SisyaUiState,
    chatState: com.example.viewmodel.ChatUiState,
    isSidebarOpen: Boolean,
    activeProjectTitle: String,
    showProjectDropdown: Boolean,
    onToggleProjectDropdown: (Boolean) -> Unit,
    onToggleSidebar: () -> Unit,
    onSelectMode: (String) -> Unit,
    onNavigate: ((AppNavScreen) -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CanvasBackground)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Sidebar Toggle
        IconButton(
            onClick = onToggleSidebar,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = if (isSidebarOpen) "Collapse Sidebar" else "Open Sidebar",
                tint = CanvasTextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        // Center: Mode Selector [Chat | Work]
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF1F5F9))
                .padding(3.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val isChat = chatState.activeTab == "Chat"
                val isWork = chatState.activeTab == "Work"

                // Chat Pill
                Surface(
                    color = if (isChat) CanvasSurfaceWhite else Color.Transparent,
                    shape = RoundedCornerShape(18.dp),
                    shadowElevation = if (isChat) 1.dp else 0.dp,
                    modifier = Modifier.clickable { onSelectMode("Chat") }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = null,
                            tint = if (isChat) CanvasTextPrimary else CanvasTextSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Chat",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isChat) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isChat) CanvasTextPrimary else CanvasTextSecondary
                            )
                        )
                    }
                }

                // Work Pill
                Surface(
                    color = if (isWork) CanvasSurfaceWhite else Color.Transparent,
                    shape = RoundedCornerShape(18.dp),
                    shadowElevation = if (isWork) 1.dp else 0.dp,
                    modifier = Modifier.clickable { onSelectMode("Work") }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.GridView,
                            contentDescription = null,
                            tint = if (isWork) CanvasTextPrimary else CanvasTextSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Work",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isWork) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isWork) CanvasTextPrimary else CanvasTextSecondary
                            )
                        )
                    }
                }
            }
        }

        // Right side: Project Context Dropdown + Notifications Bell with badge + Options Menu
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Project Selector Pill
            Box {
                Surface(
                    color = CanvasSurfaceWhite,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CanvasBorder),
                    modifier = Modifier
                        .clickable { onToggleProjectDropdown(true) }
                        .testTag("btn_select_project_context")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Folder,
                            contentDescription = null,
                            tint = CanvasTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Current Project",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = CanvasTextSecondary
                                )
                            )
                            Text(
                                text = activeProjectTitle.split(" ").take(3).joinToString(" "),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = CanvasTextPrimary
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = CanvasTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = showProjectDropdown,
                    onDismissRequest = { onToggleProjectDropdown(false) }
                ) {
                    DropdownMenuItem(
                        text = { Text("Autonomous Campus Placement Engine") },
                        onClick = { onToggleProjectDropdown(false) }
                    )
                    DropdownMenuItem(
                        text = { Text("Open Projects Workspace (Kanban)") },
                        leadingIcon = { Icon(Icons.Default.ViewKanban, contentDescription = null) },
                        onClick = {
                            onToggleProjectDropdown(false)
                            onNavigate?.invoke(AppNavScreen.PROJECTS)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("View Visual Dashboard") },
                        leadingIcon = { Icon(Icons.Default.Insights, contentDescription = null) },
                        onClick = {
                            onToggleProjectDropdown(false)
                            onNavigate?.invoke(AppNavScreen.PROJECT_DASHBOARD)
                        }
                    )
                }
            }

            // Notification Bell with Red Badge
            Box(contentAlignment = Alignment.TopEnd) {
                IconButton(
                    onClick = { /* Notifications */ },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = "Alerts",
                        tint = CanvasTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp, end = 6.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444))
                )
            }

            // More Options
            IconButton(
                onClick = { /* Context menu */ },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = CanvasTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Centered Empty Chat Welcome State with 6 Action Cards
 */
@Composable
private fun EmptyChatWelcomeView(
    suggestedActions: List<SuggestedActionCard>,
    onActionSelected: (SuggestedActionCard) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {
        item {
            // Central Book & Star Motif Icon matching reference
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFEDE9FE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = AccentBrandBlue,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Main Heading
            Text(
                text = "What are we learning or building today?",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = CanvasTextPrimary
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Supporting Text
            Text(
                text = "Learn concepts, debug code, explore architecture, or prepare\nto defend your engineering work.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = CanvasTextSecondary,
                    lineHeight = 22.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 6 Suggestion Action Cards Grid (arranged as 2-column or 3x2 rows)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 840.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Chunk into pairs of 2 for grid layout
                suggestedActions.chunked(2).forEach { rowPair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowPair.forEach { card ->
                            SuggestedActionCardItem(
                                card = card,
                                onClick = { onActionSelected(card) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Divider: "Or start a conversation..."
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = CanvasBorder)
                Text(
                    text = "  Or start a conversation...  ",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = CanvasTextSecondary
                    )
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = CanvasBorder)
            }
        }
    }
}

@Composable
private fun SuggestedActionCardItem(
    card: SuggestedActionCard,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = card.cardBg,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CanvasBorder.copy(alpha = 0.6f)),
        modifier = modifier
            .heightIn(min = 90.dp)
            .clickable(onClick = onClick)
            .testTag("card_suggested_${card.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(card.iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = card.icon,
                    contentDescription = null,
                    tint = card.iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = card.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = CanvasTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = card.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = CanvasTextSecondary,
                        fontSize = 11.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Modern Rounded Message Composer strictly matching screenshot:
 * Large white card with subtle border, multiline text, bottom action buttons
 * [ + ] [ Attach ] [ </> Code ] [ Image ]  ...  [ Mic ] [ ↑ Send ]
 */
@Composable
private fun ModernMessageComposer(
    draftText: String,
    isGenerating: Boolean,
    isTranscribing: Boolean,
    onDraftChange: (String) -> Unit,
    onSend: (String) -> Unit,
    onTranscribeVoice: () -> Unit,
    onAttachCodeSnippet: () -> Unit,
    onAttachContext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CanvasSurfaceWhite,
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CanvasBorder),
        shadowElevation = 2.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Multiline text input
            BasicTextField(
                value = draftText,
                onValueChange = onDraftChange,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    color = CanvasTextPrimary,
                    lineHeight = 22.sp
                ),
                cursorBrush = SolidColor(AccentBrandBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 40.dp, max = 120.dp)
                    .testTag("chat_input_field"),
                decorationBox = { innerTextField ->
                    if (draftText.isEmpty()) {
                        Text(
                            text = "Ask ŚiṣyaChat anything...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 15.sp,
                                color = CanvasTextSecondary
                            )
                        )
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom control row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left pills: [+] [Attach] [Code] [Image]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // [+] Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9))
                            .clickable { onAttachContext() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add context",
                            tint = CanvasTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Attach Pill
                    ComposerPillButton(
                        text = "Attach",
                        icon = Icons.Outlined.AttachFile,
                        onClick = { onAttachContext() }
                    )

                    // Code Pill
                    ComposerPillButton(
                        text = "Code",
                        icon = Icons.Outlined.Code,
                        onClick = { onAttachCodeSnippet() }
                    )

                    // Image Pill
                    ComposerPillButton(
                        text = "Image",
                        icon = Icons.Outlined.Image,
                        onClick = { onAttachContext() }
                    )
                }

                // Right controls: [Mic] [↑ Send]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Voice Mic
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9))
                            .clickable(onClick = onTranscribeVoice),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isTranscribing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = AccentBrandBlue
                            )
                        } else {
                            Icon(
                                Icons.Default.Mic,
                                contentDescription = "Voice Input",
                                tint = CanvasTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Send Button with Up Arrow
                    val canSend = draftText.isNotBlank() && !isGenerating
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (canSend) AccentBrandBlue else Color(0xFFC7CDD3))
                            .clickable(enabled = canSend) {
                                if (canSend) onSend(draftText)
                            }
                            .testTag("btn_send_chat"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ArrowUpward,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ComposerPillButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CanvasBorder),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = CanvasTextSecondary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = CanvasTextSecondary
                )
            )
        }
    }
}

/**
 * Message Row rendering user & assistant messages cleanly
 */
@Composable
private fun ModernMessageRow(
    message: ChatMessageItem,
    onExploreAction: ((AppNavScreen) -> Unit)?
) {
    val isUser = message.sender == ChatSender.USER

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chat_msg_${message.id}"),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(AccentBrandBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = message.senderDisplayName,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isUser) CanvasTextSecondary else AccentBrandBlue
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message.timestamp,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = CanvasTextSecondary
            )

            if (message.trustBadge != null) {
                Spacer(modifier = Modifier.width(8.dp))
                TrustBadge(badgeType = message.trustBadge)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Surface(
            color = if (isUser) Color(0xFFF1F5F9) else CanvasSurfaceWhite,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isUser) Color(0xFFE2E8F0) else CanvasBorder
            ),
            shadowElevation = if (isUser) 0.dp else 1.dp,
            modifier = Modifier.widthIn(max = 680.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = CanvasTextPrimary
                    )
                )

                // Contextual exploration pills when assistant suggests engineering workflows
                if (!isUser && (message.content.contains("viva", ignoreCase = true) ||
                                message.content.contains("task", ignoreCase = true) ||
                                message.content.contains("readiness", ignoreCase = true) ||
                                message.content.contains("proof", ignoreCase = true))) {
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = CanvasBorder.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (message.content.contains("viva", ignoreCase = true)) {
                            MessageActionPill("Prepare Micro-Viva") { onExploreAction?.invoke(AppNavScreen.MICRO_VIVA) }
                        }
                        if (message.content.contains("task", ignoreCase = true)) {
                            MessageActionPill("Task Workspace") { onExploreAction?.invoke(AppNavScreen.TASK_WORKSPACE) }
                        }
                        if (message.content.contains("readiness", ignoreCase = true)) {
                            MessageActionPill("Career Readiness") { onExploreAction?.invoke(AppNavScreen.CAREER) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageActionPill(label: String, onClick: () -> Unit) {
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CanvasBorder),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                color = AccentBrandBlue,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun ModernGeneratingIndicator(selectedModel: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 4.dp, top = 6.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            strokeWidth = 2.dp,
            color = AccentBrandBlue
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "ŚiṣyaChat is formulating response with $selectedModel...",
            style = MaterialTheme.typography.bodySmall.copy(color = CanvasTextSecondary)
        )
    }
}
