package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.model.LogItem
import com.example.model.LogType
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {
    @Query("SELECT * FROM live_logs ORDER BY timestamp DESC LIMIT 200")
    fun getAllLogs(): Flow<List<LogItem>>

    @Query("SELECT * FROM live_logs WHERE jobId = :jobId ORDER BY timestamp DESC LIMIT 200")
    fun getLogsForJob(jobId: String): Flow<List<LogItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: LogItem)

    @Query("DELETE FROM live_logs")
    suspend fun clearLogs()

    @Query("SELECT * FROM live_logs WHERE studentName LIKE '%' || :query || '%' OR fileName LIKE '%' || :query || '%' ORDER BY timestamp DESC LIMIT 200")
    fun searchLogs(query: String): Flow<List<LogItem>>

    @Query("SELECT * FROM live_logs WHERE logType = :type ORDER BY timestamp DESC LIMIT 200")
    fun filterLogsByType(type: LogType): Flow<List<LogItem>>
}
