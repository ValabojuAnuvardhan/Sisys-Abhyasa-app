package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.JoinRequest
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.AppNavScreen
import com.example.viewmodel.SisyaUiState

@Composable
fun CollaborateScreen(
    state: SisyaUiState,
    onAcceptJoinRequest: (String) -> Unit,
    onNavigate: (AppNavScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Team Space, 1: Join Requests, 2: Community Discovery
    val tabs = listOf("Team Space", "Join Requests", "Discovery")

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
                    text = "Collaboration & Community",
                    style = MaterialTheme.typography.headlineLarge,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Work like a professional engineering squad with explicit role attribution.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SecondarySurface)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    tabs.forEachIndexed { idx, title ->
                        val isSelected = selectedTab == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) SurfaceWhite else Color.Transparent)
                                .clickable { selectedTab = idx }
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
                // Section 33: Team Space
                item {
                    SisyaCard(backgroundColor = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusPill(text = "PROJECT SQUAD", color = PrimaryDarkTeal)
                            Text(text = "${state.project.membersCount} Active Members", style = MaterialTheme.typography.labelSmall, color = MutedText)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = state.project.title, style = MaterialTheme.typography.titleMedium, color = PrimaryText)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Owner: ${state.project.ownerName} (valabojuanuvardhan@gmail.com)",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryText
                        )
                    }
                }

                item {
                    SectionHeader(title = "Squad Members & Attributed Work")
                }

                val members = listOf(
                    Triple("Anuvardhan Valaboju", "Project Owner • Backend Architect", "Tasks: #12, #13 (Auth & DB)"),
                    Triple("Priya Nair", "Contributor • Frontend Lead", "Tasks: #5, #6 (Compose & UI)"),
                    Triple("Rahul Sharma", "Contributor • DevOps Engineer", "Tasks: #9, #10 (Docker & CI/CD)")
                )

                items(members) { (name, role, assigned) ->
                    SisyaCard(backgroundColor = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(PrimarySoftTeal),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = name.first().toString(), style = MaterialTheme.typography.titleSmall, color = PrimaryDarkTeal)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = name, style = MaterialTheme.typography.titleSmall, color = PrimaryText)
                                Text(text = role, style = MaterialTheme.typography.bodySmall, color = PrimaryDarkTeal)
                                Text(text = assigned, style = MaterialTheme.typography.labelSmall, color = SecondaryText)
                            }
                        }
                    }
                }
            }

            1 -> {
                // Section 35: Join Requests
                item {
                    SectionHeader(
                        title = "Pending Collaboration Pitches",
                        subtitle = "Students request to join with explicit contribution pitches"
                    )
                }

                items(state.activeJoinRequests) { req ->
                    SisyaCard(backgroundColor = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = req.studentName, style = MaterialTheme.typography.titleMedium, color = PrimaryText)
                            StatusPill(text = req.status, color = if (req.status == "Pending") WarningAmber else SuccessGreen)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Target Role: ${req.targetRole}", style = MaterialTheme.typography.bodySmall, color = PrimaryDarkTeal)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SecondarySurface)
                                .padding(10.dp)
                        ) {
                            Text(text = "Pitch:\n\"${req.pitch}\"", style = MaterialTheme.typography.bodySmall, color = PrimaryText)
                        }

                        if (req.status == "Pending") {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                SisyaButton(
                                    text = "Decline",
                                    onClick = { /* decline */ },
                                    modifier = Modifier.weight(1f),
                                    isPrimary = false
                                )
                                SisyaButton(
                                    text = "Accept as Contributor",
                                    onClick = { onAcceptJoinRequest(req.id) },
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Default.Check,
                                    isPrimary = true,
                                    testTag = "btn_accept_join_req"
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                // Section 34: Community Project Discovery
                item {
                    SectionHeader(
                        title = "Public Engineering Projects",
                        subtitle = "Discover real student open-source software to contribute to"
                    )
                }

                val publicProjects = listOf(
                    Triple("Distributed Log Indexer Daemon", "High-throughput log pipeline with custom ring buffer.", "Kotlin • gRPC • TimescaleDB"),
                    Triple("Zero-Knowledge Voting Ledger", "Decentralized cryptographic ballot audit platform.", "Rust • Cryptography • REST"),
                    Triple("Real-Time Collaborative Canvas Engine", "WebRTC & CRDT concurrent state reconciliation.", "TypeScript • WebSocket • Canvas")
                )

                items(publicProjects) { (title, desc, stack) ->
                    SisyaCard(backgroundColor = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
                        StatusPill(text = "COMMUNITY SQUAD", color = InfoBlue)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = title, style = MaterialTheme.typography.titleMedium, color = PrimaryText)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = desc, style = MaterialTheme.typography.bodySmall, color = SecondaryText)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Tech Stack: $stack", style = MaterialTheme.typography.labelSmall, color = PrimaryDarkTeal)
                        Spacer(modifier = Modifier.height(12.dp))
                        SisyaButton(
                            text = "Submit Collaboration Pitch",
                            onClick = { /* pitch flow */ },
                            modifier = Modifier.fillMaxWidth(),
                            isPrimary = false
                        )
                    }
                }
            }
        }
    }
}
