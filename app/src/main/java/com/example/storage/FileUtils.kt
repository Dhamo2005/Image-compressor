package com.example.storage

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.text.DecimalFormat

object FileUtils {

    private val SUPPORTED_EXTENSIONS = setOf("jpg", "jpeg", "png", "bmp", "gif", "webp", "tiff", "tif")

    fun isSupportedImage(fileName: String): Boolean {
        val ext = getExtension(fileName).lowercase()
        return SUPPORTED_EXTENSIONS.contains(ext)
    }

    fun getExtension(fileName: String): String {
        val lastDot = fileName.lastIndexOf('.')
        return if (lastDot != -1 && lastDot < fileName.length - 1) {
            fileName.substring(lastDot + 1)
        } else ""
    }

    fun getFileNameWithoutExtension(fileName: String): String {
        val lastDot = fileName.lastIndexOf('.')
        return if (lastDot != -1) {
            fileName.substring(0, lastDot)
        } else fileName
    }

    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
        val dec = DecimalFormat("#,##0.#")
        return "${dec.format(bytes / Math.pow(1024.0, digitGroups.toDouble()))} ${units[digitGroups]}"
    }

    fun formatDuration(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours = (millis / (1000 * 60 * 60))
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    fun getMimeTypeForExtension(ext: String): String {
        return when (ext.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            "gif" -> "image/gif"
            "bmp" -> "image/x-ms-bmp"
            "tiff", "tif" -> "image/tiff"
            else -> "image/jpeg"
        }
    }

    fun getDisplayNameFromUri(context: Context, uri: Uri): String {
        if (uri.scheme == "content") {
            try {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1) {
                            return cursor.getString(nameIndex) ?: uri.lastPathSegment ?: "Folder"
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore fallback to path segment
            }
        }
        val path = uri.path ?: return "Folder"
        val lastSlash = path.lastIndexOf('/')
        return if (lastSlash != -1 && lastSlash < path.length - 1) {
            path.substring(lastSlash + 1)
        } else path
    }
}
