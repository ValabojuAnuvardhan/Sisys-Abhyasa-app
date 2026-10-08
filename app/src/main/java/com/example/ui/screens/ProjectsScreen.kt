package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.IdeaRecommendation
import com.example.model.MilestoneModel
import com.example.model.TaskModel
import com.example.model.TaskStatus
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.AppNavScreen
import com.example.viewmodel.SisyaUiState

@Composable
fun ProjectsScreen(
    state: SisyaUiState,
    onSelectTask: (TaskModel) -> Unit,
    onAdoptProject: (IdeaRecommendation) -> Unit = {},
    onNavigate: (AppNavScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(0) } // 0: Milestones & Roadmap, 1: Kanban Board, 2: AI Idea Finder & Architect
    val tabs = listOf("Milestones", "Kanban Board", "Idea Finder")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MainBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        // Section Header & Project Summary
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "System B — Engineering",
                            style = MaterialTheme.typography.headlineLarge,
                            color = PrimaryText
                        )
                        Text(
                            text = "Project → Milestone → Task → Branch → PR → CI → Merge",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))

                // Tab Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SecondarySurface)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    tabs.forEachIndexed { idx, title ->
                        val isSelected = activeTab == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) SurfaceWhite else Color.Transparent)
                                .clickable { activeTab = idx }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) PrimaryDarkTeal else SecondaryText
                            )
                        }
                    }
                }
            }
        }

        when (activeTab) {
            0 -> {
                // Section 28 & 29: Project Overview & Milestones
                item {
                    val proj = state.project
                    SisyaCard(backgroundColor = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusPill(text = "Hexagonal Architecture", color = PrimaryDarkTeal)
                            Text(text = "3 Team Contributors", style = MaterialTheme.typography.labelSmall, color = SecondaryText)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = proj.title, style = MaterialTheme.typography.titleLarge, color = PrimaryText)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = proj.description, style = MaterialTheme.typography.bodyMedium, color = SecondaryText)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimarySoftTeal)
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Code, contentDescription = null, tint = PrimaryDarkTeal, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "GitHub: ${proj.repositoryName} (Main Branch Protected)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = PrimaryDarkTeal
                            )
                        }
                    }
                }

                item {
                    SectionHeader(
                        title = "Project Milestones",
                        subtitle = "Execution roadmap with task acceptance criteria"
                    )
                }

                items(state.project.milestones) { milestone ->
                    MilestoneCardView(milestone = milestone, onSelectTask = onSelectTask)
                }
            }

            1 -> {
                // Section 30: Kanban Board
                item {
                    SectionHeader(
                        title = "Interactive Kanban",
                        subtitle = "Traceable progression connected to pull requests"
                    )
                }

                val allTasks = state.project.milestones.flatMap { it.tasks }
                val statuses = listOf(TaskStatus.TODO, TaskStatus.IN_PROGRESS, TaskStatus.IN_REVIEW, TaskStatus.DONE)

                items(statuses) { status ->
                    val tasksForStatus = allTasks.filter { it.status == status }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SecondarySurface)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = status.label.uppercase(),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryText
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SurfaceWhite)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${tasksForStatus.size}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = PrimaryDarkTeal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        if (tasksForStatus.isEmpty()) {
                            Text(
                                text = "No tasks currently in ${status.label}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedText,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            tasksForStatus.forEach { task ->
                                SisyaCard(
                                    backgroundColor = SurfaceWhite,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    onClick = { onSelectTask(task) }
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "Task #${task.number}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryDarkTeal)
                                        Text(text = task.dueDate, style = MaterialTheme.typography.labelSmall, color = MutedText)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = task.title, style = MaterialTheme.typography.titleSmall, color = PrimaryText)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        StatusPill(text = task.requiredSkill, color = SecondaryText)
                                        if (task.prNumber != null) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.MergeType, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(text = "PR #${task.prNumber}", style = MaterialTheme.typography.labelSmall, color = SuccessGreen)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Section 25 & 27: AI Idea Finder & Project Architect
                item {
                    SectionHeader(
                        title = "AI Project Architect & Idea Finder",
                        subtitle = "Grounded recommendations based on your target role"
                    )
                }

                item {
                    SisyaCard(backgroundColor = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Target Role: ${state.userProfile.targetRole}",
                            style = MaterialTheme.typography.titleMedium,
                            color = PrimaryDarkTeal
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "AI recommendations optimize for capability demonstrated through real software, not simple tutorial clones.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText
                        )
                    }
                }

                items(state.ideaRecommendations) { idea ->
                    IdeaRecommendationCardView(idea = idea, onAdopt = { onAdoptProject(idea) })
                }
            }
        }
    }
}

@Composable
fun MilestoneCardView(
    milestone: MilestoneModel,
    onSelectTask: (TaskModel) -> Unit
) {
    SisyaCard(backgroundColor = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusPill(text = "MILESTONE ${milestone.number}", color = PrimaryDarkTeal)
            StatusPill(text = milestone.status, color = if (milestone.status == "In Progress") WarningAmber else SecondaryText)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = milestone.title, style = MaterialTheme.typography.titleMedium, color = PrimaryText)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = milestone.objective, style = MaterialTheme.typography.bodySmall, color = SecondaryText)

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Tasks in Milestone (${milestone.tasks.size})",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = PrimaryText
        )
        Spacer(modifier = Modifier.height(6.dp))

        if (milestone.tasks.isEmpty()) {
            Text(text = "Milestone planning in progress.", style = MaterialTheme.typography.bodySmall, color = MutedText)
        } else {
            milestone.tasks.forEach { task ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SecondarySurface)
                        .clickable { onSelectTask(task) }
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Task #${task.number}: ${task.title}",
                            style = MaterialTheme.typography.titleSmall,
                            color = PrimaryText
                        )
                        Text(
                            text = "Skill: ${task.requiredSkill} • Status: ${task.status.label}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = PrimaryDarkTeal)
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
fun IdeaRecommendationCardView(
    idea: IdeaRecommendation,
    onAdopt: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }

    SisyaCard(backgroundColor = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusPill(text = idea.difficulty, color = WarningAmber)
            Text(text = idea.duration, style = MaterialTheme.typography.labelSmall, color = MutedText)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = idea.title, style = MaterialTheme.typography.titleMedium, color = PrimaryText)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = idea.problem, style = MaterialTheme.typography.bodySmall, color = SecondaryText)

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(PrimarySoftTeal)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryDarkTeal, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = idea.whyItFits, style = MaterialTheme.typography.bodySmall, color = PrimaryDarkTeal)
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Engineering Challenges:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryText)
            idea.engineeringChallenges.forEach { ch ->
                Text(text = "• $ch", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Attributable Evidence Opportunities:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryText)
            idea.evidenceOpportunities.forEach { ev ->
                Text(text = "• $ev", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SisyaButton(
                text = if (isExpanded) "Show Less" else "Deep Architecture",
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier.weight(1f),
                isPrimary = false
            )
            SisyaButton(
                text = "Adopt as Project",
                onClick = onAdopt,
                modifier = Modifier.weight(1f),
                icon = Icons.Default.AddCircle,
                isPrimary = true
            )
        }
    }
}
