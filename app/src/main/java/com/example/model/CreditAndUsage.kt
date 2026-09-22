package com.example.model

data class CreditWallet(
    val balance: Int = 150,
    val dailyUsed: Int = 14,
    val dailyLimit: Int = 200,
    val monthlyUsed: Int = 320,
    val monthlyLimit: Int = 5000,
    val isFreeOnlyMode: Boolean = false,
    val autoApprovePaid: Boolean = false
)

data class UsageRecord(
    val id: String,
    val timestamp: Long,
    val providerId: String,
    val modelId: String,
    val capability: AICapability,
    val creditsUsed: Int,
    val tokensUsed: Int = 0,
    val latencyMs: Long = 0,
    val success: Boolean = true
)

data class UsageStats(
    val totalRequests: Int = 48,
    val totalCreditsUsed: Int = 186,
    val totalImagesGenerated: Int = 12,
    val totalVideosGenerated: Int = 3,
    val totalVoiceMinutes: Int = 25,
    val topProvider: String = "Google Gemini",
    val topModel: String = "gemini-3.5-flash"
)
