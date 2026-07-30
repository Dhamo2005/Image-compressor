package com.example.compression

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.example.storage.StorageHelper
import java.io.InputStream

object ImageDecoder {

    fun decodeBitmap(
        context: Context,
        uri: Uri,
        reqWidth: Int = 2048,
        reqHeight: Int = 2048
    ): Bitmap? {
        var inputStream: InputStream = StorageHelper.openInputStream(context, uri) ?: return null

        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(inputStream, null, options)
        try { inputStream.close() } catch (e: Exception) {}

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        options.inPreferredConfig = Bitmap.Config.ARGB_8888

        // Decode bitmap with inSampleSize set
        inputStream = StorageHelper.openInputStream(context, uri) ?: return null
        var bitmap = BitmapFactory.decodeStream(inputStream, null, options)
        try { inputStream.close() } catch (e: Exception) {}

        if (bitmap == null) return null

        // Apply EXIF rotation
        val rotation = getExifOrientationDegrees(context, uri)
        if (rotation != 0) {
            val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
            val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            if (rotated != bitmap) {
                bitmap.recycle()
                bitmap = rotated
            }
        }

        return bitmap
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun getExifOrientationDegrees(context: Context, uri: Uri): Int {
        var inputStream: InputStream? = null
        try {
            inputStream = StorageHelper.openInputStream(context, uri) ?: return 0
            val exif = ExifInterface(inputStream)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: Exception) {
            return 0
        } finally {
            try { inputStream?.close() } catch (e: Exception) {}
        }
    }
}
