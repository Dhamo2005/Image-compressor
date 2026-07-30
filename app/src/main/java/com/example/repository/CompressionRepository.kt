package com.example.repository

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.database.CompressionJobDao
import com.example.database.LogDao
import com.example.model.CompressionJob
import com.example.model.DashboardStats
import com.example.model.JobStatus
import com.example.model.LogItem
import com.example.workers.BatchCompressionWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.UUID

class CompressionRepository(
    private val context: Context,
    private val jobDao: CompressionJobDao,
    private val logDao: LogDao
) {
    val allJobs: Flow<List<CompressionJob>> = jobDao.getAllJobs()
    val allLogs: Flow<List<LogItem>> = logDao.getAllLogs()
    fun getLogsForJob(jobId: String): Flow<List<LogItem>> = logDao.getLogsForJob(jobId)

    val dashboardStats: Flow<DashboardStats> = allJobs.map { jobs ->
        val latestJob = jobs.maxByOrNull { it.startTime }
        val totalStudents = latestJob?.totalStudents ?: 0
        val totalImages = latestJob?.totalImages ?: 0
        val processed = latestJob?.processedImages ?: 0
        val failed = latestJob?.failedImages ?: 0
        val skipped = latestJob?.skippedImages ?: 0
        val origBytes = latestJob?.totalOriginalSizeBytes ?: 0L
        val compBytes = latestJob?.totalCompressedSizeBytes ?: 0L

        val spaceSaved = (origBytes - compBytes).coerceAtLeast(0L)
        val avgCompression = if (origBytes > 0) {
            ((spaceSaved.toFloat() / origBytes.toFloat()) * 100f)
        } else 0f

        DashboardStats(
            totalStudents = totalStudents,
            totalImages = totalImages,
            processedImages = processed,
            failedImages = failed,
            skippedImages = skipped,
            originalSizeBytes = origBytes,
            compressedSizeBytes = compBytes,
            spaceSavedBytes = spaceSaved,
            averageCompressionPercentage = avgCompression
        )
    }

    suspend fun startBatchCompression(sourceUri: String, destUri: String): String {
        val jobId = UUID.randomUUID().toString()
        val newJob = CompressionJob(
            id = jobId,
            sourceUri = sourceUri,
            destinationUri = destUri,
            status = JobStatus.RUNNING,
            startTime = System.currentTimeMillis()
        )
        jobDao.insertJob(newJob)

        val workData = Data.Builder()
            .putString(BatchCompressionWorker.KEY_JOB_ID, jobId)
            .putString(BatchCompressionWorker.KEY_SOURCE_URI, sourceUri)
            .putString(BatchCompressionWorker.KEY_DEST_URI, destUri)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<BatchCompressionWorker>()
            .setInputData(workData)
            .addTag(jobId)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "batch_compression_$jobId",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        return jobId
    }

    suspend fun cancelJob(jobId: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(jobId)
        val job = jobDao.getJobById(jobId)
        if (job != null) {
            jobDao.updateJob(job.copy(status = JobStatus.CANCELLED, endTime = System.currentTimeMillis()))
        }
    }

    suspend fun clearLogs() = logDao.clearLogs()
    suspend fun clearHistory() {
        jobDao.deleteAllJobs()
        logDao.clearLogs()
    }

    fun observeJob(jobId: String): Flow<CompressionJob?> = jobDao.observeJobById(jobId)
}
