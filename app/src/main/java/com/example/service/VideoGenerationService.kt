package com.example.service

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.core.content.FileProvider
import com.example.ai.GeminiApiClient
import com.example.model.VideoResult
import com.example.model.VideoStudioState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object VideoGenerationService {
    private const val TAG = "VideoGenService"

    suspend fun generateVideo(
        context: Context,
        prompt: String,
        aspectRatio: String = "16:9",
        durationSec: Int = 5,
        referenceImageBytes: ByteArray? = null,
        onStateChanged: (VideoStudioState) -> Unit
    ): Result<VideoResult> = withContext(Dispatchers.IO) {
        if (prompt.isBlank()) {
            val err = "تکایە دەقێک یان وەسفێک بۆ ڤیدیۆکە بنووسە."
            onStateChanged(VideoStudioState.Error(err, canRetry = false))
            return@withContext Result.failure(IllegalArgumentException(err))
        }

        onStateChanged(VideoStudioState.Generating("ڤیدیۆکە دروست دەکرێت..."))

        // 1. Start generation operation
        val startResult = GeminiApiClient.startVideoGeneration(
            prompt = prompt,
            aspectRatio = aspectRatio,
            referenceImageBytes = referenceImageBytes
        )

        if (startResult.isFailure) {
            val errorMsg = startResult.exceptionOrNull()?.message 
                ?: "دروستکردنی ڤیدیۆ سەرکەوتوو نەبوو. تکایە کلیلەکەت لە بەشی ڕێکخستن پشکنین بکە."
            onStateChanged(VideoStudioState.Error(errorMsg, canRetry = true))
            return@withContext Result.failure(Exception(errorMsg))
        }

        val operationName = startResult.getOrThrow()
        Log.d(TAG, "Video generation started with operation: $operationName")

        // 2. Poll operation until done
        var attempts = 0
        val maxAttempts = 35 // ~100-120 seconds
        var pollResponse = GeminiApiClient.pollVideoOperation(operationName)

        while (attempts < maxAttempts) {
            attempts++
            delay(3500)

            val currentProgress = 0.2f + (attempts.toFloat() / maxAttempts.toFloat()) * 0.6f
            onStateChanged(
                VideoStudioState.Processing(
                    progress = currentProgress,
                    messageKu = "خەریکی ئامادەکردنی ڤیدیۆکەین... (${(currentProgress * 100).toInt()}%)"
                )
            )

            pollResponse = GeminiApiClient.pollVideoOperation(operationName)
            if (pollResponse.isSuccess) {
                val data = pollResponse.getOrThrow()
                if (data.isDone) {
                    if (!data.errorMessage.isNullOrEmpty()) {
                        val errMsg = "هەڵە لە دروستکردنی ڤیدیۆ: ${data.errorMessage}"
                        onStateChanged(VideoStudioState.Error(errMsg, canRetry = true))
                        return@withContext Result.failure(Exception(errMsg))
                    }
                    break
                }
            } else {
                Log.w(TAG, "Poll attempt $attempts error: ${pollResponse.exceptionOrNull()?.message}")
            }
        }

        val finalData = pollResponse.getOrNull()
        if (finalData == null || !finalData.isDone) {
            val timeoutErr = "کاتی کارپێکردن تەواوبوو. خزمەتگوزاری نەیتوانی لە کاتی دیاریکراودا ڤیدیۆکە ئامادە بکات."
            onStateChanged(VideoStudioState.Error(timeoutErr, canRetry = true))
            return@withContext Result.failure(Exception(timeoutErr))
        }

        // 3. Download / write the video to local storage
        onStateChanged(VideoStudioState.Downloading(0.85f, "ڤیدیۆکە ئامادە دەکرێت..."))

        val videoId = UUID.randomUUID().toString().take(8)
        val videosDir = File(context.filesDir, "generated_videos").apply { mkdirs() }
        val videoFile = File(videosDir, "basoka_vid_$videoId.mp4")

        try {
            if (!finalData.videoBase64.isNullOrEmpty()) {
                val bytes = Base64.decode(finalData.videoBase64, Base64.DEFAULT)
                FileOutputStream(videoFile).use { fos ->
                    fos.write(bytes)
                    fos.flush()
                }
            } else if (!finalData.videoUri.isNullOrEmpty()) {
                val downloadRes = GeminiApiClient.downloadVideoBytes(finalData.videoUri)
                if (downloadRes.isSuccess) {
                    val bytes = downloadRes.getOrThrow()
                    FileOutputStream(videoFile).use { fos ->
                        fos.write(bytes)
                        fos.flush()
                    }
                } else {
                    val dlErr = "شکست لە داگرتنی پەڕگەی ڤیدیۆ لە ڕاژەکار."
                    onStateChanged(VideoStudioState.Error(dlErr, canRetry = true))
                    return@withContext Result.failure(Exception(dlErr))
                }
            } else {
                val noDataErr = "زانیاری ڤیدیۆی بەرهەمهاتوو بەردەست نییە."
                onStateChanged(VideoStudioState.Error(noDataErr, canRetry = true))
                return@withContext Result.failure(Exception(noDataErr))
            }

            if (!videoFile.exists() || videoFile.length() == 0L) {
                val emptyErr = "پەڕگەی ڤیدیۆی دروستکراو بەتاڵە."
                onStateChanged(VideoStudioState.Error(emptyErr, canRetry = true))
                return@withContext Result.failure(Exception(emptyErr))
            }

            val uri = try {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    videoFile
                )
            } catch (_: Exception) {
                Uri.fromFile(videoFile)
            }

            val result = VideoResult(
                id = videoId,
                prompt = prompt,
                localFilePath = videoFile.absolutePath,
                uri = uri,
                aspectRatio = aspectRatio,
                durationSec = durationSec,
                modelUsed = "Veo 2.0"
            )

            onStateChanged(VideoStudioState.Ready(result))
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "Failed saving video file", e)
            val saveErr = "شکست لە پاشەکەوتکردنی ڤیدیۆ لەسەر ئامێر: ${e.localizedMessage}"
            onStateChanged(VideoStudioState.Error(saveErr, canRetry = true))
            Result.failure(Exception(saveErr))
        }
    }

    fun shareVideo(context: Context, videoResult: VideoResult) {
        try {
            val file = File(videoResult.localFilePath)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "video/mp4"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, "ڤیدیۆی دروستکراو بە Basoka AI: ${videoResult.prompt}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "هاوبەشکردنی ڤیدیۆ").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun saveToMovies(context: Context, videoResult: VideoResult): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(videoResult.localFilePath)
            if (!file.exists()) return@withContext false

            val filename = "Basoka_${System.currentTimeMillis()}.mp4"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Video.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                    put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/BasokaAI")
                    put(MediaStore.Video.Media.IS_PENDING, 1)
                }

                val uri = context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
                    ?: return@withContext false

                context.contentResolver.openOutputStream(uri)?.use { out ->
                    file.inputStream().use { input -> input.copyTo(out) }
                }

                values.clear()
                values.put(MediaStore.Video.Media.IS_PENDING, 0)
                context.contentResolver.update(uri, values, null, null)
                true
            } else {
                val moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                val appDir = File(moviesDir, "BasokaAI").apply { mkdirs() }
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
