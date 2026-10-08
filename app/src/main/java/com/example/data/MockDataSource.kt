package com.example.data

import com.example.model.*

object MockDataSource {

    val sampleUser = UserProfile(
        id = "usr_anu_01",
        name = "Anuvardhan Valaboju",
        email = "valabojuanuvardhan@gmail.com",
        institution = "National Institute of Technology",
        branch = "Computer Science & Engineering",
        year = "3rd Year",
        targetRole = "Backend Engineer",
        targetReadinessScore = 78,
        githubHandle = "anu-valaboju",
        isGithubConnected = true,
        bio = "Building high-throughput microservices, hardened auth middlewares, and observable distributed transaction pipelines.",
        isPublicProofPublished = true
    )

    val sampleTasks = listOf(
        TaskModel(
            id = "task_12",
            number = 12,
            milestoneId = "m_01",
            title = "Implement JWT Authentication & Refresh Token Rotation",
            objective = "Build a stateless, tamper-proof authentication service with HMAC-SHA256 tokens and database-backed revoked token blacklist.",
            description = "Develop the POST /api/v1/auth/login and /refresh endpoints. Handle expired token rejection, cryptographic signature verification, and HTTP-only Secure cookie distribution.",
            acceptanceCriteria = listOf(
                "Verify HMAC-SHA256 signature using HS256 secret in environment",
                "Reject expired access tokens with explicit 401 Unauthorized",
                "Implement token blacklist check in Redis/memory store for revoked sessions",
                "Unit and integration test suite passing with >= 85% branch coverage"
            ),
            version = 1,
            status = TaskStatus.IN_REVIEW,
            requiredSkill = "Authentication & JWT",
            learningChecklist = listOf(
                LearningChecklistItem("lc_1", "Understand symmetric vs asymmetric cryptographic signing", true, "Security"),
                LearningChecklistItem("lc_2", "JWT claims structure: header, payload, and signature format", true, "JWT"),
                LearningChecklistItem("lc_3", "Token expiration window & clock drift tolerance", true, "Auth Protocols"),
                LearningChecklistItem("lc_4", "Attack vectors: timing attacks and alg:none spoofing", true, "SecOps")
            ),
            branchName = "feat/auth-jwt-rotation",
            prNumber = 18,
            prTitle = "feat(auth): token rotation middleware & verification unit tests",
            prStatus = "Under Review",
            ciStatus = "Passed",
            defenseStatus = "Required",
            assigneeName = "Anuvardhan",
            dueDate = "Today"
        ),
        TaskModel(
            id = "task_13",
            number = 13,
            milestoneId = "m_01",
            title = "Database Migration & Placement Drive Schema Design",
            objective = "Design PostgreSQL normalized schema for companies, job postings, eligible branches, and student applications with ACID integrity.",
            description = "Write migration scripts using Liquibase/Flyway. Add foreign key constraints, composite index on (student_id, job_id), and soft-deletion columns.",
            acceptanceCriteria = listOf(
                "Unique constraint preventing duplicate active applications",
                "Composite B-tree indexing for instant eligibility lookups",
                "Idempotent migration script tested on clean database instance"
            ),
            version = 2,
            status = TaskStatus.IN_PROGRESS,
            requiredSkill = "PostgreSQL & Database Design",
            learningChecklist = listOf(
                LearningChecklistItem("lc_5", "3rd Normal Form (3NF) tradeoffs in high-read systems", true, "Databases"),
                LearningChecklistItem("lc_6", "Index cardinality and execution plan explain analysis", false, "SQL Performance")
            ),
            branchName = "feat/schema-placement-drives",
            prNumber = 21,
            prTitle = "feat(db): schema migrations and composite indexing",
            prStatus = "Open",
            ciStatus = "Passed",
            defenseStatus = "Pending",
            assigneeName = "Anuvardhan",
            dueDate = "Oct 12"
        ),
        TaskModel(
            id = "task_14",
            number = 14,
            milestoneId = "m_02",
            title = "Asynchronous Resume Parsing Worker via BullMQ/Kafka",
            objective = "Implement asynchronous decoupled pipeline for candidate PDF extraction and keyword vectorization.",
            description = "Offload heavy PDF parsing from the main HTTP thread into an event worker. Maintain retry idempotency and poison pill queue.",
            acceptanceCriteria = listOf(
                "Zero HTTP timeout on multi-megabyte resume uploads",
                "Dead letter queue routing after 3 failed parsing attempts"
            ),
            version = 1,
            status = TaskStatus.TODO,
            requiredSkill = "Message Queues & Async Processing",
            learningChecklist = listOf(
                LearningChecklistItem("lc_7", "At-least-once delivery semantics & consumer idempotency", false, "Distributed Systems")
            ),
            branchName = "feat/async-resume-worker",
            prNumber = null,
            prTitle = null,
            prStatus = null,
            ciStatus = null,
            defenseStatus = "Pending",
            assigneeName = "Anuvardhan",
            dueDate = "Oct 19"
        ),
        TaskModel(
            id = "task_15",
            number = 15,
            milestoneId = "m_02",
            title = "RESTful Application Status Transition State Machine",
            objective = "Model strict application state transitions (Applied -> Shortlisted -> Interviewed -> Selected / Rejected).",
            description = "Enforce finite state machine validation to prevent invalid transition states by unauthorized recruiters.",
            acceptanceCriteria = listOf(
                "Reject unauthorized transitions with 403 Forbidden",
                "Audit log recording every transition with actor timestamp"
            ),
            version = 1,
            status = TaskStatus.BACKLOG,
            requiredSkill = "REST API Design",
            learningChecklist = listOf(
                LearningChecklistItem("lc_8", "State pattern vs relational status checks", false, "Architecture")
            ),
            branchName = "feat/fsm-application-lifecycle",
            prNumber = null,
            prTitle = null,
            prStatus = null,
            ciStatus = null,
            defenseStatus = "Pending",
            assigneeName = "Team",
            dueDate = "Oct 26"
        )
    )

