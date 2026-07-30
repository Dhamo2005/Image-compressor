package com.example

import com.example.storage.FileUtils
import com.example.model.DocumentRule
import com.example.model.OutputFormat
import org.junit.Assert.*
import org.junit.Test

class FileUtilsTest {

    @Test
    fun isSupportedImage_returnsTrueForSupported() {
        assertTrue(FileUtils.isSupportedImage("image.jpg"))
        assertTrue(FileUtils.isSupportedImage("photo.jpeg"))
        assertTrue(FileUtils.isSupportedImage("doc.PNG"))
        assertTrue(FileUtils.isSupportedImage("file.WEBP"))
    }

    @Test
    fun isSupportedImage_returnsFalseForUnsupported() {
        assertFalse(FileUtils.isSupportedImage("doc.pdf"))
        assertFalse(FileUtils.isSupportedImage("image.txt"))
        assertFalse(FileUtils.isSupportedImage("video.mp4"))
        assertFalse(FileUtils.isSupportedImage("no_extension"))
    }

    @Test
    fun getExtension_returnsCorrectExtension() {
        assertEquals("jpg", FileUtils.getExtension("image.jpg"))
        assertEquals("png", FileUtils.getExtension("folder.name/image.png"))
        assertEquals("", FileUtils.getExtension("no_extension"))
        assertEquals("jpeg", FileUtils.getExtension(".hidden.jpeg"))
    }

    @Test
    fun getFileNameWithoutExtension_returnsCorrectName() {
        assertEquals("image", FileUtils.getFileNameWithoutExtension("image.jpg"))
        assertEquals("folder.name/image", FileUtils.getFileNameWithoutExtension("folder.name/image.png"))
        assertEquals("no_extension", FileUtils.getFileNameWithoutExtension("no_extension"))
        assertEquals(".hidden", FileUtils.getFileNameWithoutExtension(".hidden.jpeg"))
    }

    @Test
    fun formatFileSize_returnsCorrectFormat() {
        assertEquals("0 B", FileUtils.formatFileSize(0))
        assertEquals("500 B", FileUtils.formatFileSize(500))
        assertEquals("1 KB", FileUtils.formatFileSize(1024))
        assertEquals("1.5 KB", FileUtils.formatFileSize(1536))
        assertEquals("1 MB", FileUtils.formatFileSize(1048576))
    }

    @Test
    fun formatDuration_returnsCorrectFormat() {
        assertEquals("00:00", FileUtils.formatDuration(0))
        assertEquals("00:15", FileUtils.formatDuration(15000))
        assertEquals("01:30", FileUtils.formatDuration(90000))
        assertEquals("01:00:00", FileUtils.formatDuration(3600000))
    }

    @Test
    fun getMimeTypeForExtension_returnsCorrectType() {
        assertEquals("image/jpeg", FileUtils.getMimeTypeForExtension("jpg"))
        assertEquals("image/jpeg", FileUtils.getMimeTypeForExtension("JPEG"))
        assertEquals("image/png", FileUtils.getMimeTypeForExtension("png"))
        assertEquals("image/webp", FileUtils.getMimeTypeForExtension("webp"))
        assertEquals("image/jpeg", FileUtils.getMimeTypeForExtension("unknown"))
    }
}

class DocumentRuleTest {
    @Test
    fun outputFormat_getExtension_returnsCorrect() {
        assertEquals("jpg", OutputFormat.JPEG.getExtension("png"))
        assertEquals("png", OutputFormat.PNG.getExtension("jpg"))
        assertEquals("webp", OutputFormat.WEBP.getExtension("jpg"))
        assertEquals("jpg", OutputFormat.KEEP_ORIGINAL.getExtension("JPG"))
        assertEquals("png", OutputFormat.KEEP_ORIGINAL.getExtension("png"))
    }
}
