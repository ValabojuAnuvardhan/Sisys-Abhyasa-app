package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.SisyaBottomNav
import com.example.ui.screens.*
import com.example.ui.theme.MainBackground
import com.example.ui.theme.PrimaryDarkTeal
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SisyaAbhyasaTheme
import com.example.ui.theme.SurfaceWhite
import com.example.viewmodel.AppNavScreen
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.SisyaViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: SisyaViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SisyaAbhyasaTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (uiState.currentScreen != AppNavScreen.ONBOARDING) {
                            SisyaTopAppBar(
                                currentScreen = uiState.currentScreen,
                                onNavigate = { viewModel.navigateTo(it) },
                                onOpenCollaborate = { viewModel.navigateTo(AppNavScreen.COLLABORATE) }
                            )
                        }
                    },
                    bottomBar = {
                        if (uiState.currentScreen != AppNavScreen.ONBOARDING) {
                            SisyaBottomNav(
                                currentScreen = uiState.currentScreen,
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                        }
                    },
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (uiState.currentScreen) {
                            AppNavScreen.ONBOARDING -> {
                                OnboardingScreen(
                                    onComplete = { viewModel.navigateTo(AppNavScreen.DASHBOARD) }
                                )
                            }
                            AppNavScreen.DASHBOARD -> {
                                DashboardScreen(
                                    state = uiState,
                                    onNavigate = { viewModel.navigateTo(it) },
                                    onSelectTask = { viewModel.selectTask(it) },
                                    onStartDefense = { viewModel.startVivaDefense(it) }
                                )
                            }
                            AppNavScreen.LEARN -> {
                                LearningScreen(
                                    state = uiState,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                            AppNavScreen.PROJECTS -> {
                                ProjectsScreen(
                                    state = uiState,
                                    onSelectTask = { viewModel.selectTask(it) },
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                            AppNavScreen.TASK_WORKSPACE -> {
                                TaskWorkspaceScreen(
                                    state = uiState,
                                    onToggleChecklist = { viewModel.toggleLearningChecklist(it) },
                                    onAdvanceStatus = { task, status -> viewModel.advanceTaskStatus(task, status) },
                                    onStartDefense = { viewModel.startVivaDefense(it) },
                                    onAddPhotoEvidence = { title, caption, uri, filePath, category, tags ->
                                        val projId = uiState.roomProject?.id ?: uiState.project.id
                                        viewModel.addPhotoEvidence(
                                            projectId = projId,
                                            title = title,
                                            caption = caption,
                                            imageUri = uri,
                                            localFilePath = filePath,
                                            category = category,
                                            tagsCsv = tags,
                                            milestoneTitle = uiState.roomProject?.activeMilestoneTitle ?: uiState.project.milestones.firstOrNull()?.title,
                                            taskNumber = uiState.selectedTask.number
                                        )
                                    },
                                    onDeletePhotoEvidence = { viewModel.deletePhotoEvidence(it) },
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                            AppNavScreen.MICRO_VIVA -> {
                                MicroVivaScreen(
                                    state = uiState,
                                    onAnswerChange = { viewModel.updateVivaInput(it) },
                                    onSubmitAnswer = { viewModel.submitVivaAnswer() },
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                            AppNavScreen.COLLABORATE -> {
                                CollaborateScreen(
                                    state = uiState,
                                    onAcceptJoinRequest = { viewModel.acceptJoinRequest(it) },
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                            AppNavScreen.PROVE -> {
                                EvidenceProofScreen(
                                    state = uiState,
                                    onTogglePreview = { viewModel.togglePublicProofPreview(it) },
                                    onAddPhotoEvidence = { title, caption, uri, filePath, category, tags ->
                                        val projId = uiState.roomProject?.id ?: uiState.project.id
                                        viewModel.addPhotoEvidence(
                                            projectId = projId,
                                            title = title,
                                            caption = caption,
                                            imageUri = uri,
                                            localFilePath = filePath,
                                            category = category,
                                            tagsCsv = tags,
                                            milestoneTitle = uiState.roomProject?.activeMilestoneTitle ?: uiState.project.milestones.firstOrNull()?.title,
                                            taskNumber = uiState.selectedTask.number
                                        )
                                    },
                                    onDeletePhotoEvidence = { viewModel.deletePhotoEvidence(it) },
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                            AppNavScreen.CAREER -> {
                                CareerReadinessScreen(
                                    state = uiState,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                            AppNavScreen.PROJECT_DASHBOARD -> {
                                ProjectDashboardScreen(
                                    state = uiState,
                                    onNavigate = { viewModel.navigateTo(it) },
                                    onSelectProjectTask = { viewModel.selectTask(it) }
                                )
                            }
                            AppNavScreen.CHAT -> {
                                ChatScreen(
                                    state = uiState,
                                    chatViewModel = chatViewModel,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SisyaTopAppBar(
    currentScreen: AppNavScreen,
    onNavigate: (AppNavScreen) -> Unit,
    onOpenCollaborate: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Śiṣya Abhyāsa",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryDarkTeal
                )
                Text(
                    text = " • ${currentScreen.title}",
                    style = MaterialTheme.typography.titleSmall,
                    color = PrimaryText
                )
            }
        },
        actions = {
            IconButton(
                onClick = onOpenCollaborate,
                modifier = Modifier.testTag("top_bar_btn_collaborate")
            ) {
                Icon(
                    Icons.Default.Group,
                    contentDescription = "Squad & Collaboration",
                    tint = PrimaryDarkTeal
                )
            }
            IconButton(
                onClick = { onNavigate(AppNavScreen.ONBOARDING) },
                modifier = Modifier.testTag("top_bar_btn_settings")
            ) {
                Icon(
                    Icons.Default.Tune,
                    contentDescription = "Onboarding & Preferences",
                    tint = PrimaryDarkTeal
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MainBackground,
            titleContentColor = PrimaryText
        ),
        modifier = modifier
    )
}
