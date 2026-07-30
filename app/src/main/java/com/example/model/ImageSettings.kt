package com.example.model

data class ImageSettings(
    val minQuality: Int = 15,
    val maxQuality: Int = 95,
    val maxAttempts: Int = 10,
    val allowResize: Boolean = true,
    val maxDownscalePercentage: Int = 50,
    val resizeStepPercentage: Int = 10,
    val jpegOptimization: Boolean = true,
    val progressiveJpeg: Boolean = true,
    val removeMetadata: Boolean = false,
    val preserveIccProfile: Boolean = true,
    val defaultOutputFormat: OutputFormat = OutputFormat.JPEG,
    val defaultTargetSizeKb: Int = 300,
    val maxWidth: Int = 2048,
    val maxHeight: Int = 2048,
    val maintainAspectRatio: Boolean = true,
    val doNotUpscale: Boolean = true,
    val workerCount: Int = 2
)

data class DashboardStats(
    val totalStudents: Int = 0,
    val totalImages: Int = 0,
    val processedImages: Int = 0,
    val failedImages: Int = 0,
    val skippedImages: Int = 0,
    val originalSizeBytes: Long = 0,
    val compressedSizeBytes: Long = 0,
    val spaceSavedBytes: Long = 0,
    val averageCompressionPercentage: Float = 0f
)
