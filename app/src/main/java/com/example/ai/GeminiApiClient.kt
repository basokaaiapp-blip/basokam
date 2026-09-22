package com.example.ai

import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class VideoPollResponse(
    val isDone: Boolean,
    val progress: Float = 0.5f,
    val videoUri: String? = null,
    val videoBase64: String? = null,
    val errorMessage: String? = null
)

object GeminiApiClient {
    private const val TAG = "GeminiApiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    var customApiKey: String? = null

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()

    const val SMART_CHAT_SYSTEM_INSTRUCTION = """تۆ Basoka AI ـیت، یاریدەدەری زیرەکی کوردی.
پێڕەوی ئەم یاسایانە بە ووردی بکە:
١. هەمیشە بە زمانی کوردیی سۆرانیی ڕوون، پاراو، جوان و سروشتی وەڵام بدەرەوە.
٢. ڕاستەوخۆ داواکاری و ئەرکەکە جێبەجێ بکە. پێویست بە پرسیاری زیادە و دووبارە ناکات.
٣. هەرگیز لە کۆتایی وەڵامەکانت مەپرسە "دەتەوێت بەردەوام بم؟"، "دەتەوێت شتی تر بکەم؟"، "ئەگەر دەتەوێت..." یان "ئایا دەتەوێت ئەمە بکەم؟". وەڵامەکەت بە شێوەیەکی سروشتی کۆتایی پێ بهێنە.
٤. هەمیشە دەق و بەستێنی گفتوگۆکانی پێشوو لەبەرچاو بگرە. ئەگەر بەکارهێنەر وتی "ئەمەش بکە"، "کورتتری بکەوە"، "هەمان شێوە بەڵام بە ئینگلیزی"، ئەوا ڕاستەوخۆ دەقی پێشوو لەبەرچاو بگرە و داواکارییەکە جێبەجێ بکە بەبێ دووبارە پرسیارکردن.
٥. هەرگیز ناوی مۆدێل و کۆمپانیاکانی وەکو Gemini، OpenAI، Claude، GPT، یان زانیاری تەکنیکی مەهێنە. ناسنامەی تۆ تەنها Basoka AI ـیە."""