    val sampleMilestones = listOf(
        MilestoneModel(
            id = "m_01",
            number = 1,
            title = "Core Placement Architecture & Secure Identity",
            objective = "Set up hardened backend foundations, role-based JWT auth, and primary placement entity persistence.",
            status = "In Progress",
            tasks = listOf(sampleTasks[0], sampleTasks[1])
        ),
        MilestoneModel(
            id = "m_02",
            number = 2,
            title = "High-Volume Application Processing & Worker Pipeline",
            objective = "Scale out asynchronous application submissions with background worker concurrency.",
            status = "Upcoming",
            tasks = listOf(sampleTasks[2], sampleTasks[3])
        ),
        MilestoneModel(
            id = "m_03",
            number = 3,
            title = "Audit Defense, Observability & Production Delivery",
            objective = "Add Prometheus metrics, CI/CD automated staging deploy, and public API documentation.",
            status = "Planning",
            tasks = emptyList()
        )
    )

    val sampleProject = ProjectModel(
        id = "proj_01",
        title = "Autonomous Campus Placement Engine",
        problemStatement = "Campus placement offices struggle with manual Excel sheets, missed eligibility criteria, fraud applications, and unverified student academic claims.",
        description = "A resilient microservices-based placement management platform handling 10,000+ simultaneous student applications, automated eligibility filters, and zero-trust audit verification.",
        architectureSummary = "Hexagonal architecture with Kotlin/Ktor service, PostgreSQL storage, Redis token blacklist, and GitHub Actions CI pipelines.",
        techStack = listOf("Kotlin", "PostgreSQL", "JWT / Auth", "Docker", "Redis", "REST APIs", "CI/CD"),
        targetRole = "Backend Engineer",
        ownerId = "usr_anu_01",
        ownerName = "Anuvardhan Valaboju",
        membersCount = 3,
        milestones = sampleMilestones,
        repositoryName = "sisya-abhyasa/campus-placement-engine",
        isPublic = true
    )

