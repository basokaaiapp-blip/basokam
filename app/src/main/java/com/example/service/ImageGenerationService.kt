package com.example.service

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.ai.GeminiApiClient
import com.example.model.ImageResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageGenerationService {

    suspend fun generateImage(
        context: Context,
        prompt: String,
        aspectRatio: String = "1:1",
        style: String = "Cinematic"
    ): Result<ImageResult> = withContext(Dispatchers.IO) {
        if (prompt.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("تکایە دەقێک بنووسە بۆ دروستکردنی وێنە."))
        }

        val enrichedPrompt = if (style.isNotBlank() && style != "None") {
            "$prompt, $style style, high quality, detailed, 4k"
        } else {
            "$prompt, high quality, highly detailed"
        }

        val apiResult = GeminiApiClient.generateImage(
            prompt = enrichedPrompt,
            aspectRatio = aspectRatio
        )

        apiResult.fold(
            onSuccess = { bytes ->
                try {
                    val imagesDir = File(context.filesDir, "generated_images").apply { mkdirs() }
                    val imageId = UUID.randomUUID().toString().take(8)
                    val imageFile = File(imagesDir, "basoka_img_$imageId.jpg")

                    FileOutputStream(imageFile).use { fos ->
                        fos.write(bytes)
                        fos.flush()
                    }

                    val uri = try {
                        FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            imageFile
                        )
                    } catch (_: Exception) {
                        Uri.fromFile(imageFile)
                    }

                    Result.success(
                        ImageResult(
                            id = imageId,
                            prompt = prompt,
                            localFilePath = imageFile.absolutePath,
                            uri = uri,
                            aspectRatio = aspectRatio,
                            style = style,
                            modelUsed = "Imagen 3"
                        )
                    )
                } catch (e: Exception) {
                    Result.failure(Exception("هەڵە لە پاشەکەوتکردنی وێنەکە لە یادگەدا: ${e.localizedMessage}"))
                }
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }

    fun shareImage(context: Context, imageResult: ImageResult) {
        try {
            val file = File(imageResult.localFilePath)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "وێنەی دروستکراو بە Basoka AI: ${imageResult.prompt}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "هاوبەشکردنی وێنە").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun saveToGallery(context: Context, imageResult: ImageResult): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(imageResult.localFilePath)
            if (!file.exists()) return@withContext false

            val filename = "Basoka_${System.currentTimeMillis()}.jpg"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/BasokaAI")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    ?: return@withContext false

                context.contentResolver.openOutputStream(uri)?.use { out ->
                    file.inputStream().use { input -> input.copyTo(out) }
                }

                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                context.contentResolver.update(uri, values, null, null)
                true
            } else {
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val appDir = File(picturesDir, "BasokaAI").apply { mkdirs() }
                val destFile = File(appDir, filename)
                file.copyTo(destFile, overwrite = true)
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
