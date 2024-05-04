package com.example.hooghirmohammedfinalproject

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class geminiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gemini)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fun askGemini(view: View) {

            // You can use safety settings to adjust the likelihood of getting responses that may be considered harmful.
            // By default, safety settings block content with medium and/or high probability of being unsafe content across all dimensions.
            val harassmentSafety = SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH)
            val hateSpeechSafety =
                SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE)


            val generativeModel = GenerativeModel(
                // For text-only input, use the gemini-pro model
                // gemini-pro-vision accepts both text and images and inputs
                // Use streaming with multi-turn conversations (like chat)
                modelName = "gemini-pro",
                apiKey = "NpX8VLS2UGJeuzpUeAGf70SfzAJuSP8Z",
                safetySettings = listOf(harassmentSafety, hateSpeechSafety)
            )


            // This is probably not the best approach as it uses Main thread
            CoroutineScope(Dispatchers.Main + CoroutineName("MyScope")).launch {
                val promptWidget = findViewById<EditText>(R.id.prompt)
                if (promptWidget.text.isEmpty()) {
                    return@launch
                }

                // Ticketmaster event description generation
                val eventPrompt = "Describe an upcoming event listed on Ticketmaster."
                val eventDescriptionResponse = generativeModel.generateContent(eventPrompt)
                val eventDescription = eventDescriptionResponse.text
                Log.d(TAG, "Generated Ticketmaster Event Description: $eventDescription")

            }
        }
    }
}