    val sampleEvidenceItems = listOf(
        SkillEvidenceItem(
            id = "ev_01",
            skillName = "Authentication & JWT",
            state = EvidenceState.DEMONSTRATED,
            projectName = "Autonomous Campus Placement Engine",
            taskNumber = 12,
            taskTitle = "Implement JWT Authentication & Refresh Token Rotation",
            prNumber = 18,
            prSha = "9c4b2a8f",
            ciStatus = "Passed",
            defenseTranscriptSnippet = "Q: What happens when the token expires? A: The client interceptor catches the 401 and presents the HttpOnly refresh token to /api/v1/auth/refresh, triggering token rotation.",
            verifiedDate = "Oct 06, 2026"
        ),
        SkillEvidenceItem(
            id = "ev_02",
            skillName = "REST API Architecture",
            state = EvidenceState.DEMONSTRATED,
            projectName = "Autonomous Campus Placement Engine",
            taskNumber = 8,
            taskTitle = "Design Standardized Error Handling & RFC 7807 Problem Details",
            prNumber = 14,
            prSha = "4d8e1f0a",
            ciStatus = "Passed",
            defenseTranscriptSnippet = "Q: Why RFC 7807 over custom json? A: Standardized error parsing across diverse mobile and web frontends with machine-readable URI type descriptors.",
            verifiedDate = "Sep 28, 2026"
        ),
        SkillEvidenceItem(
            id = "ev_03",
            skillName = "PostgreSQL & Database Design",
            state = EvidenceState.OBSERVED,
            projectName = "Autonomous Campus Placement Engine",
            taskNumber = 13,
            taskTitle = "Database Migration & Placement Drive Schema Design",
            prNumber = 21,
            prSha = "7a2b9e31",
            ciStatus = "Passed",
            defenseTranscriptSnippet = "Migration scripts pushed and validated on CI test container; Micro-Viva defense scheduled.",
            verifiedDate = "Pending Defense"
        ),
        SkillEvidenceItem(
            id = "ev_04",
            skillName = "Docker & Containerization",
            state = EvidenceState.PRACTICED,
            projectName = "Microservices Practice Sandbox",
            taskNumber = 4,
            taskTitle = "Multi-stage Dockerfile Optimization for JVM microservice",
            prNumber = 7,
            prSha = "1c3f5d90",
            ciStatus = "Passed",
            defenseTranscriptSnippet = "Reduced production container image size from 650MB to 110MB using Eclipse Temurin JRE alpine.",
            verifiedDate = "Sep 15, 2026"
        )
    )

    val sampleBackendReadiness = JobReadinessMetric(
        roleName = "Backend Engineer",
        score = 78,
        strengths = listOf("Authentication & JWT (Demonstrated)", "REST API Architecture (Demonstrated)", "Relational Schema Design (Observed)"),
        gaps = listOf("Docker & Containerization (Practiced - needs defense)", "Message Queues / Kafka (Not yet attempted)", "Distributed Caching (Developing)"),
        nextAction = "Complete Micro-Viva defense on Task #12 to elevate Authentication to Repeatedly Demonstrated."
    )

