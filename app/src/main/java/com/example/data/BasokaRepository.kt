package com.example.data

import android.content.Context
import com.example.ai.AIExecutionEngine
import com.example.ai.GeminiApiClient
import com.example.model.AICapability
import com.example.model.AIModel
import com.example.model.AIProvider
import com.example.model.ProviderStatus
import com.example.model.ChatMessage
import com.example.model.Conversation
import com.example.model.CreditWallet
import com.example.model.GenerationJob
import com.example.model.JobStatus
import com.example.model.MessageRole
import com.example.model.PromptTemplate
import com.example.model.RouterDecision
import com.example.model.RouterMode
import com.example.model.SavedContent
import com.example.model.ToolItem
import com.example.model.UserMemoryItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class BasokaRepository(context: Context) {
    private val scope = CoroutineScope(Dispatchers.Main)

    val apiKeyManager = ApiKeyManager(context)
    val creditEngine = CreditEngine()
    val usageTracker = UsageTracker()
    val healthEngine = ProviderHealthEngine()

    private val _providers = MutableStateFlow<List<AIProvider>>(
        ModelRegistry.getDefaultProviders(GeminiApiClient.isConfigured())
    )
    val providers: StateFlow<List<AIProvider>> = _providers.asStateFlow()

    private val _models = MutableStateFlow<List<AIModel>>(ModelRegistry.getDefaultModels())
    val models: StateFlow<List<AIModel>> = _models.asStateFlow()

    val routerEngine = RouterEngine(_models.value, _providers.value)
    val executionEngine = AIExecutionEngine(apiKeyManager, creditEngine, usageTracker, healthEngine)

    private val _routerMode = MutableStateFlow(RouterMode.AUTO)
    val routerMode: StateFlow<RouterMode> = _routerMode.asStateFlow()

    private val _isFreeOnly = MutableStateFlow(false)
    val isFreeOnly: StateFlow<Boolean> = _isFreeOnly.asStateFlow()

    private val _isAdvancedMode = MutableStateFlow(false)
    val isAdvancedMode: StateFlow<Boolean> = _isAdvancedMode.asStateFlow()

    private val _wallet = MutableStateFlow(creditEngine.getWallet())
    val wallet: StateFlow<CreditWallet> = _wallet.asStateFlow()

    // Conversations & Chat
    private val _conversations = MutableStateFlow<List<Conversation>>(listOf(
        Conversation(
            id = "conv_default",
            title = "گفتوگۆی سەرەکی",
            lastSnippet = "سڵاو! من ئامادەم بۆ یارمەتیدانت."
        )
    ))
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _activeConversationId = MutableStateFlow("conv_default")
    val activeConversationId: StateFlow<String> = _activeConversationId.asStateFlow()

    private val _messages = MutableStateFlow<Map<String, List<ChatMessage>>>(mapOf(
        "conv_default" to listOf(
            ChatMessage(
                id = "msg_welcome",
                conversationId = "conv_default",
                role = MessageRole.ASSISTANT,
                content = "سڵاو 👋 من **Basoka AI** ـم.\nپلاتفۆرمێکی فرە-AI ـم کە پێکدێت لە چەندین مۆدێل و دابینکەری زیرەک وەک Gemini، OpenAI، Claude و هیتر.\n\nچی دەتەوێت بکەم بۆت؟ دەتوانیت دەق، وێنە، کۆد یان فایل بنێریت.",
                modelId = "gemini-3.5-flash",
                providerId = "google_gemini"
            )
        )
    ))
    val messages: StateFlow<Map<String, List<ChatMessage>>> = _messages.asStateFlow()

    // Async Jobs (Video, Image generation, etc.)
    private val _jobs = MutableStateFlow<List<GenerationJob>>(emptyList())
    val jobs: StateFlow<List<GenerationJob>> = _jobs.asStateFlow()

    // Saved items
    private val _savedItems = MutableStateFlow<List<SavedContent>>(listOf(
        SavedContent(
            id = "s_1",
            title = "کۆدی ژمێریاری ژیری دەستکرد",
            content = "fun calculateTokens(prompt: String): Int = prompt.length / 4",
            type = "کۆد",
            modelUsed = "Gemini 3.5 Flash"
        ),
        SavedContent(
            id = "s_2",
            title = "پوختەی کتێبی هۆشمەندی دەستکرد",
            content = "ڕوونکردنەوەی فێربوونی قووڵ و تۆڕە دەمارییەکان بە زمانی کوردی.",
            type = "دەق",
            modelUsed = "Claude 3.5 Sonnet"
        )
    ))
    val savedItems: StateFlow<List<SavedContent>> = _savedItems.asStateFlow()

    // Prompt Library
    private val _promptLibrary = MutableStateFlow<List<PromptTemplate>>(listOf(
        PromptTemplate("p_1", "وێنەی سینەمایی کوردەواری", "وێنەیەکی فۆتۆریالیستیکی بەرزکوالێتی لە کێوی هەڵگورد لە کاتی خۆرئاوابوون بە ستایلی 8K Cinematic", "وێنە", true, "🎨"),
        PromptTemplate("p_2", "کۆدنووسینی Clean Architecture", "تکایە پێکهاتەی ViewModel و Repository بە زمانی Kotlin و Compose بۆ پڕۆژەیەکی Android بنووسە", "کۆد", true, "💻"),
        PromptTemplate("p_3", "پوختەکردنی پەرتووک", "تکایە ئەم دەقە لە پێنج خاڵی گرنگ و سەرنجڕاکێشدا پوختە بکەرەوە بە زمانی کوردی سۆرانی", "نووسین", false, "📝"),
        PromptTemplate("p_4", "وەرگێڕانی فەرمی و زانستی", "ئەم زانیارییە ئینگلیزییە بۆ کوردی سۆرانی پاراو وەرگێڕە بە پاراستنی دەستەواژە زانستییەکان", "وەرگێڕان", true, "🌍"),
        PromptTemplate("p_5", "پرسیاری تاقیکردنەوە (Quiz)", "دەربارەی مێژووی ژیری دەستکرد ٥ پرسیاری چواربژاردەیی لەگەڵ وەڵامی دروست دروست بکە", "خوێندن", false, "🎓")
    ))
    val promptLibrary: StateFlow<List<PromptTemplate>> = _promptLibrary.asStateFlow()

    // User Memory
    private val _userMemory = MutableStateFlow<List<UserMemoryItem>>(listOf(
        UserMemoryItem("m_1", "زمانی دڵخواز", "کوردی سۆرانی", "زمان"),
        UserMemoryItem("m_2", "شێوازی وەڵامدانەوە", "کورت، پوخت و بە زمانی فەرمی", "شێواز"),
        UserMemoryItem("m_3", "مۆدێلی دڵخواز", "Gemini 3.5 Flash", "مۆدێل")
    ))
    val userMemory: StateFlow<List<UserMemoryItem>> = _userMemory.asStateFlow()

    private val _memoryEnabled = MutableStateFlow(true)
    val memoryEnabled: StateFlow<Boolean> = _memoryEnabled.asStateFlow()

    fun setRouterMode(mode: RouterMode) {
        _routerMode.value = mode
    }

    fun setFreeOnly(enabled: Boolean) {
        _isFreeOnly.value = enabled
        creditEngine.setFreeOnlyMode(enabled)
        _wallet.value = creditEngine.getWallet()
    }

    fun setAdvancedMode(enabled: Boolean) {
        _isAdvancedMode.value = enabled
    }

    fun setMemoryEnabled(enabled: Boolean) {
        _memoryEnabled.value = enabled
    }

    fun addMemoryItem(key: String, value: String, category: String) {
        val newItem = UserMemoryItem(UUID.randomUUID().toString(), key, value, category)
        _userMemory.value = _userMemory.value + newItem
    }

    fun removeMemoryItem(id: String) {
        _userMemory.value = _userMemory.value.filter { it.id != id }
    }

    fun createNewConversation(title: String = "گفتوگۆی نوێ"): String {
        val newId = "conv_" + UUID.randomUUID().toString().take(6)
        val newConv = Conversation(
            id = newId,
            title = title,
            lastSnippet = ""
        )
        _conversations.value = listOf(newConv) + _conversations.value
        _activeConversationId.value = newId
        _messages.value = _messages.value + (newId to emptyList())
        return newId
    }

    fun selectConversation(id: String) {
        _activeConversationId.value = id
    }

    fun deleteConversation(id: String) {
        _conversations.value = _conversations.value.filter { it.id != id }
        if (_activeConversationId.value == id) {
            _activeConversationId.value = _conversations.value.firstOrNull()?.id ?: createNewConversation()
        }
    }

    fun saveContent(title: String, content: String, type: String, modelUsed: String) {
        val item = SavedContent(
            id = UUID.randomUUID().toString(),
            title = title,
            content = content,
            type = type,
            modelUsed = modelUsed
        )
        _savedItems.value = listOf(item) + _savedItems.value
    }

    fun removeSavedItem(id: String) {
        _savedItems.value = _savedItems.value.filter { it.id != id }
    }

    fun addPrompt(title: String, prompt: String, category: String) {
        val item = PromptTemplate(
            id = UUID.randomUUID().toString(),
            title = title,
            promptText = prompt,
            category = category,
            iconEmoji = "✨"
        )
        _promptLibrary.value = listOf(item) + _promptLibrary.value
    }

    suspend fun sendMessage(
        conversationId: String,
        content: String,
        explicitCapability: AICapability? = null,
        specificModelId: String? = null
    ): Result<String> {
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            role = MessageRole.USER,
            content = content
        )

        // Append user message
        val currentList = _messages.value[conversationId] ?: emptyList()
        _messages.value = _messages.value + (conversationId to (currentList + userMsg))

        // Auto Router Decision
        val decision = routerEngine.route(
            prompt = content,
            explicitCapability = explicitCapability,
            mode = _routerMode.value,
            isFreeOnly = _isFreeOnly.value,
            specificModelId = specificModelId,
            currentCreditBalance = _wallet.value.balance
        )

        // Thinking placeholder
        val thinkingMsgId = "think_" + UUID.randomUUID().toString().take(6)
        val thinkingMsg = ChatMessage(
            id = thinkingMsgId,
            conversationId = conversationId,
            role = MessageRole.ASSISTANT,
            content = "Basoka AI بیر دەکاتەوە...",
            isThinking = true,
            modelId = null,
            providerId = decision.selectedProvider.id
        )
        _messages.value = _messages.value + (conversationId to (currentList + userMsg + thinkingMsg))

        // Extract previous conversation turns for multi-turn context
        val historyTurns = currentList.map { msg ->
            val role = if (msg.role == MessageRole.USER) "user" else "assistant"
            role to msg.content
        }

        val result = executionEngine.executeChat(content, decision, historyTurns)

        _wallet.value = creditEngine.getWallet()

        val botContent = result.getOrElse {
            "ببورە، هەڵەیەک لە پەیوەندیدا ڕوویدا: ${it.localizedMessage ?: "تکایە دووبارە هەوڵ بدەوە."}"
        }

        val assistantMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            role = MessageRole.ASSISTANT,
            content = botContent,
            modelId = null, // Hidden from normal user view per requirements
            providerId = decision.selectedProvider.id,
            creditsUsed = if (result.isSuccess) decision.selectedModel.creditCost else 0,
            isError = result.isFailure
        )

        val updated = (_messages.value[conversationId] ?: emptyList()).filter { it.id != thinkingMsgId } + assistantMsg
        _messages.value = _messages.value + (conversationId to updated)

        // Update conversation snippet
        _conversations.value = _conversations.value.map {
            if (it.id == conversationId) it.copy(lastSnippet = content.take(40), updatedAt = System.currentTimeMillis())
            else it
        }

        return result
    }

    fun registerCompletedVideoJob(videoResult: com.example.model.VideoResult) {
        val job = GenerationJob(
            id = "job_vid_${videoResult.id}",
            title = "ڤیدیۆ: ${videoResult.prompt.take(30)}...",
            type = "VIDEO",
            prompt = videoResult.prompt,
            status = JobStatus.COMPLETED,
            progress = 1.0f,
            resultUrlOrText = videoResult.localFilePath,
            modelId = videoResult.modelUsed,
            providerId = "google_gemini",
            creditsCharged = 12,
            completedAt = System.currentTimeMillis()
        )
        _jobs.value = listOf(job) + _jobs.value
    }

    fun registerCompletedImageJob(imageResult: com.example.model.ImageResult) {
        val job = GenerationJob(
            id = "job_img_${imageResult.id}",
            title = "وێنە: ${imageResult.prompt.take(30)}...",
            type = "IMAGE",
            prompt = imageResult.prompt,
            status = JobStatus.COMPLETED,
            progress = 1.0f,
            resultUrlOrText = imageResult.localFilePath,
            modelId = imageResult.modelUsed,
            providerId = "google_gemini",
            creditsCharged = 4,
            completedAt = System.currentTimeMillis()
        )
        _jobs.value = listOf(job) + _jobs.value
    }

    fun startVideoJob(prompt: String, durationSec: Int = 5, style: String = "سینەمایی"): GenerationJob {
        val decision = routerEngine.route(prompt, AICapability.VIDEO_GENERATION, _routerMode.value)
        val jobId = "job_vid_" + UUID.randomUUID().toString().take(6)

        val job = GenerationJob(
            id = jobId,
            title = "ڤیدیۆی $style: ${prompt.take(25)}...",
            type = "VIDEO",
            prompt = prompt,
            status = JobStatus.PROCESSING,
            progress = 0.1f,
            modelId = decision.selectedModel.id,
            providerId = decision.selectedProvider.id,
            creditsCharged = decision.estimatedCredits
        )
        _jobs.value = listOf(job) + _jobs.value
        creditEngine.deductCredits(decision.estimatedCredits)
        _wallet.value = creditEngine.getWallet()
        return job
    }

    fun updateProviderApiKey(providerId: String, apiKey: String) {
        apiKeyManager.saveKey(providerId, apiKey)
        val hasKey = apiKey.isNotBlank()
        _providers.value = _providers.value.map {
            if (it.id == providerId) {
                it.copy(
                    hasApiKey = hasKey,
                    maskedApiKey = if (hasKey) "••••••••" + apiKey.takeLast(4) else "",
                    status = if (hasKey) ProviderStatus.ONLINE else ProviderStatus.NOT_CONFIGURED
                )
            } else it
        }
    }
}
