package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class LogType {
    SUCCESS,
    WARNING,
    ERROR,
    INFO
}

@Entity(tableName = "live_logs")
data class LogItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val jobId: String,
    val studentName: String,
    val fileName: String,
    val originalSize: Long,
    val compressedSize: Long,
    val logType: LogType,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class JobStatus {
    IDLE,
    RUNNING,
    PAUSED,
    COMPLETED,
    FAILED,
    CANCELLED
}

@Entity(tableName = "compression_jobs")
data class CompressionJob(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val sourceUri: String,
    val destinationUri: String,
    val status: JobStatus = JobStatus.IDLE,
    val totalStudents: Int = 0,
    val totalImages: Int = 0,
    val processedImages: Int = 0,
    val failedImages: Int = 0,
    val skippedImages: Int = 0,
    val totalOriginalSizeBytes: Long = 0,
    val totalCompressedSizeBytes: Long = 0,
    val startTime: Long = 0,
    val endTime: Long = 0
)
