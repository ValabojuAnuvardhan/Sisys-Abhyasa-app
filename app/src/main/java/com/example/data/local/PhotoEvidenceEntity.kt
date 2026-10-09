package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing captured photographic evidence of engineering work,
 * prototypes, architecture diagrams, whiteboard designs, circuit builds, or test benchmarks.
 * Associated directly with a specific Project in the Room database.
 */
@Entity(
    tableName = "project_evidence_photos",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["projectId"]),
        Index(value = ["createdAt"])
    ]
)
data class PhotoEvidenceEntity(
    @PrimaryKey
    val id: String,
    val projectId: String,
    val title: String,
    val caption: String,
    val imageUri: String,
    val localFilePath: String? = null,
    val category: String = "PROTOTYPE", // PROTOTYPE, WHITEBOARD, HARDWARE, UI_SCREEN, BENCHMARK, CODE_AUDIT
    val tagsCsv: String = "engineering,prototype",
    val milestoneTitle: String? = null,
    val taskNumber: Int? = null,
    val verifiedFlag: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
