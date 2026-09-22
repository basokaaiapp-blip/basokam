package com.example.model

enum class RouterMode(val kurdishTitle: String, val kurdishSubtitle: String) {
    AUTO("خۆکار", "هەڵبژاردنی باڵانسکراو بەپێی جۆری داواکاری"),
    BEST_QUALITY("باشترین کوالێتی", "پێشینەدان بە مۆدێلە پێشکەوتوو و بەهێزەکان"),
    FASTEST("خێراترین", "پێشینەدان بە خێراترین وەڵامدانەوە"),
    LOWEST_CREDIT("کەمترین Credit", "پاشەکەوتکردنی کرێدت و هەڵبژاردنی مۆدێلی کەم خەرج"),
    FREE_ONLY("تەنها خۆڕایی", "تەنها بەکارهێنانی مۆدێلە بێبەرامبەرەکان"),
    SPECIFIC_MODELS("Model ـە دیاریکراوەکان", "هەڵبژاردنی دەستی بە خواستی خۆت")
}

data class RouterDecision(
    val selectedModel: AIModel,
    val selectedProvider: AIProvider,
    val capability: AICapability,
    val fallbackChain: List<AIModel>,
    val reasonKurdish: String,
    val estimatedCredits: Int,
    val isFree: Boolean
)
