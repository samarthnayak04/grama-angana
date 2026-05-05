package com.example.gramaangana

import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject
import org.json.JSONArray
import com.example.gramaangana.BuildConfig
object GeminiHelper {
    private val API_KEY = BuildConfig.GEMINI_API_KEY

    suspend fun getSuggestion(prompt: String): String {
        return withContext(Dispatchers.IO) {
            try {
                // Add "-preview" to the model name
                val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent?key=$API_KEY")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.setRequestProperty("Accept", "application/json")
                conn.doOutput = true
                conn.connectTimeout = 15000
                conn.readTimeout = 15000

                val body = """
                    {
                      "contents": [{
                        "parts": [{"text": "$prompt"}]
                      }]
                    }
                """.trimIndent()

                conn.outputStream.write(body.toByteArray(Charsets.UTF_8))

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = conn.inputStream.bufferedReader().readText()
                    val json = JSONObject(response)
                    json.getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")
                } else {
                    val error = conn.errorStream?.bufferedReader()?.readText()
                    "Error $responseCode: $error"
                }
            } catch (e: Exception) {
                "Error: ${e.message}"
            }
        }
    }
}