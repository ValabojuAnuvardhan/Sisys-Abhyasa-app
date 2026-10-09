package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProjectEntity::class,
        PhotoEvidenceEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun projectDao(): ProjectDao
    abstract fun photoEvidenceDao(): PhotoEvidenceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sisya_projects.db"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate initial default project and sample prototype evidence
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = INSTANCE?.projectDao()
                                val photoDao = INSTANCE?.photoEvidenceDao()

                                val defaultProject = ProjectEntity(
                                    id = "proj_01",
                                    title = "Autonomous Campus Placement Engine",
                                    description = "A resilient microservices-based placement management platform handling 10,000+ simultaneous student applications, automated eligibility filters, and zero-trust audit verification.",
                                    problemStatement = "Campus placement offices struggle with manual Excel sheets, missed eligibility criteria, fraud applications, and unverified student academic claims.",
                                    architectureSummary = "Hexagonal architecture with Kotlin/Ktor service, PostgreSQL storage, Redis token blacklist, and GitHub Actions CI pipelines.",
                                    techStackCsv = "Kotlin, PostgreSQL, JWT/Auth, Docker, Redis, REST APIs, CI/CD",
                                    targetRole = "Backend Systems Engineer",
                                    ownerId = "usr_anu_01",
                                    ownerName = "Anuvardhan Valaboju",
                                    repositoryName = "sisya-abhyasa/campus-placement-engine",
                                    progressPercentage = 75,
                                    activeMilestoneTitle = "Core Placement Architecture & Secure Identity",
                                    activeTaskNumber = 12,
                                    activeTaskTitle = "JWT Authentication & Refresh Token Rotation",
                                    activeTaskStatus = "IN_PROGRESS",
                                    completedTasksCount = 3,
                                    totalTasksCount = 4,
                                    readinessScore = 82,
                                    lastUpdatedTimestamp = System.currentTimeMillis()
                                )
                                dao?.insertOrUpdateProject(defaultProject)

                                val project2 = ProjectEntity(
                                    id = "proj_02",
                                    title = "Distributed Rate Limiter & Token Bucket Gateway",
                                    description = "High-throughput edge API gateway with sliding-window rate limiting, Redis clustering, and latency benchmarks under 5ms at 50,000 req/s.",
                                    problemStatement = "Downstream microservices overwhelmed by malicious traffic bursts and quota exhaustion without distributed coordination.",
                                    architectureSummary = "Go / Netty reactive proxy with Redis cluster Lua scripts, Prometheus telemetry, and Docker swarm orchestrations.",
                                    techStackCsv = "Kotlin, Redis, Lua, Docker, Prometheus, Distributed Systems",
                                    targetRole = "Distributed Systems Engineer",
                                    ownerId = "usr_anu_01",
                                    ownerName = "Anuvardhan Valaboju",
                                    repositoryName = "sisya-abhyasa/rate-limiter-gateway",
                                    progressPercentage = 90,
                                    activeMilestoneTitle = "Benchmarking & Cluster Failover",
                                    activeTaskNumber = 8,
                                    activeTaskTitle = "Redis Sentinel Chaos Testing & Failover Proof",
                                    activeTaskStatus = "COMPLETED",
                                    completedTasksCount = 4,
                                    totalTasksCount = 4,
                                    readinessScore = 91,
                                    lastUpdatedTimestamp = System.currentTimeMillis() - 86400000L
                                )
                                dao?.insertOrUpdateProject(project2)

                                val project3 = ProjectEntity(
                                    id = "proj_03",
                                    title = "Event-Driven Telemetry & Kafka Stream Processor",
                                    description = "Real-time log ingestion pipeline aggregating campus sensor and server telemetry using Apache Kafka, schema registry, and anomaly detection.",
                                    problemStatement = "Siloed operational logs prevented proactive detection of infrastructure degradation during examination spikes.",
                                    architectureSummary = "Event sourcing pattern with Kafka streams, Avro serialization, ClickHouse analytical store, and Grafana alerting.",
                                    techStackCsv = "Kafka, Avro, ClickHouse, Docker, Kotlin, Observability",
                                    targetRole = "Data Platform / Backend Engineer",
                                    ownerId = "usr_anu_01",
                                    ownerName = "Anuvardhan Valaboju",
                                    repositoryName = "sisya-abhyasa/kafka-telemetry-pipeline",
                                    progressPercentage = 50,
                                    activeMilestoneTitle = "Schema Registry & Stream Join Pipeline",
                                    activeTaskNumber = 5,
                                    activeTaskTitle = "Avro Schema Compatibility & Dead Letter Queue",
                                    activeTaskStatus = "IN_PROGRESS",
                                    completedTasksCount = 2,
                                    totalTasksCount = 4,
                                    readinessScore = 68,
                                    lastUpdatedTimestamp = System.currentTimeMillis() - 172800000L
                                )
                                dao?.insertOrUpdateProject(project3)

                                val project4 = ProjectEntity(
                                    id = "proj_04",
                                    title = "Zero-Trust Identity Provider & OAuth2 Server",
                                    description = "RFC 6749 compliant authorization server supporting PKCE, mTLS client credentials, and hardware security token verification.",
                                    problemStatement = "Legacy applications relied on shared secret basic auth leading to audit failures and credential leaks.",
                                    architectureSummary = "OAuth2 / OIDC spec implementation with RSA key rotation, tamper-evident audit logs, and WebAuthn integration.",
                                    techStackCsv = "OAuth2, Cryptography, Kotlin, PostgreSQL, Security, JWT",
                                    targetRole = "Cloud Security / Backend Engineer",
                                    ownerId = "usr_anu_01",
                                    ownerName = "Anuvardhan Valaboju",
                                    repositoryName = "sisya-abhyasa/zero-trust-idp",
                                    progressPercentage = 25,
                                    activeMilestoneTitle = "PKCE Authorization Code Grant",
                                    activeTaskNumber = 2,
                                    activeTaskTitle = "Code Verifier SHA-256 Challenge Validation",
                                    activeTaskStatus = "TODO",
                                    completedTasksCount = 1,
                                    totalTasksCount = 4,
                                    readinessScore = 54,
                                    lastUpdatedTimestamp = System.currentTimeMillis() - 259200000L
                                )
                                dao?.insertOrUpdateProject(project4)

                                // Populate initial sample evidence entry
                                photoDao?.insertEvidence(
                                    PhotoEvidenceEntity(
                                        id = "ev_photo_init_01",
                                        projectId = "proj_01",
                                        title = "System Architecture Whiteboard & Token Flow",
                                        caption = "Whiteboard diagram detailing the refresh token rotation state machine and redis blacklist invalidation before implementation.",
                                        imageUri = "",
                                        category = "WHITEBOARD",
                                        tagsCsv = "architecture,whiteboard,auth,security",
                                        milestoneTitle = "Core Placement Architecture & Secure Identity",
                                        taskNumber = 12,
                                        verifiedFlag = true,
                                        createdAt = System.currentTimeMillis() - 86400000L
                                    )
                                )
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
