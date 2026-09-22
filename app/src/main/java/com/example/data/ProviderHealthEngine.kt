package com.example.data

import com.example.model.ProviderStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProviderHealthInfo(
    val providerId: String,
    val status: ProviderStatus,
    val latencyMs: Long,
    val successRate: Float = 0.99f,
    val lastChecked: Long = System.currentTimeMillis()
)

class ProviderHealthEngine {
    private val _healthMap = MutableStateFlow<Map<String, ProviderHealthInfo>>(
        mapOf(
            "google_gemini" to ProviderHealthInfo("google_gemini", ProviderStatus.ONLINE, 85, 0.99f),
            "openai" to ProviderHealthInfo("openai", ProviderStatus.NOT_CONFIGURED, 0, 0.0f),
            "anthropic" to ProviderHealthInfo("anthropic", ProviderStatus.NOT_CONFIGURED, 0, 0.0f),
            "deepseek" to ProviderHealthInfo("deepseek", ProviderStatus.NOT_CONFIGURED, 0, 0.0f),
            "groq" to ProviderHealthInfo("groq", ProviderStatus.NOT_CONFIGURED, 0, 0.0f),
            "mistral" to ProviderHealthInfo("mistral", ProviderStatus.NOT_CONFIGURED, 0, 0.0f)
        )
    )
    val healthMap: StateFlow<Map<String, ProviderHealthInfo>> = _healthMap.asStateFlow()

    fun updateStatus(providerId: String, status: ProviderStatus, latencyMs: Long) {
        val current = _healthMap.value.toMutableMap()
        current[providerId] = ProviderHealthInfo(
            providerId = providerId,
            status = status,
            latencyMs = latencyMs,
            successRate = if (status == ProviderStatus.ONLINE) 0.99f else 0.50f,
            lastChecked = System.currentTimeMillis()
        )
        _healthMap.value = current
    }
}
