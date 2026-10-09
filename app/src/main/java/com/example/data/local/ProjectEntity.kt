package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a user's project details and progress metrics.
 * Enables ChatScreen and SisyaChat to reference project context, milestone stats,
 * and update completion progress locally.
 */
@Entity(tableName = "user_projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val problemStatement: String,
    val architectureSummary: String,
    val techStackCsv: String,
    val targetRole: String,
    val ownerId: String,
    val ownerName: String,
    val repositoryName: String,
    val progressPercentage: Int = 0,
    val activeMilestoneTitle: String,
    val activeTaskNumber: Int,
    val activeTaskTitle: String,
    val activeTaskStatus: String,
    val completedTasksCount: Int = 0,
    val totalTasksCount: Int = 4,
    val readinessScore: Int = 78,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)
