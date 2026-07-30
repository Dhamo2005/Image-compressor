package com.example.compression

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import com.example.model.DocumentRule
import com.example.model.ImageSettings
import com.example.model.OutputFormat
import com.example.storage.FileUtils
import com.example.storage.StorageHelper
import java.io.ByteArrayOutputStream

data class CompressionResult(
    val compressedBytes: ByteArray,
    val originalSizeBytes: Long,
    val compressedSizeBytes: Long,
    val finalQuality: Int,
    val finalWidth: Int,
    val finalHeight: Int,
    val outputFormat: OutputFormat,
    val outputExtension: String,
    val targetSizeKb: Int,
    val ruleMatchedName: String?
)

object CompressionEngine {

    fun matchRuleForFile(fileName: String, rules: List<DocumentRule>): DocumentRule? {
        val cleanName = FileUtils.getFileNameWithoutExtension(fileName).lowercase()
        return rules.firstOrNull { rule ->
            rule.enabled && cleanName.contains(rule.name.lowercase())
        }
    }

    fun compressImage(
        context: Context,
        inputUri: Uri,
        fileName: String,
        rules: List<DocumentRule>,
        settings: ImageSettings
    ): CompressionResult {
        val originalInputStream = StorageHelper.openInputStream(context, inputUri)
        val originalSizeBytes = originalInputStream?.use { it.available().toLong() } ?: 0L

        val matchedRule = matchRuleForFile(fileName, rules)
        val targetSizeKb = matchedRule?.targetSizeKb ?: settings.defaultTargetSizeKb
        val targetSizeBytes = targetSizeKb * 1024L

        val outputFormat = matchedRule?.outputFormat ?: settings.defaultOutputFormat
        val origExt = FileUtils.getExtension(fileName)
        val finalExt = outputFormat.getExtension(origExt)

        val maxWidth = matchedRule?.maxWidth ?: settings.maxWidth
        val maxHeight = matchedRule?.maxHeight ?: settings.maxHeight
        val allowResize = matchedRule?.allowResize ?: settings.allowResize

        // Decode bitmap efficiently
        var bitmap = ImageDecoder.decodeBitmap(context, inputUri, maxWidth, maxHeight)
            ?: throw IllegalStateException("Could not decode bitmap from $inputUri")

        // Resize bitmap if larger than max constraints or if resizing allowed
        if (allowResize && (bitmap.width > maxWidth || bitmap.height > maxHeight)) {
            bitmap = scaleBitmap(bitmap, maxWidth, maxHeight, settings.maintainAspectRatio)
        }

        var currentBitmap = bitmap
        var quality = matchedRule?.maxQuality ?: settings.maxQuality
        val minQuality = matchedRule?.minQuality ?: settings.minQuality
        val maxAttempts = settings.maxAttempts

        var compressedBytes = ByteArray(0)
        var attempt = 0
        var achieved = false

        while (attempt < maxAttempts) {
            attempt++
            val baos = ByteArrayOutputStream()
            val compressFormat = getCompressFormat(outputFormat, origExt)

            currentBitmap.compress(compressFormat, quality, baos)
            compressedBytes = baos.toByteArray()

            if (compressedBytes.size <= targetSizeBytes || quality <= minQuality) {
                if (compressedBytes.size <= targetSizeBytes) {
                    achieved = true
                    break
                }
            }

            // If still exceeding target size and quality is near minQuality, downscale image
            if (quality <= minQuality + 5 && allowResize) {
                val scaleFactor = 0.85f
                val newW = (currentBitmap.width * scaleFactor).toInt()
                val newH = (currentBitmap.height * scaleFactor).toInt()
                if (newW > 200 && newH > 200) {
                    val scaled = Bitmap.createScaledBitmap(currentBitmap, newW, newH, true)
                    if (scaled != currentBitmap && currentBitmap != bitmap) {
                        currentBitmap.recycle()
                    }
                    currentBitmap = scaled
                    quality = (matchedRule?.maxQuality ?: settings.maxQuality) - 10
                    continue
                }
            }

            // Reduce quality in step
            quality -= ((quality - minQuality) / 3).coerceAtLeast(5)
        }

        val finalWidth = currentBitmap.width
        val finalHeight = currentBitmap.height

        // Recycle intermediate scaled bitmaps if needed
        if (currentBitmap != bitmap) {
            currentBitmap.recycle()
        }
        bitmap.recycle()

        return CompressionResult(
            compressedBytes = compressedBytes,
            originalSizeBytes = originalSizeBytes,
            compressedSizeBytes = compressedBytes.size.toLong(),
            finalQuality = quality,
            finalWidth = finalWidth,
            finalHeight = finalHeight,
            outputFormat = outputFormat,
            outputExtension = finalExt,
            targetSizeKb = targetSizeKb,
            ruleMatchedName = matchedRule?.name
        )
    }

    private fun scaleBitmap(
        src: Bitmap,
        maxWidth: Int,
        maxHeight: Int,
        maintainAspect: Boolean
    ): Bitmap {
        val width = src.width
        val height = src.height

        if (width <= maxWidth && height <= maxHeight) return src

        val newWidth: Int
        val newHeight: Int

        if (maintainAspect) {
            val ratio = width.toFloat() / height.toFloat()
            if (width > height) {
                newWidth = maxWidth
                newHeight = (maxWidth / ratio).toInt()
            } else {
                newHeight = maxHeight
                newWidth = (maxHeight * ratio).toInt()
            }
        } else {
            newWidth = maxWidth
            newHeight = maxHeight
        }

        return Bitmap.createScaledBitmap(src, newWidth, newHeight, true)
    }

    private fun getCompressFormat(format: OutputFormat, originalExtension: String): Bitmap.CompressFormat {
        return when (format) {
            OutputFormat.JPEG -> Bitmap.CompressFormat.JPEG
            OutputFormat.PNG -> Bitmap.CompressFormat.PNG
            OutputFormat.WEBP -> Bitmap.CompressFormat.WEBP
            OutputFormat.KEEP_ORIGINAL -> {
                when (originalExtension.lowercase()) {
                    "png" -> Bitmap.CompressFormat.PNG
                    "webp" -> Bitmap.CompressFormat.WEBP
                    else -> Bitmap.CompressFormat.JPEG
                }
            }
        }
    }
}