    fun getApiKey(): String {
        if (!customApiKey.isNullOrBlank()) return customApiKey!!
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key.isNullOrBlank() || key == "MY_GEMINI_API_KEY") "" else key
        } catch (_: Exception) {
            ""
        }
    }

    fun isConfigured(): Boolean {
        return getApiKey().isNotBlank()
    }

    suspend fun generateText(
        modelName: String = "gemini-3.5-flash",
        prompt: String,
        systemInstruction: String = SMART_CHAT_SYSTEM_INSTRUCTION,
        conversationTurns: List<Pair<String, String>> = emptyList()
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) {
            return@withContext Result.failure(
                IllegalStateException("کلیلەکە دانەنراوە. تکایە لە بەشی ڕێکخستندا کلیل بنووسە.")
            )
        }

        val resolvedModel = when (modelName) {
            "gemini-3.5-flash", "gemini-flash" -> "gemini-3.5-flash"
            "gemini-3.1-pro-preview", "gemini-pro" -> "gemini-3.1-pro-preview"
            else -> "gemini-3.5-flash"
        }

        try {
            val endpoint = "$BASE_URL/$resolvedModel:generateContent?key=$apiKey"
            val root = JSONObject()

            // System instruction
            if (systemInstruction.isNotBlank()) {
                val sysContent = JSONObject()
                val sysPart = JSONObject().put("text", systemInstruction)
                sysContent.put("parts", JSONArray().put(sysPart))
                root.put("systemInstruction", sysContent)
            }

            // Conversation history turns for multi-turn context
            val contentsArray = JSONArray()
            if (conversationTurns.isNotEmpty()) {
                for ((role, text) in conversationTurns) {
                    val turnObj = JSONObject()
                    turnObj.put("role", if (role == "model" || role == "assistant") "model" else "user")
                    val part = JSONObject().put("text", text)
                    turnObj.put("parts", JSONArray().put(part))
                    contentsArray.put(turnObj)
                }
            }

            // Current prompt
            val currentTurn = JSONObject()
            currentTurn.put("role", "user")
            val currentPart = JSONObject().put("text", prompt)
            currentTurn.put("parts", JSONArray().put(currentPart))
            contentsArray.put(currentTurn)

            root.put("contents", contentsArray)

            val requestBody = root.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(endpoint)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                Log.e(TAG, "Gemini API error code: ${response.code} body: $responseBody")
                return@withContext Result.failure(
                    Exception("هەڵەی API (کۆد ${response.code}): خزمەتگوزاری وەڵامی نەدایەوە.")
                )
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val contentObj = candidate.optJSONObject("content")
                val parts = contentObj?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val textBuilder = StringBuilder()
                    for (i in 0 until parts.length()) {
                        val part = parts.getJSONObject(i)
                        val text = part.optString("text")
                        if (text.isNotEmpty()) {
                            textBuilder.append(text)
                        }
                    }
                    return@withContext Result.success(textBuilder.toString())
                }
            }

            Result.failure(Exception("وەڵامێکی دروست وەرنەگیرا."))
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Gemini request", e)
            Result.failure(Exception("هەڵە لە پەیوەندی تۆڕدا: ${e.localizedMessage ?: "نەزانراو"}"))
        }
    }

    suspend fun generateImage(
        prompt: String,
        aspectRatio: String = "1:1"
    ): Result<ByteArray> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) {
            return@withContext Result.failure(
                IllegalStateException("کلیلی API دانەنراوە. تکایە لە بەشی ڕێکخستن کلیل بنووسە.")
            )
        }

        // Method 1: Try gemini-2.5-flash-image with responseModalities ["TEXT", "IMAGE"]
        try {
            val endpoint = "$BASE_URL/gemini-2.5-flash-image:generateContent?key=$apiKey"
            val root = JSONObject()

            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            val partsArray = JSONArray()
            partsArray.put(JSONObject().put("text", prompt))
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            root.put("contents", contentsArray)

            val genConfig = JSONObject()
            val imgConfig = JSONObject()
            imgConfig.put("aspectRatio", aspectRatio)
            imgConfig.put("imageSize", "1K")
            genConfig.put("imageConfig", imgConfig)
            genConfig.put("responseModalities", JSONArray().put("TEXT").put("IMAGE"))
            root.put("generationConfig", genConfig)

            val request = Request.Builder()
                .url(endpoint)
                .post(root.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string().orEmpty()

            if (response.isSuccessful) {
                val json = JSONObject(responseBody)
                val candidates = json.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null) {
                        for (i in 0 until parts.length()) {
                            val part = parts.getJSONObject(i)
                            val inlineData = part.optJSONObject("inlineData")
                            if (inlineData != null) {
                                val base64Data = inlineData.optString("data")
                                if (base64Data.isNotEmpty()) {
                                    val bytes = Base64.decode(base64Data, Base64.DEFAULT)
                                    return@withContext Result.success(bytes)
                                }
                            }
                        }
                    }
                }
            } else {
                Log.w(TAG, "gemini-2.5-flash-image returned code ${response.code}: $responseBody")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error trying gemini-2.5-flash-image, falling back to Imagen 3", e)
        }

        // Method 2: Try Imagen 3 (imagen-3.0-generate-002:predict)
        try {
            val endpoint = "$BASE_URL/imagen-3.0-generate-002:predict?key=$apiKey"
            val root = JSONObject()

            val instances = JSONArray()
            instances.put(JSONObject().put("prompt", prompt))
            root.put("instances", instances)

            val parameters = JSONObject()
            parameters.put("sampleCount", 1)
            parameters.put("aspectRatio", aspectRatio)
            root.put("parameters", parameters)

            val request = Request.Builder()
                .url(endpoint)
                .post(root.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string().orEmpty()

            if (response.isSuccessful) {
                val json = JSONObject(responseBody)
                val predictions = json.optJSONArray("predictions")
                if (predictions != null && predictions.length() > 0) {
                    val pred = predictions.getJSONObject(0)
                    val base64 = pred.optString("bytesBase64Encoded")
                    if (base64.isNotEmpty()) {
                        val bytes = Base64.decode(base64, Base64.DEFAULT)
                        return@withContext Result.success(bytes)
                    }
                }
            } else {
                Log.e(TAG, "Imagen 3 error code ${response.code}: $responseBody")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in Imagen 3 API call", e)
        }

        Result.failure(Exception("دروستکردنی وێنە سەرکەوتوو نەبوو. تکایە دووبارە هەوڵ بدەوە یان کلیلەکەت لە بەشی ڕێکخستن پشکنین بکە."))
    }

    suspend fun startVideoGeneration(
        prompt: String,
        aspectRatio: String = "16:9",
        referenceImageBytes: ByteArray? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) {
            return@withContext Result.failure(
                IllegalStateException("کلیلی API دانەنراوە. تکایە لە بەشی ڕێکخستن کلیل بنووسە.")
            )
        }

        try {
            val endpoint = "$BASE_URL/veo-2.0-generate-001:generateVideos?key=$apiKey"
            val root = JSONObject()
            root.put("prompt", prompt)

            val config = JSONObject()
            config.put("numberOfVideos", 1)
            config.put("resolution", "720p")
            config.put("aspectRatio", aspectRatio)
            root.put("config", config)

            if (referenceImageBytes != null && referenceImageBytes.isNotEmpty()) {
                val imgObj = JSONObject()
                imgObj.put("imageBytes", Base64.encodeToString(referenceImageBytes, Base64.NO_WRAP))
                imgObj.put("mimeType", "image/jpeg")
                root.put("image", imgObj)
            }

            val request = Request.Builder()
                .url(endpoint)
                .post(root.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                Log.e(TAG, "Veo API error code ${response.code}: $responseBody")
                return@withContext Result.failure(
                    Exception("هەڵەی دەستپێکردنی دروستکردنی ڤیدیۆ (کۆد ${response.code}).")
                )
            }

            val json = JSONObject(responseBody)
            val operationName = json.optString("name")
            if (operationName.isNotEmpty()) {
                return@withContext Result.success(operationName)
            }

            Result.failure(Exception("ناسنامەی کارەکە وەرنەگیرا."))
        } catch (e: Exception) {
            Log.e(TAG, "Exception starting video generation", e)
            Result.failure(Exception("هەڵە لە پەیوەندی تۆڕدا: ${e.localizedMessage ?: "نەزانراو"}"))
        }
    }

    suspend fun pollVideoOperation(operationName: String): Result<VideoPollResponse> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) {
            return@withContext Result.failure(IllegalStateException("کلیلی API دانەنراوە."))
        }

        try {
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/$operationName?key=$apiKey"
            val request = Request.Builder()
                .url(endpoint)
                .get()
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                Log.e(TAG, "Poll error code ${response.code}: $responseBody")
                return@withContext Result.failure(Exception("هەڵە لە پشکنینی کارەکە (کۆد ${response.code})."))
            }

            val json = JSONObject(responseBody)
            val isDone = json.optBoolean("done", false)

            if (!isDone) {
                return@withContext Result.success(
                    VideoPollResponse(isDone = false, progress = 0.45f)
                )
            }

            val errorObj = json.optJSONObject("error")
            if (errorObj != null) {
                val msg = errorObj.optString("message", "هەڵەی نەزانراو لە دروستکردندا.")
                return@withContext Result.success(
                    VideoPollResponse(isDone = true, errorMessage = msg)
                )
            }

            val responseObj = json.optJSONObject("response")
            val generatedVideos = responseObj?.optJSONArray("generatedVideos")
            if (generatedVideos != null && generatedVideos.length() > 0) {
                val first = generatedVideos.getJSONObject(0)
                val videoObj = first.optJSONObject("video")
                val uri = videoObj?.optString("uri")
                val base64 = videoObj?.optString("bytesBase64Encoded")

                return@withContext Result.success(
                    VideoPollResponse(
                        isDone = true,
                        progress = 1.0f,
                        videoUri = uri,
                        videoBase64 = base64
                    )
                )
            }

            Result.failure(Exception("ڤیدیۆی بەرهەمهاتوو لە وەڵامەکەدا نەدۆزرایەوە."))
        } catch (e: Exception) {
            Log.e(TAG, "Exception polling video operation", e)
            Result.failure(Exception("هەڵە لە وەرگرتنی زانیاری ڤیدیۆ: ${e.localizedMessage}"))
        }
    }

    suspend fun downloadVideoBytes(videoUri: String): Result<ByteArray> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        val url = if (videoUri.contains("key=")) videoUri else {
            if (videoUri.contains("?")) "$videoUri&key=$apiKey" else "$videoUri?key=$apiKey"
        }

        try {
            val request = Request.Builder().url(url).get().build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("شکست لە داگرتنی ڤیدیۆ (کۆد ${response.code})"))
            }
            val bytes = response.body?.bytes()
            if (bytes != null && bytes.isNotEmpty()) {
                Result.success(bytes)
            } else {
                Result.failure(Exception("پەڕگەی ڤیدیۆ بەتاڵە."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
