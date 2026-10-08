package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SkillEvidenceItem
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.AppNavScreen
import com.example.viewmodel.SisyaUiState

@Composable
fun EvidenceProofScreen(
    state: SisyaUiState,
    onTogglePreview: (Boolean) -> Unit,
    onNavigate: (AppNavScreen) -> Unit,
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
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "System C — Proof Ledger",
                            style = MaterialTheme.typography.headlineLarge,
                            color = PrimaryText
                        )
                        Text(
                            text = "Contribution → Commit → PR → CI → Defense → Proof Profile",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))

                // Public Proof Profile Toggle Banner
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Public Proof Profile",
                                style = MaterialTheme.typography.titleMedium,
                                color = PrimaryText
                            )
                            Text(
                                text = if (state.userProfile.isPublicProofPublished) "Live at: sisya.io/proof/${state.userProfile.githubHandle}" else "Currently Private",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (state.userProfile.isPublicProofPublished) PrimaryDarkTeal else MutedText
                            )
                        }
                        StatusPill(
                            text = if (state.userProfile.isPublicProofPublished) "PUBLISHED" else "DRAFT",
                            color = if (state.userProfile.isPublicProofPublished) SuccessGreen else SecondaryText
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Recruiters and engineering leads inspect verified PRs, CI execution, and Micro-Viva defense transcripts rather than resume claims.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    SisyaButton(
                        text = if (state.isPublicProofPreviewOpen) "Hide Public Proof Preview" else "Inspect Public Proof Profile",
                        onClick = { onTogglePreview(!state.isPublicProofPreviewOpen) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_inspect_proof_profile"),
                        icon = Icons.Default.Launch,
                        isPrimary = true
                    )
                }
            }
        }

        // Section 47: Public Proof Profile Preview Drawer/Card
        if (state.isPublicProofPreviewOpen) {
            item {
                SisyaCard(
                    backgroundColor = PrimarySoftTeal,
                    borderColor = PrimaryDarkTeal,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(PrimaryDarkTeal),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.userProfile.name.split(" ").map { it.first() }.joinToString(""),
                                style = MaterialTheme.typography.titleMedium,
                                color = SurfaceWhite
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = state.userProfile.name, style = MaterialTheme.typography.titleLarge, color = PrimaryText)
                            Text(text = "Target Role: ${state.userProfile.targetRole} • Class of 2027", style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = state.userProfile.bio, style = MaterialTheme.typography.bodyMedium, color = PrimaryText)

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = PrimaryDarkTeal.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Verified Proof Bundle:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryDarkTeal
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• 1 Active Microservices Project (${state.project.title})\n• 4 Attributable PRs merged with 100% CI pass rate\n• Zero self-declared vanity badges; 100% verified defense backing",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryText
                    )
                }
            }
        }

        // Section 37 & 45: Skill Evidence Ledger
        item {
            SectionHeader(
                title = "Attributable Skill Evidence Ledger",
                subtitle = "Never vanity % scores; only real PRs, CI, and defenses"
            )
        }

        items(state.evidenceList) { evidence ->
            EvidenceCardView(evidence = evidence)
        }
    }
}

@Composable
fun EvidenceCardView(
    evidence: SkillEvidenceItem
) {
    SisyaCard(backgroundColor = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = evidence.skillName,
                style = MaterialTheme.typography.titleMedium,
                color = PrimaryText
            )
            EvidenceStateBadge(state = evidence.state)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "From Task #${evidence.taskNumber}: ${evidence.taskTitle}",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = PrimaryDarkTeal
        )
        Text(
            text = "Project: ${evidence.projectName}",
            style = MaterialTheme.typography.bodySmall,
            color = SecondaryText
        )

        Spacer(modifier = Modifier.height(10.dp))
        // Artifacts row: PR & CI
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SecondarySurface)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MergeType, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "PR #${evidence.prNumber} (${evidence.prSha})",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = PrimaryText
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "CI: ${evidence.ciStatus}", fontSize = 11.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
            }
            Text(text = evidence.verifiedDate, fontSize = 11.sp, color = MutedText)
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Micro-Viva Defense Transcript:",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = PrimaryText
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = evidence.defenseTranscriptSnippet,
            style = MaterialTheme.typography.bodySmall,
            color = SecondaryText
        )
    }
}
