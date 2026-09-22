package com.example.model

enum class CostLevel(val kurdishText: String) {
    FREE("خۆڕایی"),
    LOW("کەم"),
    MEDIUM("مامناوەند"),
    HIGH("بەرز")
}

enum class ModelCategory(val kurdishName: String) {
    CHAT("گفتوگۆ"),
    REASONING("شیکاری"),
    CODING("کۆد"),
    VISION("بینین"),
    IMAGE("وێنە"),
    VIDEO("ڤیدیۆ"),
    VOICE("دەنگ"),
    TRANSLATION("وەرگێڕان"),
    SEARCH("گەڕان")
}

data class AIModel(
    val id: String,
    val name: String,
    val providerId: String,
    val category: ModelCategory,
    val capabilities: Set<AICapability>,
    val quality: Int, // 1 to 5
    val speed: Int,   // 1 to 5
    val cost: CostLevel,
    val creditCost: Int,
    val isFree: Boolean,
    val dailyLimit: Int = 100,
    val monthlyLimit: Int = 3000,
    val enabled: Boolean = true,
    val priority: Int = 1,
    val descriptionKu: String = ""
)
