package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.AppNavScreen
import com.example.viewmodel.SisyaUiState

@Composable
fun MicroVivaScreen(
    state: SisyaUiState,
    onAnswerChange: (String) -> Unit,
    onSubmitAnswer: () -> Unit,
    onNavigate: (AppNavScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val task = state.selectedTask
    val turns = state.activeVivaTurns
    val currentIndex = state.currentVivaIndex
    val isDone = state.isVivaCompleted

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MainBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        // Defense Header & Scope
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusPill(text = "MICRO-VIVA DEFENSE", color = PrimaryDarkTeal)
                    StatusPill(text = if (isDone) "COMPLETED" else "IN PROGRESS", color = if (isDone) SuccessGreen else WarningAmber)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Defense for Task #${task.number}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Defend your engineering decisions and code implementation to establish attributable skill evidence.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText
                )
            }
        }

        // Section 42: Trust Boundary Card
        item {
            SisyaCard(
                backgroundColor = PrimarySoftTeal,
                borderColor = PrimaryDarkTeal.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = PrimaryDarkTeal, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Defense Trust Model",
                        style = MaterialTheme.typography.titleSmall,
                        color = PrimaryDarkTeal
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "A defense demonstrates demonstrated understanding and architectural reasoning. It does not replace code evidence; it anchors PR #${task.prNumber ?: 18} to your verified profile.",
                    style = MaterialTheme.typography.bodySmall,
                    color = PrimaryText
                )
            }
        }

        if (!isDone) {
            val activeQuestion = turns.getOrNull(currentIndex)
            if (activeQuestion != null) {
                // Active Question Card
                item {
                    SisyaCard(backgroundColor = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Question ${currentIndex + 1} of ${turns.size}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryDarkTeal
                            )
                            TrustBadge(badgeType = "SOCRATIC GUIDANCE")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = activeQuestion.question,
                            style = MaterialTheme.typography.titleMedium,
                            color = PrimaryText
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Follow-up Context: ${activeQuestion.followUp ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedTextField(
                            value = state.vivaAnswerInput,
                            onValueChange = onAnswerChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp)
                                .testTag("viva_answer_input"),
                            placeholder = {
                                Text(
                                    text = "Explain your technical decision, trade-offs, and where in your PR this is handled...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MutedText
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryDarkTeal,
                                unfocusedBorderColor = BorderColor,
                                focusedContainerColor = SurfaceWhite,
                                unfocusedContainerColor = SurfaceWhite
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        SisyaButton(
                            text = if (currentIndex == turns.size - 1) "Submit Defense & Verify Evidence" else "Submit Answer & Continue",
                            onClick = onSubmitAnswer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_submit_viva"),
                            icon = Icons.Default.Check,
                            isPrimary = true
                        )
                    }
                }
            }
        } else {
            // Defense Completed Success State
            item {
                SisyaCard(
                    backgroundColor = SurfaceWhite,
                    borderColor = SuccessGreen.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(SuccessSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Defense Successfully Completed!",
                            style = MaterialTheme.typography.titleLarge,
                            color = PrimaryText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Skill status upgraded to: DEMONSTRATED",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryDarkTeal
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your transcript has been recorded in the Evidence Ledger and added to your Public Proof Profile.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SisyaButton(
                                text = "View Proof Ledger",
                                onClick = { onNavigate(AppNavScreen.PROVE) },
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.WorkspacePremium,
                                isPrimary = true
                            )
                            SisyaButton(
                                text = "Return to Dashboard",
                                onClick = { onNavigate(AppNavScreen.DASHBOARD) },
                                modifier = Modifier.weight(1f),
                                isPrimary = false
                            )
                        }
                    }
                }
            }
        }

        // Section 30: Defense Transcripts Ledger
        item {
            SectionHeader(
                title = "Defense Transcripts & Reasoning Record",
                subtitle = "Permanent attributable record of engineering reasoning"
            )
        }

        turns.forEachIndexed { i, turn ->
            if (turn.studentAnswer != null) {
                item {
                    SisyaCard(backgroundColor = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Defense Turn #${i+1}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryDarkTeal)
                            StatusPill(text = "EVALUATED", color = SuccessGreen)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Q: ${turn.question}", style = MaterialTheme.typography.titleSmall, color = PrimaryText)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SecondarySurface)
                                .padding(8.dp)
                        ) {
                            Text(text = "Student Explanation:\n\"${turn.studentAnswer}\"", style = MaterialTheme.typography.bodySmall, color = PrimaryText)
                        }
                        if (turn.feedback != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = turn.feedback, style = MaterialTheme.typography.bodySmall, color = SuccessGreen)
                        }
                    }
                }
            }
        }
    }
}
