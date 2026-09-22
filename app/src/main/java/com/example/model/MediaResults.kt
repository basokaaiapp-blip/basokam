package com.example.model

import android.net.Uri

data class ImageResult(
    val id: String,
    val prompt: String,
    val localFilePath: String,
    val uri: Uri,
    val aspectRatio: String = "1:1",
    val style: String = "Cinematic",
    val modelUsed: String = "Imagen 3",
    val createdAt: Long = System.currentTimeMillis()
)

sealed interface ImageStudioState {
    data object Idle : ImageStudioState
    data class Generating(val messageKu: String = "وێنەکە دروست دەکرێت...") : ImageStudioState
    data class Success(val imageResult: ImageResult) : ImageStudioState
    data class Error(val messageKu: String, val canRetry: Boolean = true) : ImageStudioState {
        val errorMessageKu: String get() = messageKu
    }
}

data class VideoResult(
    val id: String,
    val prompt: String,
    val localFilePath: String,
    val uri: Uri,
    val aspectRatio: String = "16:9",
    val durationSec: Int = 5,
    val modelUsed: String = "Veo",
    val createdAt: Long = System.currentTimeMillis()
)

sealed interface VideoStudioState {
    data object Idle : VideoStudioState
    data class Starting(val messageKu: String = "دەستپێکردنی دروستکردنی ڤیدیۆ...") : VideoStudioState
    data class Generating(val messageKu: String = "ڤیدیۆکە دروست دەکرێت...") : VideoStudioState
    data class Processing(val progress: Float = 0.3f, val messageKu: String = "خەریکی ئامادەکردنی ڤیدیۆکەین...") : VideoStudioState
    data class Downloading(val progress: Float = 0.8f, val messageKu: String = "ڤیدیۆکە ئامادە دەکرێت...") : VideoStudioState
    data class Ready(val videoResult: VideoResult) : VideoStudioState
    data class Error(val messageKu: String, val canRetry: Boolean = true) : VideoStudioState {
        val errorMessageKu: String get() = messageKu
    }
}
