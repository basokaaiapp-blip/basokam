package com.example.model

enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM
}

enum class AttachmentType {
    IMAGE,
    FILE,
    VOICE,
    CODE
}

data class MessageAttachment(
    val id: String,
    val type: AttachmentType,
    val name: String,
    val uriOrPath: String = "",
    val mimeType: String = ""
)

data class ChatMessage(
    val id: String,
    val conversationId: String,
    val role: MessageRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val modelId: String? = null,
    val providerId: String? = null,
    val creditsUsed: Int = 0,
    val isError: Boolean = false,
    val isThinking: Boolean = false,
    val attachments: List<MessageAttachment> = emptyList(),
    val isFavorite: Boolean = false
)

data class Conversation(
    val id: String,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val selectedModelId: String = "AUTO", // "AUTO" or specific model ID
    val lastSnippet: String = ""
)
