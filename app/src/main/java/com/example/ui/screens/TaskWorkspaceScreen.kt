package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    onNavigate: (AppNavScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val task = state.selectedTask

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
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
            }
        }

        // Acceptance Criteria (Must be strictly satisfied)
        item {
            SisyaCard(backgroundColor = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Acceptance Criteria (Immutable v${task.version})",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(8.dp))
                task.acceptanceCriteria.forEachIndexed { i, ac ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = PrimaryDarkTeal,
                            modifier = Modifier.size(16.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = ac,
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryText
                        )
                    }
                }
            }
        }

        // Section 15: Learning Checklist vs Engineering Completion
        item {
            SisyaCard(backgroundColor = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Learning Checklist",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryText
                    )
                    Text(
                        text = "Learning ≠ Task Completion",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedText
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Checking concepts demonstrates knowledge acquisition, but the task is complete only when code satisfies acceptance criteria.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
                Spacer(modifier = Modifier.height(10.dp))

                task.learningChecklist.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onToggleChecklist(item.id) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = item.isCompleted,
                            onCheckedChange = { onToggleChecklist(item.id) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = PrimaryDarkTeal,
                                uncheckedColor = BorderColor
                            )
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
}
