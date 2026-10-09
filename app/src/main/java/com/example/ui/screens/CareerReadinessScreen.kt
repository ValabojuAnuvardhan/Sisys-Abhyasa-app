package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.AppNavScreen
import com.example.viewmodel.SisyaUiState

@Composable
fun CareerReadinessScreen(
    state: SisyaUiState,
    onNavigate: (AppNavScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val readiness = state.backendReadiness

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Career Readiness & Skill Gaps",
                            style = MaterialTheme.typography.headlineLarge,
                            color = PrimaryText
                        )
                        Text(
                            text = "Role-specific, evidence-backed evaluation with explainable next actions.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SecondaryText
                        )
                    }

                    Surface(
                        color = PrimaryDarkTeal,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .clickable { onNavigate(AppNavScreen.PROJECT_DASHBOARD) }
                            .testTag("btn_career_to_project_dashboard")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Insights, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Visual Dashboard",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Section 49: Explainable Readiness Index
        item {
            SisyaCard(backgroundColor = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = readiness.roleName,
                            style = MaterialTheme.typography.titleLarge,
                            color = PrimaryText
                        )
                        Text(
                            text = "Target Evaluation Benchmark",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText
                        )
                    }
                    Text(
                        text = "${readiness.score}/100",
                        style = MaterialTheme.typography.headlineLarge,
                        color = PrimaryDarkTeal
                    )
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

                Spacer(modifier = Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimarySoftTeal)
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = PrimaryDarkTeal, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Next Recommended Engineering Action:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryDarkTeal
                            )
                            Text(
                                text = readiness.nextAction,
                                style = MaterialTheme.typography.bodySmall,
                                color = PrimaryText
                            )
                        }
                    }
                }
            }
        }

        // Verified Strengths
        item {
            SisyaCard(backgroundColor = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Evidence-Backed Verified Strengths",
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(8.dp))
                readiness.strengths.forEach { str ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = str, style = MaterialTheme.typography.bodyMedium, color = PrimaryText)
                    }
                }
            }
        }

        // Identified Skill Gaps & Path forward
        item {
            SisyaCard(backgroundColor = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Identified Skill Gaps to Close",
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Every gap is tied to a specific project task you can execute next.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
                Spacer(modifier = Modifier.height(10.dp))
                readiness.gaps.forEach { gap ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = gap, style = MaterialTheme.typography.bodyMedium, color = PrimaryText)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                SisyaButton(
                    text = "Open Active Task to Close Gaps",
                    onClick = { onNavigate(AppNavScreen.TASK_WORKSPACE) },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.PlayArrow,
                    isPrimary = true
                )
            }
        }

        // Section 50: Alternative Career Tracks Benchmark
        item {
            SectionHeader(
                title = "Alternative Career Tracks Benchmark",
                subtitle = "How your demonstrated engineering evidence maps to other roles"
            )
        }

        val altRoles = listOf(
            Triple("Full Stack Engineer", 68, "Strong on API/Auth; Needs Frontend State Management evidence"),
            Triple("DevOps / Cloud Engineer", 48, "Needs Container Orchestration and Terraform evidence"),
            Triple("Systems Software Engineer", 42, "Needs Low-Level Memory and Concurrency benchmarks")
        )

        altRoles.forEach { (role, score, notes) ->
            item {
                SisyaCard(backgroundColor = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = role, style = MaterialTheme.typography.titleSmall, color = PrimaryText)
                        Text(text = "$score / 100", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = PrimaryDarkTeal)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = notes, style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                }
            }
        }
    }
}
