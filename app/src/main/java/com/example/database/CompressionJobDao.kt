package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.CompressionJob
import kotlinx.coroutines.flow.Flow

@Dao
interface CompressionJobDao {
    @Query("SELECT * FROM compression_jobs ORDER BY startTime DESC")
    fun getAllJobs(): Flow<List<CompressionJob>>

    @Query("SELECT * FROM compression_jobs WHERE id = :id")
    suspend fun getJobById(id: String): CompressionJob?

    @Query("SELECT * FROM compression_jobs WHERE id = :id")
    fun observeJobById(id: String): Flow<CompressionJob?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: CompressionJob)

    @Update
    suspend fun updateJob(job: CompressionJob)

    @Query("DELETE FROM compression_jobs")
    suspend fun deleteAllJobs()
}
