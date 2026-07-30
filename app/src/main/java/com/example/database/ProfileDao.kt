package com.example.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.model.CompressionProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM compression_profiles ORDER BY createdAt DESC")
    fun getAllProfiles(): Flow<List<CompressionProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: CompressionProfile)

    @Delete
    suspend fun deleteProfile(profile: CompressionProfile)

    @Query("DELETE FROM compression_profiles")
    suspend fun deleteAllProfiles()
}
