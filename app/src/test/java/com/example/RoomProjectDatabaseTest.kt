package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.PhotoEvidenceDao
import com.example.data.local.PhotoEvidenceEntity
import com.example.data.local.ProjectDao
import com.example.data.local.ProjectEntity
import com.example.data.local.ProjectRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoomProjectDatabaseTest {

    private lateinit var database: AppDatabase
    private lateinit var projectDao: ProjectDao
    private lateinit var photoEvidenceDao: PhotoEvidenceDao
    private lateinit var repository: ProjectRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        projectDao = database.projectDao()
        photoEvidenceDao = database.photoEvidenceDao()
        repository = ProjectRepository(projectDao, photoEvidenceDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `insert and retrieve project entity`() = runBlocking {
        val testProject = ProjectEntity(
            id = "proj_test_01",
            title = "Autonomous Campus Placement Engine",
            description = "Resilient placement engine",
            problemStatement = "Manual placement workflows",
            architectureSummary = "Hexagonal architecture",
            techStackCsv = "Kotlin, PostgreSQL, Docker",
            targetRole = "Backend Systems Engineer",
            ownerId = "usr_anu_01",
            ownerName = "Anuvardhan Valaboju",
            repositoryName = "sisya-abhyasa/campus-placement-engine",
            progressPercentage = 25,
            activeMilestoneTitle = "Core Placement Architecture",
            activeTaskNumber = 12,
            activeTaskTitle = "JWT Auth & Refresh Token Rotation",
            activeTaskStatus = "IN_PROGRESS",
            completedTasksCount = 1,
            totalTasksCount = 4,
            readinessScore = 78
        )

        projectDao.insertOrUpdateProject(testProject)

        val retrieved = projectDao.getProjectOnce("proj_test_01")
        assertNotNull(retrieved)
        assertEquals("Autonomous Campus Placement Engine", retrieved?.title)
        assertEquals(25, retrieved?.progressPercentage)
        assertEquals("Backend Systems Engineer", retrieved?.targetRole)
    }

    @Test
    fun `insert and retrieve photo evidence associated with project`() = runBlocking {
        val testProject = ProjectEntity(
            id = "proj_photo_test",
            title = "Campus Placement Engine",
            description = "Resilient platform",
            problemStatement = "Manual workflows",
            architectureSummary = "Microservices",
            techStackCsv = "Kotlin, PostgreSQL",
            targetRole = "Backend Engineer",
            ownerId = "usr_01",
            ownerName = "Anuvardhan",
            repositoryName = "repo/placement",
            activeMilestoneTitle = "Milestone 1",
            activeTaskNumber = 12,
            activeTaskTitle = "JWT Security",
            activeTaskStatus = "IN_PROGRESS"
        )
        projectDao.insertOrUpdateProject(testProject)

        val photoEvidence = PhotoEvidenceEntity(
            id = "ev_snap_01",
            projectId = "proj_photo_test",
            title = "Whiteboard Architecture Diagram",
            caption = "Detailed sketch of token rotation and Redis blacklist cache invalidation.",
            imageUri = "content://com.example.fileprovider/test.jpg",
            category = "WHITEBOARD",
            tagsCsv = "architecture,security,whiteboard",
            milestoneTitle = "Milestone 1",
            taskNumber = 12
        )

        repository.savePhotoEvidence(photoEvidence)

        val retrievedPhotos = repository.observeEvidencePhotosForProject("proj_photo_test").first()
        assertEquals(1, retrievedPhotos.size)
        assertEquals("Whiteboard Architecture Diagram", retrievedPhotos[0].title)
        assertEquals("WHITEBOARD", retrievedPhotos[0].category)
        assertEquals(12, retrievedPhotos[0].taskNumber)
    }

    @Test
    fun `advance project progress updates task count and score`() = runBlocking {
        val testProject = ProjectEntity(
            id = "proj_test_02",
            title = "Autonomous Placement Platform",
            description = "Platform",
            problemStatement = "Problem",
            architectureSummary = "Arch",
            techStackCsv = "Kotlin",
            targetRole = "Backend Engineer",
            ownerId = "usr_01",
            ownerName = "Anuvardhan",
            repositoryName = "repo/placement",
            progressPercentage = 25,
            activeMilestoneTitle = "Milestone 1",
            activeTaskNumber = 12,
            activeTaskTitle = "JWT Auth",
            activeTaskStatus = "IN_PROGRESS",
            completedTasksCount = 1,
            totalTasksCount = 4,
            readinessScore = 70
        )

        projectDao.insertOrUpdateProject(testProject)
        repository.advanceActiveTaskProgress("proj_test_02")

        val updated = projectDao.getProjectOnce("proj_test_02")
        assertNotNull(updated)
        assertEquals(2, updated?.completedTasksCount)
        assertEquals(50, updated?.progressPercentage)
        assertEquals(13, updated?.activeTaskNumber)
        assertEquals(75, updated?.readinessScore)
    }

    @Test
    fun `query all projects for project dashboard metrics`() = runBlocking {
        val proj1 = ProjectEntity(
            id = "proj_multi_01",
            title = "Autonomous Placement Platform",
            description = "Resilient backend",
            problemStatement = "Problem 1",
            architectureSummary = "Hexagonal",
            techStackCsv = "Kotlin, PostgreSQL",
            targetRole = "Backend Engineer",
            ownerId = "usr_01",
            ownerName = "Anuvardhan",
            repositoryName = "repo/placement",
            progressPercentage = 80,
            activeMilestoneTitle = "Milestone 1",
            activeTaskNumber = 12,
            activeTaskTitle = "JWT Auth",
            activeTaskStatus = "DONE",
            completedTasksCount = 4,
            totalTasksCount = 5,
            readinessScore = 85
        )
        val proj2 = ProjectEntity(
            id = "proj_multi_02",
            title = "Distributed Rate Limiter",
            description = "Edge Gateway",
            problemStatement = "Problem 2",
            architectureSummary = "Reactive Proxy",
            techStackCsv = "Kotlin, Redis, Lua",
            targetRole = "Distributed Systems Engineer",
            ownerId = "usr_01",
            ownerName = "Anuvardhan",
            repositoryName = "repo/rate-limiter",
            progressPercentage = 60,
            activeMilestoneTitle = "Milestone 2",
            activeTaskNumber = 6,
            activeTaskTitle = "Redis Sentinel",
            activeTaskStatus = "IN_PROGRESS",
            completedTasksCount = 3,
            totalTasksCount = 5,
            readinessScore = 78
        )

        projectDao.insertProjects(listOf(proj1, proj2))

        val allProjects = repository.observeAllProjects().first()
        assertEquals(2, allProjects.size)

        val avgCompletion = allProjects.sumOf { it.progressPercentage } / allProjects.size
        assertEquals(70, avgCompletion)

        val avgReadiness = allProjects.sumOf { it.readinessScore } / allProjects.size
        assertEquals(81, avgReadiness)
    }
}
