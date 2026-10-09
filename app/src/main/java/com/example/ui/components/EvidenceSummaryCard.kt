package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EvidenceState
import com.example.ui.theme.*

/**
 * Summary metrics for evidence, assessments, and verified competencies.
 */
data class EvidenceSummaryMetrics(
    val evidenceCollectedCount: Int,
    val assessmentsCompletedCount: Int,
    val skillsWithVerifiedEvidenceCount: Int,
    val totalSkillsCount: Int = 0,
    val pendingAssessmentsCount: Int = 0,
    val photoEvidenceCount: Int = 0
)

/**
 * An EvidenceSummary dashboard component that tracks counts for:
 * 1. Evidence collected (attributable code artifacts, PRs, photos, commits)
 * 2. Assessments completed (Micro-Viva defenses and rubric evaluations finished)
 * 3. Skills with verified evidence (competencies with repeatedly demonstrated proof)
 *
 * Adheres strictly to Śiṣya Abhyāsa's core principle:
 * "Never vanity % scores; only real PRs, CI, and defenses."
 */
@Composable
fun EvidenceSummaryCard(
    metrics: EvidenceSummaryMetrics,
    modifier: Modifier = Modifier,
    onViewEvidenceLedger: (() -> Unit)? = null,
    onStartAssessment: (() -> Unit)? = null
) {
    SisyaCard(
        backgroundColor = SurfaceWhite,
        borderColor = PrimaryDarkTeal.copy(alpha = 0.25f),
        modifier = modifier
            .fillMaxWidth()
            .testTag("evidence_summary_card")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimarySoftTeal),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified Proof Icon",
                        tint = PrimaryDarkTeal,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Evidence & Competency Summary",
                        style = MaterialTheme.typography.titleMedium,
                        color = PrimaryText
                    )
                    Text(
                        text = "System C • Provable Engineering Ledger",
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText
                    )
                }
            }

            StatusPill(
                text = "VERIFIED",
                color = SuccessGreen
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3-Column Metric Stat Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("evidence_summary_metrics_row"),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Evidence Collected Count
            EvidenceMetricTile(
                count = metrics.evidenceCollectedCount,
                label = "Evidence Collected",
                subtext = if (metrics.photoEvidenceCount > 0) "${metrics.photoEvidenceCount} prototype photos" else "PRs & Code",
                icon = Icons.Default.FolderSpecial,
                accentColor = PrimaryDarkTeal,
                containerColor = PrimarySoftTeal,
                modifier = Modifier
                    .weight(1f)
                    .testTag("metric_evidence_collected")
            )

            // 2. Assessments Completed Count
            EvidenceMetricTile(
                count = metrics.assessmentsCompletedCount,
                label = "Assessments Done",
                subtext = "Micro-Viva Defenses",
                icon = Icons.Default.RecordVoiceOver,
                accentColor = InfoBlue,
                containerColor = InfoBlue.copy(alpha = 0.12f),
                modifier = Modifier
                    .weight(1f)
                    .testTag("metric_assessments_completed")
            )

            // 3. Skills With Verified Evidence Count
            EvidenceMetricTile(
                count = metrics.skillsWithVerifiedEvidenceCount,
                label = "Verified Skills",
                subtext = if (metrics.totalSkillsCount > 0) "of ${metrics.totalSkillsCount} tracked" else "Peer/Viva Proven",
                icon = Icons.Default.WorkspacePremium,
                accentColor = SuccessGreen,
                containerColor = SuccessGreen.copy(alpha = 0.12f),
                modifier = Modifier
                    .weight(1f)
                    .testTag("metric_verified_skills")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Verification Integrity Guarantee Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SecondarySurface)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = PrimaryDarkTeal,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Tamper-resistant cryptographic binding to git commits, CI logs & voice defense transcripts.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = SecondaryText,
                lineHeight = 15.sp
            )
        }

        // Action Buttons if callbacks provided
        if (onViewEvidenceLedger != null || onStartAssessment != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (onViewEvidenceLedger != null) {
                    SisyaButton(
                        text = "View Evidence Ledger",
                        onClick = onViewEvidenceLedger,
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.FactCheck,
                        isPrimary = true,
                        testTag = "btn_summary_view_evidence"
                    )
                }
                if (onStartAssessment != null) {
                    SisyaButton(
                        text = "Defend Work",
                        onClick = onStartAssessment,
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Gavel,
                        isPrimary = false,
                        testTag = "btn_summary_start_assessment"
                    )
                }
            }
        }
    }
}

@Composable
private fun EvidenceMetricTile(
    count: Int,
    label: String,
    subtext: String,
    icon: ImageVector,
    accentColor: Color,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SecondarySurface)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(containerColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "$count",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = PrimaryText
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = PrimaryText,
                maxLines = 1
            )

            Text(
                text = subtext,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.sp
                ),
                color = SecondaryText,
                maxLines = 1
            )
        }
    }
}