    val sampleIdeaRecommendations = listOf(
        IdeaRecommendation(
            id = "idea_1",
            title = "High-Throughput Campus Placement Engine",
            problem = "Campus placement offices struggle with manual Excel sheets, missed eligibility criteria, fraud applications, and unverified student claims.",
            whyItFits = "Directly develops your PostgreSQL concurrency, state machine modeling, and zero-trust authentication skills.",
            difficulty = "Intermediate (Engineering Grade)",
            duration = "6 Weeks (4-5 hrs/week)",
            technologies = listOf("Kotlin", "PostgreSQL", "JWT", "Docker", "Redis"),
            skillsGained = listOf("Authentication", "ACID Transactions", "REST APIs", "CI/CD"),
            engineeringChallenges = listOf("Handling spike traffic during drive launch", "Idempotent state transitions"),
            evidenceOpportunities = listOf("PR reviews on auth middleware", "Micro-Viva on token revocation", "CI test suites")
        ),
        IdeaRecommendation(
            id = "idea_2",
            title = "Distributed Log Aggregator & Alerting Daemon",
            problem = "Small engineering teams lack lightweight log pipelines that operate reliably without full ELK stack overhead.",
            whyItFits = "Targets high-performance streaming, socket handling, and indexing fundamentals for systems engineering.",
            difficulty = "Advanced",
            duration = "8 Weeks",
            technologies = listOf("Go/Kotlin", "gRPC", "TimescaleDB", "Prometheus"),
            skillsGained = listOf("gRPC", "Time-series storage", "Concurrency", "Monitoring"),
            engineeringChallenges = listOf("Backpressure handling during network partition", "Zero-alloc JSON parsing"),
            evidenceOpportunities = listOf("Benchmark defense transcript", "Race condition analysis PR")
        ),
        IdeaRecommendation(
            id = "idea_3",
            title = "Micro-Fintech Idempotent Ledger Service",
            problem = "Double-charge anomalies and distributed split-brain inconsistencies in payment processing.",
            whyItFits = "Best project for high-paying fintech backend roles; demonstrates deep understanding of distributed transactions.",
            difficulty = "Advanced",
            duration = "5 Weeks",
            technologies = listOf("PostgreSQL", "Redis Locks", "REST APIs", "Docker"),
            skillsGained = listOf("Distributed Locks", "Two-Phase Commit", "Idempotency Keys"),
            engineeringChallenges = listOf("Simulated network latency during commit", "Optimistic concurrency locking"),
            evidenceOpportunities = listOf("Jepsen-style automated concurrency test evidence")
        )
    )

    val sampleCourses = listOf(
        CourseModel(
            id = "c_1",
            title = "Hardened Backend Engineering & Identity Systems",
            level = "Intermediate",
            duration = "14 Hours",
            progressPercent = 65,
            skills = listOf("Authentication", "JWT", "Cryptography", "Security"),
            currentLessonTitle = "Token Revocation Strategies: Blacklists vs Short Expiries",
            connectedTask = "Task #12: Implement JWT Authentication"
        ),
        CourseModel(
            id = "c_2",
            title = "PostgreSQL Concurrency, Indexing & Query Tuning",
            level = "Advanced",
            duration = "18 Hours",
            progressPercent = 40,
            skills = listOf("PostgreSQL", "Query Plans", "Transactions", "Locks"),
            currentLessonTitle = "MVCC Mechanics and Isolation Levels in High-Concurrency",
            connectedTask = "Task #13: Database Migration & Schema Design"
        ),
        CourseModel(
            id = "c_3",
            title = "Distributed Messaging Semantics with BullMQ & Kafka",
            level = "Intermediate",
            duration = "12 Hours",
            progressPercent = 10,
            skills = listOf("Message Queues", "Async Workers", "Event Driven"),
            currentLessonTitle = "Consumer Groups & Offset Commit Guarantees",
            connectedTask = "Task #14: Asynchronous Resume Parsing Worker"
        )
    )

