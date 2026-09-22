package com.example.model

enum class JobStatus(val kurdishText: String) {
    QUEUED("لە ڕیزدایە ⏳"),
    PROCESSING("لە جێبەجێکردندایە ⚙️"),
    COMPLETED("تەواوبوو ✅"),
    FAILED("شکستیهێنا ❌"),
    CANCELLED("هەڵوەشێنرایەوە ⏹️")
}

data class GenerationJob(
    val id: String,
    val title: String,
    val type: String, // "VIDEO", "IMAGE", "RESEARCH", "PDF"
    val prompt: String,
    val status: JobStatus,
    val progress: Float = 0f, // 0.0 to 1.0
    val resultUrlOrText: String? = null,
    val modelId: String,
    val providerId: String,
    val creditsCharged: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
