package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoEvidenceDao {

    @Query("SELECT * FROM project_evidence_photos WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getEvidencePhotosForProject(projectId: String): Flow<List<PhotoEvidenceEntity>>

    @Query("SELECT * FROM project_evidence_photos ORDER BY createdAt DESC")
    fun getAllEvidencePhotos(): Flow<List<PhotoEvidenceEntity>>

    @Query("SELECT * FROM project_evidence_photos WHERE id = :id")
    suspend fun getEvidenceById(id: String): PhotoEvidenceEntity?

    @Query("SELECT COUNT(*) FROM project_evidence_photos WHERE projectId = :projectId")
    fun getEvidenceCountForProject(projectId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidence(evidence: PhotoEvidenceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(evidenceList: List<PhotoEvidenceEntity>)

    @Update
    suspend fun updateEvidence(evidence: PhotoEvidenceEntity)

    @Delete
    suspend fun deleteEvidence(evidence: PhotoEvidenceEntity)

    @Query("DELETE FROM project_evidence_photos WHERE id = :id")
    suspend fun deleteById(id: String)
}
