package com.example.data

import com.example.model.AICapability
import com.example.model.AIModel
import com.example.model.AIProvider
import com.example.model.CostLevel
import com.example.model.ModelCategory
import com.example.model.ProviderStatus

object ModelRegistry {

    fun getDefaultProviders(hasGeminiKey: Boolean): List<AIProvider> {
        return listOf(
            AIProvider(
                id = "google_gemini",
                name = "Google Gemini",
                descriptionKu = "مۆدێلە پێشکەوتووەکانی گووگڵ بۆ وتووێژ، لۆژیک، وێنە و دەنگ",
                status = if (hasGeminiKey) ProviderStatus.ONLINE else ProviderStatus.ONLINE,
                hasApiKey = hasGeminiKey,
                maskedApiKey = if (hasGeminiKey) "••••••••AI_STUDIO" else "",
                isEnabled = true,
                priority = 1,
                latencyMs = 95,
                capabilities = setOf(
                    AICapability.CHAT, AICapability.REASONING, AICapability.CODING,
                    AICapability.VISION, AICapability.IMAGE_GENERATION, AICapability.IMAGE_EDITING,
                    AICapability.IMAGE_ENHANCEMENT, AICapability.IMAGE_UPSCALING,
                    AICapability.VIDEO_GENERATION, AICapability.SPEECH_TO_TEXT,
                    AICapability.TEXT_TO_SPEECH, AICapability.FILE_ANALYSIS,
                    AICapability.PDF_ANALYSIS, AICapability.WEB_SEARCH,
                    AICapability.RESEARCH, AICapability.TRANSLATION
                ),
                isOfficialServerKey = true
            ),
            AIProvider(
                id = "openai",
                name = "OpenAI",
                descriptionKu = "مۆدێلەکانی GPT-4o و DALL-E و Whisper بۆ دەق، وێنە و دەنگ",
                status = ProviderStatus.NOT_CONFIGURED,
                hasApiKey = false,
                maskedApiKey = "",
                isEnabled = true,
                priority = 2,
                latencyMs = 150,
                capabilities = setOf(
                    AICapability.CHAT, AICapability.REASONING, AICapability.CODING,
                    AICapability.VISION, AICapability.IMAGE_GENERATION,
                    AICapability.SPEECH_TO_TEXT, AICapability.TEXT_TO_SPEECH,
                    AICapability.TRANSLATION
                )
            ),
            AIProvider(
                id = "anthropic",
                name = "Anthropic Claude",
                descriptionKu = "مۆدێلەکانی Claude 3.5 بۆ نووسینی سروشتی، بیرکردنەوە و کۆد",
                status = ProviderStatus.NOT_CONFIGURED,
                hasApiKey = false,
                maskedApiKey = "",
                isEnabled = true,
                priority = 3,
                latencyMs = 175,
                capabilities = setOf(
                    AICapability.CHAT, AICapability.REASONING, AICapability.CODING,
                    AICapability.FILE_ANALYSIS, AICapability.RESEARCH, AICapability.TRANSLATION
                )
            ),
            AIProvider(
                id = "deepseek",
                name = "DeepSeek",
                descriptionKu = "مۆدێلەکانی شیکاری بیرکاری، لۆژیک و پرۆگرامسازی باڵا",
                status = ProviderStatus.NOT_CONFIGURED,
                hasApiKey = false,
                maskedApiKey = "",
                isEnabled = true,
                priority = 4,
                latencyMs = 190,
                capabilities = setOf(
                    AICapability.CHAT, AICapability.REASONING, AICapability.CODING
                )
            ),
            AIProvider(
                id = "groq",
                name = "Groq Fast Llama",
                descriptionKu = "ئامێری LPU بۆ وەڵامدانەوەی خێرا بە مۆدێلەکانی کراوە",
                status = ProviderStatus.NOT_CONFIGURED,
                hasApiKey = false,
                maskedApiKey = "",
                isEnabled = true,
                priority = 5,
                latencyMs = 45,
                capabilities = setOf(
                    AICapability.CHAT, AICapability.CODING, AICapability.TRANSLATION
                )
            ),
            AIProvider(
                id = "mistral",
                name = "Mistral AI",
                descriptionKu = "مۆدێلە ئەوروپییە خێراکان بۆ زمان و کۆد",
                status = ProviderStatus.NOT_CONFIGURED,
                hasApiKey = false,
                maskedApiKey = "",
                isEnabled = false,
                priority = 6,
                latencyMs = 135,
                capabilities = setOf(
                    AICapability.CHAT, AICapability.CODING, AICapability.TRANSLATION
                )
            )
        )
    }

