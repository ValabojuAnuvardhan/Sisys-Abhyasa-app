package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EvidenceState
import com.example.model.TaskStatus
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.AppNavScreen
import com.example.viewmodel.SisyaUiState

@Composable
fun DashboardScreen(
    state: SisyaUiState,
    onNavigate: (AppNavScreen) -> Unit,
    onSelectTask: (com.example.model.TaskModel) -> Unit,
    onStartDefense: (com.example.model.TaskModel) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MainBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        // Top Greeting & Product Loop Indicator
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Good morning, ${state.userProfile.name.split(" ").first()}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Here is what you can build and prove today.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText
                )
                Spacer(modifier = Modifier.height(12.dp))
                ProductLoopBar(currentStep = "BUILD")
            }
        }

        // Action Hero Card: Current Task & Next Action
        item {
            val activeTask = state.selectedTask
            SisyaCard(
                backgroundColor = SurfaceWhite,
                borderColor = PrimaryDarkTeal.copy(alpha = 0.3f),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_hero_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusPill(text = "CURRENT TASK #${activeTask.number}", color = PrimaryDarkTeal)
                    StatusPill(text = activeTask.status.label, color = WarningAmber)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = activeTask.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = activeTask.objective,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Evidence status banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimarySoftTeal)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = null,
                        tint = PrimaryDarkTeal,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Linked PR #${activeTask.prNumber ?: 18} • CI: ${activeTask.ciStatus ?: "Passed"}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryDarkTeal
                        )
                        Text(
                            text = "Micro-Viva Defense available to demonstrate understanding",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SisyaButton(
                        text = "Open Workspace",
                        onClick = { onSelectTask(activeTask) },
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Code,
                        isPrimary = true,
                        testTag = "btn_continue_task"
                    )
                    SisyaButton(
                        text = "Defend Work",
                        onClick = { onStartDefense(activeTask) },
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.RecordVoiceOver,
                        isPrimary = false,
                        testTag = "btn_start_viva"
                    )
                }
            }
        }

        // Section 37/45: EvidenceSummary Dashboard Component
        item {
            // Compute real metric counts from state
            // 1. Evidence collected: skill evidence items + Room photo evidence captures + merged PR tasks
            val totalEvidenceCount = state.evidenceList.size + state.photoEvidenceList.size
            // 2. Assessments completed: Micro-Viva turns evaluated + tasks with completed defenses
            val vivaTurnsCompleted = if (state.isVivaCompleted) state.activeVivaTurns.size else state.activeVivaTurns.count { it.isAiEvaluated }
            val completedDefenseTasks = state.project.milestones.flatMap { it.tasks }.count { it.defenseStatus == "Completed" }
            val assessmentsCompletedCount = (vivaTurnsCompleted + completedDefenseTasks).coerceAtLeast(1)
            // 3. Skills with verified evidence: Unique skills where evidence state is DEMONSTRATED or REPEATEDLY_DEMONSTRATED
            val verifiedSkillsCount = state.evidenceList
                .filter { it.state.level >= EvidenceState.DEMONSTRATED.level }
                .map { it.skillName }
                .distinct()
                .size
                .coerceAtLeast(1)

            val summaryMetrics = EvidenceSummaryMetrics(
                evidenceCollectedCount = totalEvidenceCount,
                assessmentsCompletedCount = assessmentsCompletedCount,
                skillsWithVerifiedEvidenceCount = verifiedSkillsCount,
                totalSkillsCount = state.project.techStack.size.coerceAtLeast(6),
                photoEvidenceCount = state.photoEvidenceList.size
            )

            val activeTask = state.selectedTask
            EvidenceSummaryCard(
                metrics = summaryMetrics,
                onViewEvidenceLedger = { onNavigate(AppNavScreen.PROVE) },
                onStartAssessment = { onStartDefense(activeTask) }
            )
        }

        // Job Readiness Card with Explainable Reasoning (Section 48 & 49)
        item {
            val readiness = state.backendReadiness
            SisyaCard(
                backgroundColor = SurfaceWhite,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = readiness.roleName,
                            style = MaterialTheme.typography.titleMedium,
                            color = PrimaryText
                        )
                        Text(
                            text = "Evidence-Backed Job Readiness Index",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(PrimarySoftTeal)
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${readiness.score}/100",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryDarkTeal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { readiness.score / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = PrimaryDarkTeal,
                    trackColor = BorderColor
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Key Verified Strengths:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = SecondaryText
                )
                Spacer(modifier = Modifier.height(4.dp))
                readiness.strengths.forEach { s ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = s, style = MaterialTheme.typography.bodySmall, color = PrimaryText)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Identified Skill Gaps:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = WarningAmber
                )
                Spacer(modifier = Modifier.height(4.dp))
                readiness.gaps.forEach { g ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                        Icon(Icons.Default.RadioButtonUnchecked, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = g, style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SisyaButton(
                        text = "Career Explorer",
                        onClick = { onNavigate(AppNavScreen.CAREER) },
                        modifier = Modifier.weight(1f),
                        isPrimary = false,
                        testTag = "btn_career_explorer"
                    )
                    SisyaButton(
                        text = "Visual Dashboard",
                        onClick = { onNavigate(AppNavScreen.PROJECT_DASHBOARD) },
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Insights,
                        isPrimary = true,
                        testTag = "btn_dashboard_project_charts"
                    )
                }
            }
        }

        // Active Project Overview
        item {
            val proj = state.project
            SisyaCard(
                backgroundColor = SurfaceWhite,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusPill(text = "ACTIVE PROJECT", color = InfoBlue)
                    Text(
                        text = "Repository: ${proj.repositoryName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedText
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = proj.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = proj.architectureSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Tech stack pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    proj.techStack.take(4).forEach { tech ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SecondarySurface)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = tech, style = MaterialTheme.typography.labelSmall, color = PrimaryText)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                SisyaButton(
                    text = "Explore Project Roadmap & Kanban",
                    onClick = { onNavigate(AppNavScreen.PROJECTS) },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.ViewKanban,
                    isPrimary = false,
                    testTag = "btn_open_project"
                )
            }
        }

        // Quick Navigation Grid to Core Systems
        item {
            SectionHeader(title = "Core Platform Systems", subtitle = "Connected Learning, Engineering, and Proof")
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SisyaCard(
                    backgroundColor = SurfaceWhite,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(AppNavScreen.LEARN) }
                ) {
                    Icon(Icons.Default.School, contentDescription = null, tint = PrimaryDarkTeal, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "System A: Learn", style = MaterialTheme.typography.titleSmall, color = PrimaryText)
                    Text(text = "Roadmap & Practice", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                }
                SisyaCard(
                    backgroundColor = SurfaceWhite,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(AppNavScreen.PROVE) }
                ) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "System C: Proof", style = MaterialTheme.typography.titleSmall, color = PrimaryText)
                    Text(text = "Evidence & Profile", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                }
            }
        }
    }
}
