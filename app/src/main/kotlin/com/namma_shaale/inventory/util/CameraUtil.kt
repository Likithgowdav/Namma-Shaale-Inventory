package com.namma_shaale.inventory.util

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object CameraUtil {
    fun saveBitmapToFile(context: Context, bitmap: Bitmap): String? {
        return try {
            val filesDir = context.getExternalFilesDir("asset_photos")
            if (filesDir != null && !filesDir.exists()) {
                filesDir.mkdirs()
            }

            val fileName = "AST_${UUID.randomUUID()}.jpg"
            val file = File(filesDir, fileName)
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos)
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    fun createImageFileUri(context: Context): Result<Pair<android.net.Uri, String>> {
        return try {
            val resolver = context.contentResolver
            val contentValues = android.content.ContentValues().apply {
                put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, "AST_${UUID.randomUUID()}.jpg")
                put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES + "/NammaShaale")
                }
            }
            
            val uri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                Result.success(Pair(uri, uri.toString()))
            } else {
                createCacheFileUri(context)
            }
        } catch (e: Exception) {
            try {
                createCacheFileUri(context)
            } catch (e2: Exception) {
                android.util.Log.e("CameraUtil", "Error creating URI", e2)
                Result.failure(e2)
            }
        }
    }

    private fun createCacheFileUri(context: Context): Result<Pair<android.net.Uri, String>> {
        val fileName = "AST_${UUID.randomUUID()}.jpg"
        val file = File(context.cacheDir, fileName)
        file.createNewFile()
        val uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "com.namma_shaale.inventory.fileprovider",
            file
        )
        return Result.success(Pair(uri, file.absolutePath))
    }

    fun copyUriToInternalStorage(context: Context, uri: android.net.Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val filesDir = context.getExternalFilesDir("asset_photos")
            if (filesDir != null && !filesDir.exists()) {
                filesDir.mkdirs()
            }
            val fileName = "AST_${UUID.randomUUID()}.jpg"
            val file = File(filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }


}
