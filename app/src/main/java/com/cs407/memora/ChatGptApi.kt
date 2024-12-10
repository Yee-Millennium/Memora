package com.cs407.memora

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import okhttp3.Request

object ChatGptApi {
    private var apiKey: String? = null
    private const val BASE_URL = "https://api.openai.com/v1/chat/completions"

    // Simple function to initialize API key from SharedPreferences
    fun init(context: Context) {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        apiKey = prefs.getString("openai_api_key", null)
    }

    // Simple function to save API key
    fun saveApiKey(context: Context, key: String) {
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            .edit()
            .putString("openai_api_key", key)
            .apply()
        apiKey = key
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${apiKey ?: throw IllegalStateException("API key not set")}")
                .build()
            chain.proceed(request)
        }
        .build()

    suspend fun sendQuestion(question: String): String {
        if (apiKey == null) {
            throw IllegalStateException("Please set your API key first")
        }

        val requestBody = """
            {
                "model": "gpt-3.5-turbo",
                "messages": [
                    {
                        "role": "user",
                        "content": "$question"
                    }
                ]
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(BASE_URL)
            .post(requestBody.toRequestBody("application/json".toMediaType()))
            .build()

        return withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected response $response")

                val jsonResponse = JSONObject(response.body?.string() ?: "")
                jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
            }
        }
    }
}