    fun getDefaultModels(): List<AIModel> {
        return listOf(
            AIModel(
                id = "gemini-3.5-flash",
                name = "Gemini 3.5 Flash",
                providerId = "google_gemini",
                category = ModelCategory.CHAT,
                capabilities = setOf(
                    AICapability.CHAT, AICapability.CODING, AICapability.VISION,
                    AICapability.FILE_ANALYSIS, AICapability.PDF_ANALYSIS,
                    AICapability.WEB_SEARCH, AICapability.TRANSLATION, AICapability.RESEARCH
                ),
                quality = 4,
                speed = 5,
                cost = CostLevel.FREE,
                creditCost = 0,
                isFree = true,
                priority = 1,
                descriptionKu = "مۆدێلی خێرا و سەردەمیانەی گووگڵ بۆ زۆربەی کارە ڕۆژانەییەکان بەبێ خەرجکردنی کرێدت"
            ),
            AIModel(
                id = "gemini-3.1-pro-preview",
                name = "Gemini 3.1 Pro Preview",
                providerId = "google_gemini",
                category = ModelCategory.REASONING,
                capabilities = setOf(
                    AICapability.CHAT, AICapability.REASONING, AICapability.CODING,
                    AICapability.RESEARCH, AICapability.FILE_ANALYSIS
                ),
                quality = 5,
                speed = 4,
                cost = CostLevel.LOW,
                creditCost = 2,
                isFree = false,
                priority = 2,
                descriptionKu = "بەهێزترین مۆدێلی گووگڵ بۆ بیرکردنەوەی قووڵ، ماتماتیک و پەرەپێدانی پڕۆژەی کۆد"
            ),
            AIModel(
                id = "gemini-2.5-flash-image",
                name = "Gemini 2.5 Flash Image",
                providerId = "google_gemini",
                category = ModelCategory.IMAGE,
                capabilities = setOf(
                    AICapability.IMAGE_GENERATION, AICapability.IMAGE_EDITING, AICapability.IMAGE_ENHANCEMENT
                ),
                quality = 5,
                speed = 4,
                cost = CostLevel.MEDIUM,
                creditCost = 4,
                isFree = false,
                priority = 1,
                descriptionKu = "دروستکردن و دەستکاریکردنی وێنە بە کوالێتی بەرز و ستایلی جیاواز"
            ),
            AIModel(
                id = "gemini-3.1-flash-image-preview",
                name = "Gemini 3.1 Flash Image Preview (4K)",
                providerId = "google_gemini",
                category = ModelCategory.IMAGE,
                capabilities = setOf(
                    AICapability.IMAGE_GENERATION, AICapability.IMAGE_UPSCALING
                ),
                quality = 5,
                speed = 3,
                cost = CostLevel.HIGH,
                creditCost = 6,
                isFree = false,
                priority = 2,
                descriptionKu = "بەرهەمهێنانی وێنەی تەواو ڕوون و 4K بۆ کاری پرۆفیشناڵ"
            ),
            AIModel(
                id = "veo-3.1-fast-generate-preview",
                name = "Veo 3.1 Fast Generate Preview",
                providerId = "google_gemini",
                category = ModelCategory.VIDEO,
                capabilities = setOf(
                    AICapability.VIDEO_GENERATION, AICapability.VIDEO_EDITING
                ),
                quality = 5,
                speed = 3,
                cost = CostLevel.HIGH,
                creditCost = 12,
                isFree = false,
                priority = 1,
                descriptionKu = "مۆدێلی دروستکردنی ڤیدیۆی سینەمایی لە دەق بە جووڵەی نەرم"
            ),
            AIModel(
                id = "gemini-2.5-flash-preview-tts",
                name = "Gemini 2.5 Flash TTS",
                providerId = "google_gemini",
                category = ModelCategory.VOICE,
                capabilities = setOf(
                    AICapability.TEXT_TO_SPEECH, AICapability.SPEECH_TO_TEXT
                ),
                quality = 4,
                speed = 5,
                cost = CostLevel.LOW,
                creditCost = 1,
                isFree = false,
                priority = 1,
                descriptionKu = "گۆڕینی دەق بۆ دەنگی سروشتی بە خێرایی بەرز"
            ),
            // OpenAI models
            AIModel(
                id = "gpt-4o",
                name = "GPT-4o (Omni)",
                providerId = "openai",
                category = ModelCategory.CHAT,
                capabilities = setOf(
                    AICapability.CHAT, AICapability.REASONING, AICapability.CODING,
                    AICapability.VISION, AICapability.TRANSLATION
                ),
                quality = 5,
                speed = 4,
                cost = CostLevel.HIGH,
                creditCost = 3,
                isFree = false,
                priority = 3,
                descriptionKu = "مۆدێلی فرەچەشنی باڵای OpenAI بۆ دەق، وێنە و بیرکردنەوە"
            ),
            AIModel(
                id = "gpt-4o-mini",
                name = "GPT-4o Mini",
                providerId = "openai",
                category = ModelCategory.CHAT,
                capabilities = setOf(
                    AICapability.CHAT, AICapability.CODING, AICapability.TRANSLATION
                ),
                quality = 4,
                speed = 5,
                cost = CostLevel.FREE,
                creditCost = 0,
                isFree = true,
                priority = 2,
                descriptionKu = "مۆدێلی خێرای OpenAI بۆ پرسیار و وەڵامی ئاسایی"
            ),
            AIModel(
                id = "dall-e-3",
                name = "DALL-E 3",
                providerId = "openai",
                category = ModelCategory.IMAGE,
                capabilities = setOf(AICapability.IMAGE_GENERATION),
                quality = 5,
                speed = 3,
                cost = CostLevel.HIGH,
                creditCost = 6,
                isFree = false,
                priority = 3,
                descriptionKu = "دروستکردنی وێنەی ورد و داهێنەرانە"
            ),
            // Anthropic Claude
            AIModel(
                id = "claude-3-5-sonnet",
                name = "Claude 3.5 Sonnet",
                providerId = "anthropic",
                category = ModelCategory.CODING,
                capabilities = setOf(
                    AICapability.CHAT, AICapability.REASONING, AICapability.CODING,
                    AICapability.FILE_ANALYSIS, AICapability.RESEARCH
                ),
                quality = 5,
                speed = 4,
                cost = CostLevel.HIGH,
                creditCost = 3,
                isFree = false,
                priority = 4,
                descriptionKu = "مۆدێلی ژمارە یەک بۆ کۆدنووسین و نووسینی قووڵی ئەدەبی"
            ),
            // DeepSeek
            AIModel(
                id = "deepseek-r1",
                name = "DeepSeek-R1 (Reasoning)",
                providerId = "deepseek",
                category = ModelCategory.REASONING,
                capabilities = setOf(
                    AICapability.CHAT, AICapability.REASONING, AICapability.CODING
                ),
                quality = 5,
                speed = 3,
                cost = CostLevel.LOW,
                creditCost = 1,
                isFree = false,
                priority = 3,
                descriptionKu = "شیکارکردنی هەنگاو بە هەنگاو بە لۆژیکی کراوە و کەمترین خەرجی"
            ),
            // Groq Llama
            AIModel(
                id = "llama-3-3-70b",
                name = "Llama 3.3 70B (Groq)",
                providerId = "groq",
                category = ModelCategory.CHAT,
                capabilities = setOf(
                    AICapability.CHAT, AICapability.CODING, AICapability.TRANSLATION
                ),
                quality = 4,
                speed = 5,
                cost = CostLevel.FREE,
                creditCost = 0,
                isFree = true,
                priority = 2,
                descriptionKu = "وەڵامی زۆر خێرا بەهۆی تەکنەلۆژیای خێرای Groq"
            )
        )
    }
}
