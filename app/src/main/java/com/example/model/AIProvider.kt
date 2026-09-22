package com.example.model

enum class ProviderStatus(val kurdishText: String) {
    ONLINE("چالاک 🟢"),
    LIMITED("سنووردار 🟡"),
    OFFLINE("ناچالاک 🔴"),
    NOT_CONFIGURED("دانەنراوە ⚪")
}

data class AIProvider(
    val id: String,
    val name: String,
    val descriptionKu: String,
    val status: ProviderStatus,
    val hasApiKey: Boolean,
    val maskedApiKey: String = "",
    val isEnabled: Boolean = true,
    val priority: Int = 1,
    val latencyMs: Long = 120,
    val capabilities: Set<AICapability> = emptySet(),
    val isOfficialServerKey: Boolean = false,
    val supportsLocalFallback: Boolean = true
)
