package com.example.llmdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : ComponentActivity() {
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LlmDemoApp()
        }
    }

    @Composable
    fun LlmDemoApp() {
        var input by remember { mutableStateOf("") }
        var response by remember { mutableStateOf("Zadej text a stiskni Odeslat") }
        val scope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Zadej dotaz") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Button(onClick = {
                scope.launch {
                    val res = queryLlm(input)
                    response = res
                }
            }) {
                Text("Odeslat")
            }
            Spacer(Modifier.height(24.dp))
            Text("Odpověď LLM:")
            Text(response)
        }
    }

    suspend fun queryLlm(prompt: String): String = withContext(Dispatchers.IO) {
        val json = JSONObject()
        json.put("prompt", prompt)
        val body = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            json.toString()
        )
        val request = Request.Builder()
            .url("http://10.0.2.2:5000/generate")
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext "Chyba serveru: ${response.code()}"
                val jsonResponse = JSONObject(response.body()!!.string())
                return@withContext jsonResponse.optString("response", "Žádná odpověď")
            }
        } catch (e: Exception) {
            return@withContext "Chyba: ${e.message}"
        }
    }
}

