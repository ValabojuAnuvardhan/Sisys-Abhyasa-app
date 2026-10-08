package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.AppNavScreen

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableStateOf(1) }
    var selectedRole by remember { mutableStateOf("Backend Engineer") }
    var selectedHours by remember { mutableStateOf("10-15 hrs / week") }
    var targetDomain by remember { mutableStateOf("Distributed Systems & Microservices") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MainBackground)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 40.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Welcome to Śiṣya Abhyāsa",
                    style = MaterialTheme.typography.displayMedium,
                    color = PrimaryDarkTeal
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Learn. Build. Collaborate. Prove. Become Ready.",
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(14.dp))
                LinearProgressIndicator(
                    progress = { step / 3f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = PrimaryDarkTeal,
                    trackColor = BorderColor
                )
            }
        }

        when (step) {
            1 -> {
                item {
                    SisyaCard(backgroundColor = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Step 1: Define Target Engineering Role",
                            style = MaterialTheme.typography.titleLarge,
                            color = PrimaryText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your practice problems, project architecture recommendations, and proof ledger will align to this role's real-world bar.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SecondaryText
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        val roles = listOf("Backend Engineer", "Full Stack Engineer", "DevOps Engineer", "Cloud Systems Engineer")
                        roles.forEach { role ->
                            val isSelected = selectedRole == role
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) PrimarySoftTeal else SecondarySurface)
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = role,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) PrimaryDarkTeal else PrimaryText
                                )
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedRole = role },
                                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryDarkTeal)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        SisyaButton(
                            text = "Next: Available Time & Pace",
                            onClick = { step = 2 },
                            modifier = Modifier.fillMaxWidth(),
                            icon = Icons.Default.ArrowForward,
                            isPrimary = true
                        )
                    }
                }
            }

            2 -> {
                item {
                    SisyaCard(backgroundColor = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Step 2: Available Commitment",
                            style = MaterialTheme.typography.titleLarge,
                            color = PrimaryText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Engineering mastery requires consistent deep work and attributable PR execution.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SecondaryText
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        val hoursList = listOf("5-8 hrs / week (Part-time)", "10-15 hrs / week (Recommended)", "20+ hrs / week (Intensive)")
                        hoursList.forEach { hrs ->
                            val isSelected = selectedHours == hrs
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) PrimarySoftTeal else SecondarySurface)
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = hrs,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) PrimaryDarkTeal else PrimaryText
                                )
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedHours = hrs },
                                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryDarkTeal)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            SisyaButton(
                                text = "Back",
                                onClick = { step = 1 },
                                modifier = Modifier.weight(1f),
                                isPrimary = false
                            )
                            SisyaButton(
                                text = "Next: Connect GitHub",
                                onClick = { step = 3 },
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.ArrowForward,
                                isPrimary = true
                            )
                        }
                    }
                }
            }

            3 -> {
                item {
                    SisyaCard(backgroundColor = SurfaceWhite, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Step 3: GitHub & Evidence Ledger",
                            style = MaterialTheme.typography.titleLarge,
                            color = PrimaryText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "The platform connects your real GitHub commits, branches, and PRs as immutable evidence facts. AI interprets facts—it never invents them.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SecondaryText
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SuccessSoft)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "GitHub Connected: @anu-valaboju",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = SuccessGreen
                                )
                                Text(
                                    text = "SSH Keys & Signed Webhooks verified",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SecondaryText
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        SisyaButton(
                            text = "Launch My Śiṣya Abhyāsa Path",
                            onClick = onComplete,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_complete_onboarding"),
                            icon = Icons.Default.RocketLaunch,
                            isPrimary = true
                        )
                    }
                }
            }
        }
    }
}
