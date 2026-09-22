package com.example.data

import com.example.model.AICapability
import com.example.model.UsageRecord
import com.example.model.UsageStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UsageTracker {
    private val records = mutableListOf<UsageRecord>()
    private val _stats = MutableStateFlow(UsageStats())
    val stats: StateFlow<UsageStats> = _stats.asStateFlow()

    init {
        // Pre-populate with realistic initial data for stats visualizer
        records.add(
            UsageRecord(
                id = "init_1",
                timestamp = System.currentTimeMillis() - 86400000,
                providerId = "google_gemini",
                modelId = "gemini-3.5-flash",
                capability = AICapability.CHAT,
                creditsUsed = 0,
                tokensUsed = 420,
                latencyMs = 85,
                success = true
            )
        )
        records.add(
            UsageRecord(
                id = "init_2",
                timestamp = System.currentTimeMillis() - 43200000,
                providerId = "google_gemini",
                modelId = "gemini-2.5-flash-image",
                capability = AICapability.IMAGE_GENERATION,
                creditsUsed = 4,
                tokensUsed = 120,
                latencyMs = 650,
                success = true
            )
        )
        recalculate()
    }

    fun record(
        providerId: String,
        modelId: String,
        capability: AICapability,
        creditsUsed: Int,
        tokensUsed: Int = 0,
        latencyMs: Long = 100,
        success: Boolean = true
    ) {
        val r = UsageRecord(
            id = "rec_${System.currentTimeMillis()}",
            timestamp = System.currentTimeMillis(),
            providerId = providerId,
            modelId = modelId,
            capability = capability,
            creditsUsed = creditsUsed,
            tokensUsed = tokensUsed,
            latencyMs = latencyMs,
            success = success
        )
        records.add(0, r)
        recalculate()
    }

    fun getAllRecords(): List<UsageRecord> = records.toList()

    private fun recalculate() {
        val totalReq = records.size
        val totalCredits = records.sumOf { it.creditsUsed }
        val imageCount = records.count { it.capability == AICapability.IMAGE_GENERATION || it.capability == AICapability.IMAGE_EDITING }
        val videoCount = records.count { it.capability == AICapability.VIDEO_GENERATION }
        val voiceMinutes = records.count { it.capability == AICapability.SPEECH_TO_TEXT || it.capability == AICapability.TEXT_TO_SPEECH } * 2

        _stats.value = UsageStats(
            totalRequests = totalReq,
            totalCreditsUsed = totalCredits,
            totalImagesGenerated = imageCount,
            totalVideosGenerated = videoCount,
            totalVoiceMinutes = voiceMinutes,
            topProvider = "Google Gemini",
            topModel = "gemini-3.5-flash"
        )
    }
}
