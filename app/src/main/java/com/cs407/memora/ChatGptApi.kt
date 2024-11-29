package com.cs407.memora

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import okhttp3.Request


// ChatGptApi.kt
object ChatGptApi {
    private const val API_KEY = "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"
    private const val BASE_URL = "https://api.openai.com/v1/chat/completions"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $API_KEY")
                .build()
            chain.proceed(request)
        }
        .build()

    suspend fun sendQuestion(question: String): String {
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

                // Parse JSON response and extract ChatGPT's reply
                val jsonResponse = JSONObject(response.body?.string() ?: "")
                jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
            }
        }
    }
}