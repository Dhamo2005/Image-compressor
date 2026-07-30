package com.example.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

data class StudentFolderItem(
    val studentName: String,
    val folderDocument: DocumentFile,
    val imageFiles: List<DocumentFile>
)

object StorageHelper {

    fun scanStudentFolders(context: Context, treeUri: Uri): List<StudentFolderItem> {
        val rootDir = DocumentFile.fromTreeUri(context, treeUri) ?: return emptyList()
        val studentFolders = mutableListOf<StudentFolderItem>()

        val files = rootDir.listFiles()
        for (file in files) {
            if (file.isDirectory) {
                val studentName = file.name ?: "Unknown"
                val images = mutableListOf<DocumentFile>()
                for (child in file.listFiles()) {
                    if (!child.isDirectory && child.name != null && FileUtils.isSupportedImage(child.name!!)) {
                        images.add(child)
                    }
                }
                if (images.isNotEmpty()) {
                    studentFolders.add(StudentFolderItem(studentName, file, images))
                }
            } else if (!file.isDirectory && file.name != null && FileUtils.isSupportedImage(file.name!!)) {
                // If images are directly in root directory, group under "General"
                val studentName = "General"
                val existing = studentFolders.find { it.studentName == studentName }
                if (existing != null) {
                    (existing.imageFiles as MutableList).add(file)
                } else {
                    studentFolders.add(StudentFolderItem(studentName, rootDir, mutableListOf(file)))
                }
            }
        }
        return studentFolders.sortedBy { it.studentName }
    }

    fun getOrCreateStudentFolder(context: Context, destTreeUri: Uri, studentName: String): DocumentFile? {
        val rootDir = DocumentFile.fromTreeUri(context, destTreeUri) ?: return null
        val existing = rootDir.findFile(studentName)
        if (existing != null && existing.isDirectory) {
            return existing
        }
        return rootDir.createDirectory(studentName)
    }

    fun createOutputFile(
        context: Context,
        studentFolder: DocumentFile,
        outputFileName: String,
        mimeType: String
    ): DocumentFile? {
        // Delete existing file if exists so we overwrite cleanly
        val existing = studentFolder.findFile(outputFileName)
        existing?.delete()
        return studentFolder.createFile(mimeType, outputFileName)
    }

    fun openInputStream(context: Context, uri: Uri): InputStream? {
        return try {
            context.contentResolver.openInputStream(uri)
        } catch (e: Exception) {
            null
        }
    }

    fun openOutputStream(context: Context, uri: Uri): OutputStream? {
        return try {
            context.contentResolver.openOutputStream(uri)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Generates sample student folders on local app storage for quick 1-click test/demo!
     */
    fun createSampleStudentFolders(context: Context): Uri {
        val sampleDir = File(context.getExternalFilesDir(null), "SampleStudents")
        if (!sampleDir.exists()) {
            sampleDir.mkdirs()
        }

        val students = mapOf(
            "Kaviya" to listOf("Marksheet.jpg", "Aadhar.png", "Signature.jpeg", "Typewriting.webp"),
            "Dhamo" to listOf("Marksheet.png", "Aadhar.jpg", "Signature.png", "Typewriting.jpg"),
            "Arun" to listOf("Marksheet.jpg", "Aadhar.png", "CommunityCertificate.jpg"),
            "Priya" to listOf("Marksheet.jpg", "Aadhar.jpg", "IncomeCertificate.png")
        )

        val colors = intArrayOf(
            Color.parseColor("#4F46E5"),
            Color.parseColor("#0EA5E9"),
            Color.parseColor("#10B981"),
            Color.parseColor("#F59E0B")
        )

        students.forEach { (student, docs) ->
            val sDir = File(sampleDir, student)
            if (!sDir.exists()) sDir.mkdirs()

            docs.forEachIndexed { index, docName ->
                val file = File(sDir, docName)
                if (!file.exists()) {
                    val bitmap = Bitmap.createBitmap(1600, 1200, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    canvas.drawColor(colors[index % colors.size])

                    val paint = Paint().apply {
                        color = Color.WHITE
                        textSize = 64f
                        isAntiAlias = true
                        textAlign = Paint.Align.CENTER
                    }

                    val paintSub = Paint().apply {
                        color = Color.YELLOW
                        textSize = 40f
                        isAntiAlias = true
                        textAlign = Paint.Align.CENTER
                    }

                    canvas.drawText("STUDENT DOCUMENT", 800f, 400f, paint)
                    canvas.drawText("Student: $student", 800f, 550f, paint)
                    canvas.drawText("File: $docName", 800f, 680f, paintSub)
                    canvas.drawText("High Resolution Original Image Sample", 800f, 820f, paintSub)

                    FileOutputStream(file).use { out ->
                        val ext = FileUtils.getExtension(docName).lowercase()
                        when (ext) {
                            "png" -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                            "webp" -> bitmap.compress(Bitmap.CompressFormat.WEBP, 100, out)
                            else -> bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                        }
                    }
                    bitmap.recycle()
                }
            }
        }

        return Uri.fromFile(sampleDir)
    }
}
