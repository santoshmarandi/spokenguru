package com.spokenenglishguru.app.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TextToSpeechManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("TTSManager", "Failed to construct TextToSpeech", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            try {
                val result = tts?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.setLanguage(Locale.UK)
                }
                tts?.setSpeechRate(0.9f)
                isInitialized = true
            } catch (e: Exception) {
                Log.e("TTSManager", "Error setting language/speech rate", e)
            }
        } else {
            Log.e("TTSManager", "TextToSpeech initialization failed with status: $status")
        }
    }

    fun speak(text: String) {
        try {
            if (isInitialized && text.isNotBlank()) {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UtteranceId_${System.currentTimeMillis()}")
            }
        } catch (e: Exception) {
            Log.e("TTSManager", "Error speaking text", e)
        }
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("TTSManager", "Error stopping TTS", e)
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e("TTSManager", "Error shutting down TTS", e)
        } finally {
            tts = null
        }
    }
}
