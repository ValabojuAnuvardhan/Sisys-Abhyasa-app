package com.example.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PhotoStorageUtil {

    /**
     * Creates a temporary file in the cache directory and generates a content Uri
     * using the app's FileProvider to pass to ActivityResultContracts.TakePicture().
     */
    fun createTempPictureUri(context: Context): Uri {
        val storageDir = File(context.cacheDir, "evidence_photos").apply {
            if (!exists()) mkdirs()
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val tempFile = File.createTempFile("TEMP_EVIDENCE_${timeStamp}_", ".jpg", storageDir)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
    }

    /**
     * Persists the captured photo from a temporary Uri into permanent internal storage
     * so it remains accessible offline across app sessions.
     * Returns the permanent Uri string and local file path.
     */
    fun persistPhotoToInternalStorage(context: Context, sourceUri: Uri): Pair<String, String>? {
        return try {
            val permanentDir = File(context.filesDir, "evidence_photos").apply {
                if (!exists()) mkdirs()
            }
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val permanentFile = File(permanentDir, "PROOF_${timeStamp}_${System.currentTimeMillis()}.jpg")

            context.contentResolver.openInputStream(sourceUri)?.use { inputStream: InputStream ->
                FileOutputStream(permanentFile).use { outputStream: FileOutputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            val permanentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                permanentFile
            )

            Pair(permanentUri.toString(), permanentFile.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
