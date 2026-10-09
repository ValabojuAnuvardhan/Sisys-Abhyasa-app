package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TaskModel
import com.example.model.TaskStatus
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.AppNavScreen
import com.example.viewmodel.SisyaUiState

@Composable
fun TaskWorkspaceScreen(
    state: SisyaUiState,
    onToggleChecklist: (String) -> Unit,
    onAdvanceStatus: (TaskModel, TaskStatus) -> Unit,
    onStartDefense: (TaskModel) -> Unit,
    onAddPhotoEvidence: (
        title: String,
        caption: String,
        imageUri: String,
        localFilePath: String?,
        category: String,
        tagsCsv: String
    ) -> Unit = { _, _, _, _, _, _ -> },
    onDeletePhotoEvidence: (String) -> Unit = {},
    onNavigate: (AppNavScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val task = state.selectedTask
    var showCameraDialog by remember { mutableStateOf(false) }

    // Task-specific photo evidence entries from Room DB
    val taskPhotos = remember(state.photoEvidenceList, task.number) {
        state.photoEvidenceList.filter { it.taskNumber == task.number }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MainBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        // Top Breadcrumb & Task Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onNavigate(AppNavScreen.PROJECTS) }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryDarkTeal, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Back to Project",
                            style = MaterialTheme.typography.labelMedium,
                            color = PrimaryDarkTeal
                        )
                    }
                    StatusPill(text = "Task Version: v${task.version}", color = MutedText)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Task #${task.number}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = PrimaryDarkTeal
                    )
                    StatusPill(text = task.status.label, color = WarningAmber)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = PrimaryText
                )
            }
        }

        // Section 31 & 32: Task Objective & Description
        item {
            SisyaCard(backgroundColor = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Objective & Architectural Rationale",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryDarkTeal
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.objective,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Acceptance Criteria:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(4.dp))
                task.acceptanceCriteria.forEach { criterion ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(text = "•", color = PrimaryDarkTeal, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = criterion, style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                    }
                }
            }
        }

        // Section 33: Micro-Learning Checklist
        item {
            SisyaCard(backgroundColor = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Task Prerequisites & Concepts",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryDarkTeal
                    )
                    Text(
                        text = "${task.learningChecklist.count { it.isCompleted }}/${task.learningChecklist.size} Completed",
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryText
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                task.learningChecklist.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleChecklist(item.id) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = item.isCompleted,
                            onCheckedChange = { onToggleChecklist(item.id) },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryDarkTeal)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (item.isCompleted) FontWeight.Medium else FontWeight.Normal
                                ),
                                color = if (item.isCompleted) PrimaryDarkTeal else PrimaryText
                            )
                            Text(
                                text = "Domain: ${item.conceptTag}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedText
                            )
                        }
                    }
                }
            }
        }

        // Hardware & Prototype Photographic Evidence (Associated with Task in Room DB)
        item {
            SisyaCard(
                backgroundColor = SurfaceWhite,
                borderColor = PrimaryDarkTeal.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = PrimaryDarkTeal, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Prototype & Physical Proof",
                            style = MaterialTheme.typography.titleMedium,
                            color = PrimaryText
                        )
                    }

                    Button(
                        onClick = { showCameraDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryDarkTeal),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_task_capture_photo")
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Photo", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Capture whiteboard architecture, oscilloscope wave, circuit wiring, or physical prototype evidence for Task #${task.number}.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )

                if (taskPhotos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "${taskPhotos.size} Evidence Photos Recorded in Room DB:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryDarkTeal
                    )
                }
            }
        }

        // List task photo entries
        items(taskPhotos) { photoEntry ->
            PhotoEvidenceCardView(
                evidence = photoEntry,
                onDelete = { onDeletePhotoEvidence(photoEntry.id) }
            )
        }

        // Section 36 & 38: GitHub Engineering Workflow & AI PR Reviewer
        item {
            SisyaCard(backgroundColor = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Engineering Evidence Source",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryDarkTeal
                    )
                    TrustBadge(badgeType = "FACT")
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Git Branch & PR details
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SecondarySurface)
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CallSplit, contentDescription = null, tint = PrimaryText, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Branch: ${task.branchName}", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = PrimaryText)
                    }
                    if (task.prNumber != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MergeType, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Pull Request #${task.prNumber}: ${task.prTitle}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = PrimaryText
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "CI Status: ${task.ciStatus ?: "Passed"}", style = MaterialTheme.typography.labelSmall, color = SuccessGreen)
                            Text(text = "PR Status: ${task.prStatus ?: "Under Review"}", style = MaterialTheme.typography.labelSmall, color = InfoBlue)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                // AI PR Reviewer findings
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimarySoftTeal)
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.RateReview, contentDescription = null, tint = PrimaryDarkTeal, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI PR Reviewer Analysis (PR #${task.prNumber ?: 18})",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryDarkTeal
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Strengths: Strict HMAC-SHA256 signature validation with timing-safe comparisons.\n• Observation: Token expiration properly set to 15m; HttpOnly cookie flags verified.\n• Defense Recommendation: Explain revoked token cache invalidation in Micro-Viva.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                SisyaButton(
                    text = "Initiate Micro-Viva Defense",
                    onClick = { onStartDefense(task) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_task_initiate_viva"),
                    icon = Icons.Default.RecordVoiceOver,
                    isPrimary = true
                )
            }
        }
    }

    if (showCameraDialog) {
        val activeProj = state.roomProject
        CaptureEvidencePhotoDialog(
            projectId = activeProj?.id ?: state.project.id,
            projectTitle = activeProj?.title ?: state.project.title,
            milestoneTitle = activeProj?.activeMilestoneTitle ?: state.project.milestones.firstOrNull()?.title,
            taskNumber = task.number,
            onDismiss = { showCameraDialog = false },
            onSaveEvidence = { title, caption, uri, filePath, category, tags ->
                onAddPhotoEvidence(title, caption, uri, filePath, category, tags)
                showCameraDialog = false
            }
        )
    }
}
