package com.example.model

data class PromptTemplate(
    val id: String,
    val title: String,
    val promptText: String,
    val category: String, // "وێنە", "نووسین", "کۆد", "توێژینەوە", "خوێندن", "ڤیدیۆ"
    val isFavorite: Boolean = false,
    val iconEmoji: String = "✨"
)

data class SavedContent(
    val id: String,
    val title: String,
    val content: String,
    val type: String, // "وێنە", "دەق", "کۆد", "ڤیدیۆ", "توێژینەوە"
    val timestamp: Long = System.currentTimeMillis(),
    val modelUsed: String = "",
    val extraData: String? = null
)

data class UserMemoryItem(
    val id: String,
    val key: String,
    val value: String,
    val category: String = "گشتی"
)

data class ToolItem(
    val id: String,
    val kurdishTitle: String,
    val kurdishDesc: String,
    val category: String,
    val capability: AICapability,
    val iconEmoji: String,
    val badge: String? = null
)
