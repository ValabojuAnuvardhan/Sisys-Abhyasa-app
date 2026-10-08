package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.model.CourseModel
import com.example.model.PracticeProblem
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.AppNavScreen
import com.example.viewmodel.SisyaUiState

@Composable
fun LearningScreen(
    state: SisyaUiState,
    onNavigate: (AppNavScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Roadmap & Courses, 1: Practice Problems, 2: Skill Tracks
    val tabs = listOf("Roadmap & Courses", "Practice Sandbox", "Skill Tracks")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MainBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        item {
            Column {
                Text(
                    text = "System A — Learning Engine",
                    style = MaterialTheme.typography.headlineLarge,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Skill → Concept → Practice → Assessment → Engineering Task",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Tab Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SecondarySurface)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) SurfaceWhite else Color.Transparent)
                                .clickable { selectedTab = index }
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

        when (selectedTab) {
            0 -> {
                // Section 16 & 19: Learning Roadmap & Connected Courses
                item {
                    SectionHeader(
                        title = "Visual Learning Journey",
                        subtitle = "Curriculum grounded directly in engineering tasks"
                    )
                }

                // Vertical Journey Stepper
                item {
                    SisyaCard(
                        backgroundColor = SurfaceWhite,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val stages = listOf(
                            Triple("1. Foundations & Protocols", "HTTP/2, Cryptographic Signing, Stateless Tokens", true),
                            Triple("2. Backend API Architecture", "Idempotency, REST RFC 7807, Auth Middlewares", true),
                            Triple("3. Relational Persistence", "PostgreSQL MVCC, Concurrency Locks, 3NF Tradeoffs", false),
                            Triple("4. Asynchronous Queues", "BullMQ/Kafka, Consumer Groups, Dead Letter Queues", false),
                            Triple("5. Production Systems", "CI/CD Pipeline Security, Docker Containers, Observability", false)
                        )

                        stages.forEachIndexed { i, (stageTitle, desc, isCompleted) ->
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(if (isCompleted) SuccessGreen else PrimarySoftTeal),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isCompleted) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = SurfaceWhite, modifier = Modifier.size(14.dp))
                                        } else {
                                            Text(text = "${i+1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkTeal)
                                        }
                                    }
                                    if (i < stages.size - 1) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(38.dp)
                                                .background(if (isCompleted) SuccessGreen.copy(alpha = 0.5f) else BorderColor)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = stageTitle, style = MaterialTheme.typography.titleSmall, color = PrimaryText)
                                    Text(text = desc, style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                                    Spacer(modifier = Modifier.height(14.dp))
                                }
                            }
                        }
                    }
                }

                item {
                    SectionHeader(
                        title = "Connected Engineering Courses",
                        subtitle = "Every module is tied to an active task"
                    )
                }

                items(state.courses) { course ->
                    CourseCardView(course = course, onOpenTask = { onNavigate(AppNavScreen.TASK_WORKSPACE) })
                }
            }

            1 -> {
                // Section 20 & 23: Practice Sandbox
                item {
                    SectionHeader(
                        title = "Interactive Practice Sandbox",
                        subtitle = "Deep-dive scenario review, architecture & bug hunting"
                    )
                }

                items(state.practiceProblems) { problem ->
                    PracticeProblemCardView(problem = problem)
                }
            }

            2 -> {
                // Section 18: Skill Tracks
                item {
                    SectionHeader(
                        title = "Mastery Skill Tracks",
                        subtitle = "Traceable progression across primary engineering domains"
                    )
                }

                val skillTracks = listOf(
                    Triple("Authentication & Cryptography", "HMAC-SHA256, Refresh Rotation, Salted Hashing", "Repeatedly Demonstrated"),
                    Triple("PostgreSQL & Data Engineering", "MVCC, Index Tuning, ACID Transactions", "Observed"),
                    Triple("Distributed Message Queues", "Kafka, Worker Idempotency, Dead Letter Handling", "Practiced"),
                    Triple("CI/CD & Production Pipelines", "GitHub Actions, Automated Linters, Container Scans", "Practiced")
                )

                items(skillTracks) { (name, details, currentLevel) ->
                    SisyaCard(
                        backgroundColor = SurfaceWhite,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = name, style = MaterialTheme.typography.titleMedium, color = PrimaryText)
                            StatusPill(text = currentLevel, color = PrimaryDarkTeal)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = details, style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Evidence source: Autonomous Campus Placement Engine",
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedText
                            )
                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = PrimaryDarkTeal, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CourseCardView(
    course: CourseModel,
    onOpenTask: () -> Unit
) {
    SisyaCard(
        backgroundColor = SurfaceWhite,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusPill(text = course.level.uppercase(), color = InfoBlue)
            Text(text = "${course.progressPercent}% Completed", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryDarkTeal)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = course.title, style = MaterialTheme.typography.titleMedium, color = PrimaryText)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Current Lesson: ${course.currentLessonTitle}", style = MaterialTheme.typography.bodySmall, color = SecondaryText)

        Spacer(modifier = Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { course.progressPercent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = PrimaryDarkTeal,
            trackColor = BorderColor
        )

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SecondarySurface)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Link, contentDescription = null, tint = PrimaryDarkTeal, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Tied to ${course.connectedTask}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = PrimaryDarkTeal
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        SisyaButton(
            text = "Apply Lesson to Project Task",
            onClick = onOpenTask,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Default.Code,
            isPrimary = false
        )
    }
}

@Composable
fun PracticeProblemCardView(
    problem: PracticeProblem
) {
    var showSolution by remember { mutableStateOf(false) }

    SisyaCard(
        backgroundColor = SurfaceWhite,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusPill(text = problem.type.uppercase(), color = PrimaryDarkTeal)
            StatusPill(text = problem.difficulty, color = WarningAmber)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = problem.title, style = MaterialTheme.typography.titleMedium, color = PrimaryText)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = problem.objective, style = MaterialTheme.typography.bodySmall, color = SecondaryText)

        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1E293B))
                .padding(12.dp)
        ) {
            Text(
                text = problem.initialCodeOrScenario,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = Color(0xFFF8FAFC)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(text = problem.question, style = MaterialTheme.typography.bodyMedium, color = PrimaryText)

        if (showSolution) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SuccessSoft)
                    .padding(10.dp)
            ) {
                Column {
                    Text(text = "Engineering Solution & Rationale:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = SuccessGreen)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = problem.expectedOutcome, style = MaterialTheme.typography.bodySmall, color = PrimaryText)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        SisyaButton(
            text = if (showSolution) "Hide Solution" else "Verify Analysis & Solution",
            onClick = { showSolution = !showSolution },
            modifier = Modifier.fillMaxWidth(),
            isPrimary = false
        )
    }
}
