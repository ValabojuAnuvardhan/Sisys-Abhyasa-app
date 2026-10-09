package com.example.data.local

import kotlinx.coroutines.flow.Flow

/**
 * Repository abstracting Room database operations for user projects and captured photo evidence.
 * Used by ChatViewModel, SisyaViewModel, EvidenceProofScreen, and ProjectsScreen.
 */
class ProjectRepository(
    private val projectDao: ProjectDao,
    private val photoEvidenceDao: PhotoEvidenceDao
) {

    val activeProject: Flow<ProjectEntity?> = projectDao.getActiveProject()

    fun observeAllProjects(): Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    fun observeProject(projectId: String): Flow<ProjectEntity?> = projectDao.getProjectById(projectId)

    suspend fun getProject(projectId: String): ProjectEntity? = projectDao.getProjectOnce(projectId)

    suspend fun saveProject(project: ProjectEntity) = projectDao.insertOrUpdateProject(project)

    suspend fun updateProgress(
        projectId: String,
        progress: Int,
        completedTasks: Int,
        readinessScore: Int,
        activeTaskNumber: Int? = null,
        activeTaskTitle: String? = null,
        activeTaskStatus: String? = null
    ) {
        val current = projectDao.getProjectOnce(projectId)
        if (current != null) {
            val updated = current.copy(
                progressPercentage = progress.coerceIn(0, 100),
                completedTasksCount = completedTasks,
                activeTaskNumber = activeTaskNumber ?: current.activeTaskNumber,
                activeTaskTitle = activeTaskTitle ?: current.activeTaskTitle,
                activeTaskStatus = activeTaskStatus ?: current.activeTaskStatus,
                readinessScore = readinessScore.coerceIn(0, 100),
                lastUpdatedTimestamp = System.currentTimeMillis()
            )
            projectDao.insertOrUpdateProject(updated)
        }
    }

    suspend fun updateActiveTask(
        projectId: String,
        taskNumber: Int,
        taskTitle: String,
        taskStatus: String
    ) {
        projectDao.updateActiveTask(
            projectId = projectId,
            taskNumber = taskNumber,
            taskTitle = taskTitle,
            taskStatus = taskStatus
        )
    }

    suspend fun incrementProgress(projectId: String, current: ProjectEntity) {
        advanceActiveTaskProgress(projectId)
    }

    suspend fun advanceActiveTaskProgress(projectId: String) {
        val current = projectDao.getProjectOnce(projectId) ?: return
        val newCompleted = (current.completedTasksCount + 1).coerceAtMost(current.totalTasksCount)
        val newProgress = ((newCompleted.toFloat() / current.totalTasksCount) * 100).toInt()
        val nextTaskNum = current.activeTaskNumber + 1
        val nextTitle = when (nextTaskNum) {
            13 -> "PostgreSQL Relational Schema & Flyway Migrations"
            14 -> "Redis Distributed Token Blacklist"
            15 -> "Docker Compose Integration & GitHub Actions CI"
            else -> "System Hardening & Performance Benchmarks"
        }
        val newScore = (current.readinessScore + 5).coerceAtMost(100)

        val updated = current.copy(
            progressPercentage = newProgress,
            completedTasksCount = newCompleted,
            activeTaskNumber = nextTaskNum,
            activeTaskTitle = nextTitle,
            activeTaskStatus = if (newCompleted == current.totalTasksCount) "COMPLETED" else "IN_PROGRESS",
            readinessScore = newScore,
            lastUpdatedTimestamp = System.currentTimeMillis()
        )
        projectDao.insertOrUpdateProject(updated)
    }

    // --- Photo Evidence Operations ---

    fun observeEvidencePhotosForProject(projectId: String): Flow<List<PhotoEvidenceEntity>> =
        photoEvidenceDao.getEvidencePhotosForProject(projectId)

    fun observeAllEvidencePhotos(): Flow<List<PhotoEvidenceEntity>> =
        photoEvidenceDao.getAllEvidencePhotos()

    fun observeEvidenceCountForProject(projectId: String): Flow<Int> =
        photoEvidenceDao.getEvidenceCountForProject(projectId)

    suspend fun savePhotoEvidence(evidence: PhotoEvidenceEntity) {
        photoEvidenceDao.insertEvidence(evidence)
    }

    suspend fun deletePhotoEvidence(evidenceId: String) {
        photoEvidenceDao.deleteById(evidenceId)
    }
}
