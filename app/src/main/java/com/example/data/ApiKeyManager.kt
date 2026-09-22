package com.example.data

import android.content.Context
import android.content.SharedPreferences

enum class KeyStatus(val kurdishText: String) {
    ACTIVE("چالاک ✅"),
    INVALID("نادروست ❌"),
    EXPIRED("بەسەرچوو ⚠️"),
    RATE_LIMITED("سنووردار ⏳"),
    NETWORK_ERROR("هەڵەی پەیوەندی 🌐")
}

data class StoredApiKey(
    val providerId: String,
    val aliasName: String,
    val maskedKey: String,
    val status: KeyStatus,
    val addedAt: Long = System.currentTimeMillis()
)

class ApiKeyManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("basoka_api_keys", Context.MODE_PRIVATE)

    fun saveKey(providerId: String, rawKey: String, alias: String = ""): StoredApiKey {
        val trimmed = rawKey.trim()
        val masked = if (trimmed.length > 8) {
            "••••••••" + trimmed.takeLast(4)
        } else {
            "••••" + trimmed.takeLast(2)
        }

        // Save raw key securely in app-private prefs (in production would use EncryptedSharedPreferences)
        prefs.edit().putString("raw_$providerId", trimmed).apply()
        prefs.edit().putString("masked_$providerId", masked).apply()
        prefs.edit().putString("alias_$providerId", alias.ifEmpty { "$providerId Key" }).apply()
        prefs.edit().putString("status_$providerId", KeyStatus.ACTIVE.name).apply()

        return StoredApiKey(
            providerId = providerId,
            aliasName = alias.ifEmpty { "$providerId Key" },
            maskedKey = masked,
            status = KeyStatus.ACTIVE
        )
    }

    fun getRawKey(providerId: String): String? {
        return prefs.getString("raw_$providerId", null)
    }

    fun getStoredKey(providerId: String): StoredApiKey? {
        val masked = prefs.getString("masked_$providerId", null) ?: return null
        val alias = prefs.getString("alias_$providerId", "$providerId Key") ?: "$providerId Key"
        val statusStr = prefs.getString("status_$providerId", KeyStatus.ACTIVE.name)
        val status = try {
            KeyStatus.valueOf(statusStr ?: KeyStatus.ACTIVE.name)
        } catch (_: Exception) {
            KeyStatus.ACTIVE
        }

        return StoredApiKey(
            providerId = providerId,
            aliasName = alias,
            maskedKey = masked,
            status = status
        )
    }

    fun testKey(providerId: String): KeyStatus {
        val raw = getRawKey(providerId)
        val status = if (raw.isNullOrBlank()) {
            KeyStatus.INVALID
        } else if (raw.length < 8) {
            KeyStatus.INVALID
        } else {
            KeyStatus.ACTIVE
        }
        prefs.edit().putString("status_$providerId", status.name).apply()
        return status
    }

    fun deleteKey(providerId: String) {
        prefs.edit()
            .remove("raw_$providerId")
            .remove("masked_$providerId")
            .remove("alias_$providerId")
            .remove("status_$providerId")
            .apply()
    }
}
