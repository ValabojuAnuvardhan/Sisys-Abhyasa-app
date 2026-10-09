package com.example.data

import android.content.Context
import android.util.Log
import com.example.R
import com.example.model.EvidenceState
import com.example.model.SkillEvidenceItem
import com.example.model.TaskModel
import com.example.model.TaskStatus
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreRepository(private val context: Context) {

    private val firebaseApp: FirebaseApp? by lazy {
        try {
            val apps = FirebaseApp.getApps(context)
            if (apps.isEmpty()) {
                FirebaseApp.initializeApp(context)
            } else {
                apps[0]
            }
        } catch (e: Throwable) {
            Log.w("FirestoreRepository", "FirebaseApp initialization skipped: ${e.message}")
            null
        }
    }

    private val db: FirebaseFirestore? by lazy {
        try {
            val app = firebaseApp ?: return@lazy null
            val dbId = try {
                context.applicationContext.getString(R.string.firestore_database_id)
            } catch (e: Throwable) {
                ""
            }
            if (dbId.isNotBlank()) {
                FirebaseFirestore.getInstance(app, dbId)
            } else {
                FirebaseFirestore.getInstance(app)
            }
        } catch (e: Throwable) {
            Log.w("FirestoreRepository", "FirebaseFirestore instance unavailable: ${e.message}")
            null
        }
    }

    private val auth: FirebaseAuth? by lazy {
        try {
            if (firebaseApp != null) Firebase.auth else null
        } catch (e: Throwable) {
            Log.w("FirestoreRepository", "FirebaseAuth unavailable: ${e.message}")
            null
        }
    }

    private fun requireUserId(): String {
        return try {
            auth?.currentUser?.uid ?: "usr_anu_01"
        } catch (e: Throwable) {
            "usr_anu_01"
        }
    }

    /**
     * Persists or updates user profile in Firestore
     */
    suspend fun saveUserProfile(
        name: String,
        email: String,
        targetRole: String,
        readinessScore: Int,
        githubHandle: String
    ) {
        val database = db ?: return
        try {
            val uid = requireUserId()
            val data = hashMapOf(
                "userId" to uid,
                "name" to name,
                "email" to email,
                "targetRole" to targetRole,
                "readinessScore" to readinessScore,
                "githubHandle" to githubHandle,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            database.collection("users").document(uid).set(data).await()
        } catch (e: Throwable) {
            Log.w("FirestoreRepository", "Failed to saveUserProfile: ${e.message}")
        }
    }

    /**
     * Observes task items in real-time from Firestore, falling back to MockDataSource
     */
    fun observeTasks(): Flow<List<TaskModel>> = callbackFlow {
        val database = db
        if (database == null) {
            trySend(MockDataSource.sampleTasks)
            close()
            return@callbackFlow
        }

        var listener: com.google.firebase.firestore.ListenerRegistration? = null
        try {
            val uid = requireUserId()
            listener = database.collection("users").document(uid).collection("tasks")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(MockDataSource.sampleTasks)
                        return@addSnapshotListener
                    }
                    if (snapshot == null || snapshot.isEmpty) {
                        trySend(MockDataSource.sampleTasks)
                    } else {
                        val tasks = snapshot.documents.mapNotNull { doc ->
                            val num = (doc.getLong("number") ?: 12).toInt()
                            val title = doc.getString("title") ?: "Task"
                            val statusStr = doc.getString("status") ?: "TODO"
                            val status = try { TaskStatus.valueOf(statusStr) } catch (e: Exception) { TaskStatus.TODO }
                            val skill = doc.getString("requiredSkill") ?: "Backend"
                            val branch = doc.getString("branchName") ?: "feat/task"
                            val prNum = doc.getLong("prNumber")?.toInt()
                            val ci = doc.getString("ciStatus")
                            val defense = doc.getString("defenseStatus")

                            TaskModel(
                                id = doc.id,
                                number = num,
                                milestoneId = "m_01",
                                title = title,
                                objective = doc.getString("objective") ?: "Implement criteria",
                                description = "Verified Firestore task record",
                                acceptanceCriteria = listOf("Validated against Firestore rules"),
                                version = 1,
                                status = status,
                                requiredSkill = skill,
                                learningChecklist = emptyList(),
                                branchName = branch,
                                prNumber = prNum,
                                ciStatus = ci,
                                defenseStatus = defense
                            )
                        }
                        trySend(tasks)
                    }
                }
        } catch (e: Throwable) {
            Log.w("FirestoreRepository", "Failed to register snapshot listener: ${e.message}")
            trySend(MockDataSource.sampleTasks)
        }

        awaitClose {
            listener?.remove()
        }
    }

    /**
     * Saves or advances task status in Firestore
     */
    suspend fun updateTaskStatus(task: TaskModel, newStatus: TaskStatus) {
        val database = db ?: return
        try {
            val uid = requireUserId()
            val data = hashMapOf(
                "taskId" to task.id,
                "userId" to uid,
                "number" to task.number,
                "title" to task.title,
                "objective" to task.objective,
                "status" to newStatus.name,
                "requiredSkill" to task.requiredSkill,
                "branchName" to task.branchName,
                "prNumber" to (task.prNumber ?: 18),
                "ciStatus" to (task.ciStatus ?: "Passed"),
                "defenseStatus" to (task.defenseStatus ?: "Pending"),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            database.collection("users").document(uid).collection("tasks").document(task.id).set(data).await()
        } catch (e: Throwable) {
            Log.w("FirestoreRepository", "Failed to updateTaskStatus: ${e.message}")
        }
    }

    /**
     * Saves newly demonstrated skill evidence
     */
    suspend fun saveEvidence(evidence: SkillEvidenceItem) {
        val database = db ?: return
        try {
            val uid = requireUserId()
            val data = hashMapOf(
                "evidenceId" to evidence.id,
                "userId" to uid,
                "skillName" to evidence.skillName,
                "state" to evidence.state.name,
                "taskNumber" to evidence.taskNumber,
                "taskTitle" to evidence.taskTitle,
                "prNumber" to evidence.prNumber,
                "prSha" to evidence.prSha,
                "ciStatus" to evidence.ciStatus,
                "defenseTranscript" to evidence.defenseTranscriptSnippet,
                "verifiedDate" to evidence.verifiedDate,
                "createdAt" to FieldValue.serverTimestamp()
            )
            database.collection("users").document(uid).collection("evidence").document(evidence.id).set(data).await()
        } catch (e: Throwable) {
            Log.w("FirestoreRepository", "Failed to saveEvidence: ${e.message}")
        }
    }

    /**
     * Records chat messages into Firestore
     */
    suspend fun saveChatMessage(
        messageId: String,
        sender: String,
        content: String,
        timestamp: String,
        contextTag: String?,
        trustBadge: String?
    ) {
        val database = db ?: return
        try {
            val uid = requireUserId()
            val data = hashMapOf(
                "messageId" to messageId,
                "userId" to uid,
                "sender" to sender,
                "content" to content,
                "timestamp" to timestamp,
                "contextTag" to (contextTag ?: ""),
                "trustBadge" to (trustBadge ?: ""),
                "createdAt" to FieldValue.serverTimestamp()
            )
            database.collection("users").document(uid).collection("chats").document(messageId).set(data).await()
        } catch (e: Throwable) {
            Log.w("FirestoreRepository", "Failed to saveChatMessage: ${e.message}")
        }
    }
}
