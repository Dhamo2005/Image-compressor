package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class OutputFormat {
    KEEP_ORIGINAL,
    JPEG,
    PNG,
    WEBP;

    fun getExtension(originalExtension: String): String {
        return when (this) {
            KEEP_ORIGINAL -> originalExtension.lowercase()
            JPEG -> "jpg"
            PNG -> "png"
            WEBP -> "webp"
        }
    }
}

@Entity(tableName = "document_rules")
data class DocumentRule(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String, // e.g., Marksheet, Aadhar, Signature, Typewriting
    val targetSizeKb: Int, // e.g., 296, 146
    val outputFormat: OutputFormat = OutputFormat.JPEG,
    val enabled: Boolean = true,
    val maxQuality: Int = 95,
    val minQuality: Int = 20,
    val allowResize: Boolean = true,
    val maxWidth: Int = 2048,
    val maxHeight: Int = 2048
)

@Entity(tableName = "compression_profiles")
data class CompressionProfile(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String, // e.g., College Admission, Employment, Passport, Government Documents
    val description: String,
    val rulesJson: String, // Serialized list of rules
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
