package com.example

import com.example.model.EvidenceState
import com.example.model.SkillEvidenceItem
import com.example.ui.components.EvidenceSummaryMetrics
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EvidenceSummaryComponentTest {

    @Test
    fun `test evidence summary metric computation with verified skills and completed assessments`() {
        // Sample Evidence items
        val evidenceList = listOf(
            SkillEvidenceItem(
                id = "ev_1",
                skillName = "REST API Architecture",
                state = EvidenceState.REPEATEDLY_DEMONSTRATED,
                projectName = "Campus Placement Engine",
                taskNumber = 1,
                taskTitle = "JWT Auth Service",
                prNumber = 12,
                prSha = "a1b2c3d4",
                ciStatus = "Passed",
                defenseTranscriptSnippet = "Q: Token revocation? A: Redis blacklisting",
                verifiedDate = "Oct 05, 2026"
            ),
            SkillEvidenceItem(
                id = "ev_2",
                skillName = "Database Indexing",
                state = EvidenceState.DEMONSTRATED,
                projectName = "Campus Placement Engine",
                taskNumber = 2,
                taskTitle = "Schema Migration",
                prNumber = 14,
                prSha = "b2c3d4e5",
                ciStatus = "Passed",
                defenseTranscriptSnippet = "Q: B-tree indexing? A: Composite index on user_id + status",
                verifiedDate = "Oct 06, 2026"
            ),
            SkillEvidenceItem(
                id = "ev_3",
                skillName = "Docker Containerization",
                state = EvidenceState.PRACTICED, // Not yet verified demonstrated level
                projectName = "Campus Placement Engine",
                taskNumber = 3,
                taskTitle = "Docker Compose Setup",
                prNumber = 16,
                prSha = "c3d4e5f6",
                ciStatus = "Passed",
                defenseTranscriptSnippet = "Q: Multi-stage build? A: In progress",
                verifiedDate = "Oct 07, 2026"
            )
        )

        val photoEvidenceCount = 2
        val totalEvidenceCount = evidenceList.size + photoEvidenceCount

        // Assessments completed: viva turns evaluated (e.g. 2) + completed task defenses (1)
        val assessmentsCompletedCount = 3

        // Verified skills: filter >= DEMONSTRATED (level 4)
        val verifiedSkills = evidenceList
            .filter { it.state.level >= EvidenceState.DEMONSTRATED.level }
            .map { it.skillName }
            .distinct()

        val metrics = EvidenceSummaryMetrics(
            evidenceCollectedCount = totalEvidenceCount,
            assessmentsCompletedCount = assessmentsCompletedCount,
            skillsWithVerifiedEvidenceCount = verifiedSkills.size,
            totalSkillsCount = 5,
            photoEvidenceCount = photoEvidenceCount
        )

        assertEquals(5, metrics.evidenceCollectedCount)
        assertEquals(3, metrics.assessmentsCompletedCount)
        assertEquals(2, metrics.skillsWithVerifiedEvidenceCount)
        assertEquals(2, metrics.photoEvidenceCount)
        assertTrue(verifiedSkills.contains("REST API Architecture"))
        assertTrue(verifiedSkills.contains("Database Indexing"))
    }

    @Test
    fun `test zero-trust verified state distinction from self declared and practiced`() {
        // Verify levels
        assertTrue(EvidenceState.SELF_DECLARED.level < EvidenceState.DEMONSTRATED.level)
        assertTrue(EvidenceState.PRACTICED.level < EvidenceState.DEMONSTRATED.level)
        assertTrue(EvidenceState.OBSERVED.level < EvidenceState.DEMONSTRATED.level)
        assertTrue(EvidenceState.DEMONSTRATED.level >= 4)
        assertTrue(EvidenceState.REPEATEDLY_DEMONSTRATED.level == 5)
    }
}
