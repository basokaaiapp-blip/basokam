package com.example.data

import com.example.model.AICapability
import com.example.model.AIModel
import com.example.model.AIProvider
import com.example.model.CostLevel
import com.example.model.ProviderStatus
import com.example.model.RouterDecision
import com.example.model.RouterMode

class RouterEngine(
    private val allModels: List<AIModel>,
    private val allProviders: List<AIProvider>
) {

    fun detectCapability(userPrompt: String, explicitCapability: AICapability?): AICapability {
        if (explicitCapability != null) return explicitCapability

        val lower = userPrompt.lowercase().trim()

        return when {
            lower.contains("وێنە") || lower.contains("image") || lower.contains("picture") || lower.contains("بکێشە") || lower.contains("draw") -> {
                if (lower.contains("چاک") || lower.contains("تەرمیم") || lower.contains("enhanc") || lower.contains("edit")) {
                    AICapability.IMAGE_ENHANCEMENT
                } else if (lower.contains("4k") || lower.contains("گورەکردن") || lower.contains("upscale")) {
                    AICapability.IMAGE_UPSCALING
                } else {
                    AICapability.IMAGE_GENERATION
                }
            }
            lower.contains("ڤیدیۆ") || lower.contains("video") || lower.contains("کلیپ") -> AICapability.VIDEO_GENERATION
            lower.contains("کۆد") || lower.contains("code") || lower.contains("error") || lower.contains("function") ||
                    lower.contains("bug") || lower.contains("پڕۆگرام") || lower.contains("kotlin") || lower.contains("python") -> AICapability.CODING
            lower.contains("pdf") || lower.contains("فایل") || lower.contains("دۆکیومێنت") || lower.contains("کتاب") -> AICapability.PDF_ANALYSIS
            lower.contains("گەڕان") || lower.contains("search") || lower.contains("ئینتەرنێت") || lower.contains("نوێترین") -> AICapability.WEB_SEARCH
            lower.contains("توێژینەوە") || lower.contains("research") -> AICapability.RESEARCH
            lower.contains("وەرگێڕان") || lower.contains("translate") || lower.contains("بیکە بە کوردی") || lower.contains("ترجم") -> AICapability.TRANSLATION
            lower.contains("دەنگ") || lower.contains("voice") || lower.contains("بمخوێنەرەوە") || lower.contains("audio") -> AICapability.TEXT_TO_SPEECH
            lower.contains("بۆچی") || lower.contains("هۆکار") || lower.contains("شیکاری") || lower.contains("ماتماتیک") -> AICapability.REASONING
            else -> AICapability.CHAT
        }
    }

    fun route(
        prompt: String,
        explicitCapability: AICapability? = null,
        mode: RouterMode = RouterMode.AUTO,
        isFreeOnly: Boolean = false,
        specificModelId: String? = null,
        currentCreditBalance: Int = 100
    ): RouterDecision {
        val capability = detectCapability(prompt, explicitCapability)

        // Find provider lookup map
        val providerMap = allProviders.associateBy { it.id }

        // Specific model selection
        if (mode == RouterMode.SPECIFIC_MODELS && !specificModelId.isNullOrBlank() && specificModelId != "AUTO") {
            val targeted = allModels.find { it.id == specificModelId }
            if (targeted != null) {
                val prov = providerMap[targeted.providerId] ?: allProviders.first()
                val fallbacks = allModels.filter { it.id != targeted.id && it.capabilities.contains(capability) }
                return RouterDecision(
                    selectedModel = targeted,
                    selectedProvider = prov,
                    capability = capability,
                    fallbackChain = fallbacks.take(3),
                    reasonKurdish = "هەڵبژێردراو بەپێی دەستنیشانکردنی دەستی بەکارهێنەر (${targeted.name})",
                    estimatedCredits = targeted.creditCost,
                    isFree = targeted.isFree
                )
            }
        }

        // Filter models that support this capability and are enabled
        var eligibleModels = allModels.filter { model ->
            model.enabled && model.capabilities.contains(capability)
        }

        if (eligibleModels.isEmpty()) {
            // Fallback to chat models
            eligibleModels = allModels.filter { it.capabilities.contains(AICapability.CHAT) }
        }

        // Filter by Provider availability (Online or has API key / official integration)
        val availableModels = eligibleModels.filter { model ->
            val prov = providerMap[model.providerId]
            prov != null && prov.isEnabled && (prov.hasApiKey || prov.isOfficialServerKey || prov.status == ProviderStatus.ONLINE)
        }.ifEmpty { eligibleModels } // If none strictly active, use eligible

        // Sort based on RouterMode
        val sortedModels = when (mode) {
            RouterMode.FREE_ONLY -> {
                availableModels.filter { it.isFree }.ifEmpty { availableModels.sortedBy { it.creditCost } }
            }
            RouterMode.BEST_QUALITY -> {
                availableModels.sortedWith(compareByDescending<AIModel> { it.quality }.thenByDescending { it.speed })
            }
            RouterMode.FASTEST -> {
                availableModels.sortedWith(compareByDescending<AIModel> { it.speed }.thenByDescending { it.quality })
            }
            RouterMode.LOWEST_CREDIT -> {
                availableModels.sortedWith(compareBy<AIModel> { it.creditCost }.thenByDescending { it.quality })
            }
            RouterMode.AUTO, RouterMode.SPECIFIC_MODELS -> {
                if (isFreeOnly) {
                    availableModels.sortedWith(compareBy<AIModel> { it.creditCost }.thenByDescending { it.quality })
                } else {
                    // Balance: high quality, fast speed, sensible cost
                    availableModels.sortedWith(
                        compareByDescending<AIModel> { it.priority }
                            .thenByDescending { it.quality }
                            .thenByDescending { it.speed }
                    )
                }
            }
        }

        val primary = sortedModels.firstOrNull() ?: allModels.first()
        val fallbacks = sortedModels.drop(1).take(3)
        val selectedProvider = providerMap[primary.providerId] ?: allProviders.first()

        val reason = when (mode) {
            RouterMode.FREE_ONLY -> "هەڵبژێردرا چونکە دۆخی 'تەنها خۆڕایی' چالاکە و خەرجی کرێدت نییە."
            RouterMode.BEST_QUALITY -> "هەڵبژێردرا چونکە بەرزترین کواڵێتی ($primary.quality/5) و شیکاری هەیە."
            RouterMode.FASTEST -> "هەڵبژێردرا چونکە خێراترین وەڵامدانەوە ($primary.speed/5) پێشکەش دەکات."
            RouterMode.LOWEST_CREDIT -> "هەڵبژێردرا بۆ کەمترین بەکارهێنانی کرێدت (${primary.creditCost} کرێدت)."
            RouterMode.AUTO -> "دەستنیشانکرا بە شێوەی خۆکار لە نێوان چەندین مۆدێل بۆ توانای '${capability.kurdishName}'."
            RouterMode.SPECIFIC_MODELS -> "هەڵبژێردرا بە شێوەی تایبەت."
        }

        return RouterDecision(
            selectedModel = primary,
            selectedProvider = selectedProvider,
            capability = capability,
            fallbackChain = fallbacks,
            reasonKurdish = reason,
            estimatedCredits = primary.creditCost,
            isFree = primary.isFree
        )
    }
}