    val samplePracticeProblems = listOf(
        PracticeProblem(
            id = "p_1",
            title = "Defend Against JWT alg:none Signature Bypass",
            type = "Security Code Review",
            difficulty = "Medium",
            objective = "Identify vulnerability where malicious client alters header to 'alg: none' and bypasses cryptographic signature verification.",
            question = "Review this JWT parsing middleware snippet. Explain the flaw in line 14 and supply the strict algorithm whitelist check.",
            initialCodeOrScenario = "fun verifyToken(jwt: String): Boolean {\n  val header = parseHeader(jwt)\n  if (header.alg == \"none\") return true // VULNERABILITY\n  return cryptoVerify(jwt, secretKey)\n}",
            expectedOutcome = "Enforce explicit rejection of any algorithm not in the approved server whitelist (e.g., HS256 / RS256).",
            hints = listOf(
                "Never trust client-specified algorithm header blindly.",
                "Enforce strict server-side configured algorithm checking before verifying signature payload."
            )
        ),
        PracticeProblem(
            id = "p_2",
            title = "Resolving Deadlocks in Concurrent Application Submissions",
            type = "Architecture & Concurrency",
            difficulty = "Hard",
            objective = "Prevent PostgreSQL lock order deadlocks when two concurrent threads update student quotas and application counts in inverted sequence.",
            question = "When thread A locks Student row then Job row, and thread B locks Job row then Student row, a 40P01 deadlock occurs. How do you re-architect the transaction order?",
            initialCodeOrScenario = "Tx1: SELECT FOR UPDATE students WHERE id = 10; ... SELECT FOR UPDATE jobs WHERE id = 50;\nTx2: SELECT FOR UPDATE jobs WHERE id = 50; ... SELECT FOR UPDATE students WHERE id = 10;",
            expectedOutcome = "Enforce global lexicographical resource ordering (e.g., always lock Student before Job, or acquire advisory locks based on sorted entity IDs).",
            hints = listOf(
                "Consistent resource acquisition ordering eliminates circular wait conditions.",
                "Look into SELECT ... FOR UPDATE with deterministic sorting."
            )
        )
    )

    val sampleDefenseQuestions = listOf(
        MicroVivaTurn(
            question = "Why did you choose HMAC-SHA256 (symmetric) over RSA-256 (asymmetric) for your JWT implementation in Task #12?",
            studentAnswer = null,
            followUp = "What are the key security and infrastructure trade-offs when scaling your auth service to multiple microservices?",
            isAiEvaluated = false,
            feedback = null
        ),
        MicroVivaTurn(
            question = "How does your implementation handle a user whose session was revoked prior to the 15-minute access token expiry?",
            studentAnswer = null,
            followUp = "Show exactly where in your PR #18 review the middleware checks the revoked token blacklist.",
            isAiEvaluated = false,
            feedback = null
        )
    )

    val initialChatMessages = listOf(
        ChatMessage(
            id = "msg_1",
            sender = "sisya",
            content = "Welcome to your engineering practice space, Anuvardhan. I see you are working on Task #12: JWT Authentication for your Campus Placement Engine. How can I help you reason through the architecture today?",
            timestamp = "10:14 AM",
            contextTag = "Task #12",
            trustBadge = "SOCRATIC GUIDANCE"
        ),
        ChatMessage(
            id = "msg_2",
            sender = "user",
            content = "I'm setting up token expiration. Should I make the access token valid for 7 days so users don't have to keep logging in?",
            timestamp = "10:15 AM"
        ),
        ChatMessage(
            id = "msg_3",
            sender = "sisya",
            content = "Let's think through the security boundary. If an access token with a 7-day lifetime is intercepted or leaked from client memory, how can your server invalidate it before the 7 days expire without querying a stateful database on every single API request?\n\nWhat happens to the advantage of stateless JWT if you have to track every active token for a whole week?",
            timestamp = "10:16 AM",
            contextTag = "Task #12: JWT",
            trustBadge = "SOCRATIC GUIDANCE"
        ),
        ChatMessage(
            id = "msg_4",
            sender = "mentor",
            content = "Contextual Review on PR #18:\nFACT: PR #18 adds `JwtAuthFilter.kt` with a 15-minute access token and separate `/refresh` endpoint.\nOBSERVATION: Good separation of concerns. However, ensure the refresh token is stored in an HttpOnly, Secure, SameSite=Strict cookie rather than local storage to prevent XSS exfiltration.",
            timestamp = "10:20 AM",
            contextTag = "PR #18",
            trustBadge = "FACT & REVIEW"
        )
    )
}
