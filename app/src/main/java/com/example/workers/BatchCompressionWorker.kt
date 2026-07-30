package com.example.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.compression.CompressionEngine
import com.example.database.AppDatabase
import com.example.model.CompressionJob
import com.example.model.JobStatus
import com.example.model.LogItem
import com.example.model.LogType
import com.example.preferences.AppPreferences
import com.example.storage.FileUtils
import com.example.storage.StorageHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.OutputStream

class BatchCompressionWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_JOB_ID = "job_id"
        const val KEY_SOURCE_URI = "source_uri"
        const val KEY_DEST_URI = "dest_uri"
        const val NOTIFICATION_CHANNEL_ID = "compression_worker_channel"
        const val NOTIFICATION_ID = 1001
    }

    private val db = AppDatabase.getDatabase(context)
    private val jobDao = db.compressionJobDao()
    private val logDao = db.logDao()
    private val ruleDao = db.documentRuleDao()
    private val prefs = AppPreferences(context)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val jobId = inputData.getString(KEY_JOB_ID) ?: return@withContext Result.failure()
        val sourceUriStr = inputData.getString(KEY_SOURCE_URI) ?: return@withContext Result.failure()
        val destUriStr = inputData.getString(KEY_DEST_URI) ?: return@withContext Result.failure()

        val sourceUri = Uri.parse(sourceUriStr)
        val destUri = Uri.parse(destUriStr)

        createNotificationChannel()
        setForeground(createForegroundInfo("Preparing batch compression...", 0, 100))

        val rules = ruleDao.getEnabledRulesList()
        val settings = prefs.imageSettingsFlow.first()

        // Scan student folders
        val studentFolders = StorageHelper.scanStudentFolders(context, sourceUri)
        if (studentFolders.isEmpty()) {
            val job = jobDao.getJobById(jobId) ?: CompressionJob(id = jobId, sourceUri = sourceUriStr, destinationUri = destUriStr)
            jobDao.updateJob(job.copy(status = JobStatus.FAILED, endTime = System.currentTimeMillis()))
            logDao.insertLog(LogItem(jobId = jobId, studentName = "System", fileName = "-", originalSize = 0, compressedSize = 0, logType = LogType.ERROR, message = "No valid student folders or image files found in source directory."))
            return@withContext Result.failure()
        }

        val totalStudents = studentFolders.size
        var totalImages = 0
        studentFolders.forEach { totalImages += it.imageFiles.size }

        var currentJob = (jobDao.getJobById(jobId) ?: CompressionJob(id = jobId, sourceUri = sourceUriStr, destinationUri = destUriStr))
            .copy(
                status = JobStatus.RUNNING,
                totalStudents = totalStudents,
                totalImages = totalImages,
                startTime = System.currentTimeMillis()
            )
        jobDao.insertJob(currentJob)

        var processedCount = 0
        var failedCount = 0
        var skippedCount = 0
        var totalOrigBytes = 0L
        var totalCompBytes = 0L

        for ((studentIndex, studentItem) in studentFolders.withIndex()) {
            if (isStopped) {
                jobDao.updateJob(currentJob.copy(status = JobStatus.CANCELLED, endTime = System.currentTimeMillis()))
                return@withContext Result.failure()
            }

            val studentFolderDoc = StorageHelper.getOrCreateStudentFolder(context, destUri, studentItem.studentName)
            if (studentFolderDoc == null) {
                logDao.insertLog(LogItem(jobId = jobId, studentName = studentItem.studentName, fileName = "-", originalSize = 0, compressedSize = 0, logType = LogType.ERROR, message = "Failed to create output folder for ${studentItem.studentName}"))
                failedCount += studentItem.imageFiles.size
                continue
            }

            for (imageDoc in studentItem.imageFiles) {
                if (isStopped) {
                    jobDao.updateJob(currentJob.copy(status = JobStatus.CANCELLED, endTime = System.currentTimeMillis()))
                    return@withContext Result.failure()
                }

                val fileName = imageDoc.name ?: "image.jpg"
                val progressPct = ((processedCount + failedCount + skippedCount) * 100) / totalImages.coerceAtLeast(1)

                val notifText = "${studentItem.studentName} / $fileName ($progressPct%)"
                setForeground(createForegroundInfo(notifText, progressPct, 100))

                try {
                    val result = CompressionEngine.compressImage(
                        context = context,
                        inputUri = imageDoc.uri,
                        fileName = fileName,
                        rules = rules,
                        settings = settings
                    )

                    val nameWithoutExt = FileUtils.getFileNameWithoutExtension(fileName)
                    val outputFileName = "$nameWithoutExt.${result.outputExtension}"
                    val mimeType = FileUtils.getMimeTypeForExtension(result.outputExtension)

                    val outFile = StorageHelper.createOutputFile(context, studentFolderDoc, outputFileName, mimeType)
                    if (outFile != null) {
                        var outStream: OutputStream? = null
                        try {
                            outStream = StorageHelper.openOutputStream(context, outFile.uri)
                            outStream?.write(result.compressedBytes)
                            outStream?.flush()

                            processedCount++
                            totalOrigBytes += result.originalSizeBytes
                            totalCompBytes += result.compressedSizeBytes

                            val origStr = FileUtils.formatFileSize(result.originalSizeBytes)
                            val compStr = FileUtils.formatFileSize(result.compressedSizeBytes)
                            val ruleMatchedStr = result.ruleMatchedName?.let { " (Rule: $it)" } ?: ""

                            logDao.insertLog(
                                LogItem(
                                    jobId = jobId,
                                    studentName = studentItem.studentName,
                                    fileName = fileName,
                                    originalSize = result.originalSizeBytes,
                                    compressedSize = result.compressedSizeBytes,
                                    logType = LogType.SUCCESS,
                                    message = "✔ ${studentItem.studentName}/$fileName: $origStr → $compStr $ruleMatchedStr"
                                )
                            )
                        } catch (e: Exception) {
                            failedCount++
                            logDao.insertLog(
                                LogItem(
                                    jobId = jobId,
                                    studentName = studentItem.studentName,
                                    fileName = fileName,
                                    originalSize = 0,
                                    compressedSize = 0,
                                    logType = LogType.ERROR,
                                    message = "❌ ${studentItem.studentName}/$fileName: Failed to write output file: ${e.localizedMessage}"
                                )
                            )
                        } finally {
                            try { outStream?.close() } catch (e: Exception) {}
                        }
                    } else {
                        failedCount++
                        logDao.insertLog(
                            LogItem(
                                jobId = jobId,
                                studentName = studentItem.studentName,
                                fileName = fileName,
                                originalSize = 0,
                                compressedSize = 0,
                                logType = LogType.ERROR,
                                message = "❌ ${studentItem.studentName}/$fileName: Failed to create output file"
                            )
                        )
                    }
                } catch (e: Exception) {
                    failedCount++
                    logDao.insertLog(
                        LogItem(
                            jobId = jobId,
                            studentName = studentItem.studentName,
                            fileName = fileName,
                            originalSize = 0,
                            compressedSize = 0,
                            logType = LogType.ERROR,
                            message = "❌ ${studentItem.studentName}/$fileName: ${e.localizedMessage}"
                        )
                    )
                }

                // Update job in Room DB reactively
                currentJob = currentJob.copy(
                    processedImages = processedCount,
                    failedImages = failedCount,
                    skippedImages = skippedCount,
                    totalOriginalSizeBytes = totalOrigBytes,
                    totalCompressedSizeBytes = totalCompBytes
                )
                jobDao.updateJob(currentJob)
                setProgress(workDataOf("progress" to progressPct))
            }
        }

        currentJob = currentJob.copy(
            status = JobStatus.COMPLETED,
            endTime = System.currentTimeMillis()
        )
        jobDao.updateJob(currentJob)

        logDao.insertLog(
            LogItem(
                jobId = jobId,
                studentName = "System",
                fileName = "Batch Summary",
                originalSize = totalOrigBytes,
                compressedSize = totalCompBytes,
                logType = LogType.INFO,
                message = "🎉 Compression Complete! Processed: $processedCount, Failed: $failedCount, Total Saved: ${FileUtils.formatFileSize(totalOrigBytes - totalCompBytes)}"
            )
        )

        return@withContext Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Image Compressor Worker",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows real-time progress for batch student document image compression"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createForegroundInfo(contentText: String, progress: Int, max: Int): ForegroundInfo {
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Image Compressor Pro")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_menu_save)
            .setOngoing(true)
            .setProgress(max, progress, false)
            .build()
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                NOTIFICATION_ID, 
                notification, 
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(NOTIFICATION_ID, notification)
        }
    }
}
