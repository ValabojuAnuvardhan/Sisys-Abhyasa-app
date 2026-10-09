package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {

    @Query("SELECT * FROM user_projects ORDER BY lastUpdatedTimestamp DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM user_projects WHERE id = :projectId LIMIT 1")
    fun getProjectById(projectId: String): Flow<ProjectEntity?>

    @Query("SELECT * FROM user_projects WHERE id = :projectId LIMIT 1")
    suspend fun getProjectOnce(projectId: String): ProjectEntity?

    @Query("SELECT * FROM user_projects LIMIT 1")
    fun getActiveProject(): Flow<ProjectEntity?>

    @Query("SELECT * FROM user_projects LIMIT 1")
    suspend fun getActiveProjectOnce(): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProject(project: ProjectEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjects(projects: List<ProjectEntity>)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Query("UPDATE user_projects SET progressPercentage = :progress, completedTasksCount = :completedTasks, readinessScore = :readinessScore, lastUpdatedTimestamp = :timestamp WHERE id = :projectId")
    suspend fun updateProjectProgress(
        projectId: String,
        progress: Int,
        completedTasks: Int,
        readinessScore: Int,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("UPDATE user_projects SET activeTaskNumber = :taskNumber, activeTaskTitle = :taskTitle, activeTaskStatus = :taskStatus, lastUpdatedTimestamp = :timestamp WHERE id = :projectId")
    suspend fun updateActiveTask(
        projectId: String,
        taskNumber: Int,
        taskTitle: String,
        taskStatus: String,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("DELETE FROM user_projects WHERE id = :projectId")
    suspend fun deleteProjectById(projectId: String)

    @Query("DELETE FROM user_projects")
    suspend fun clearAll()
}
