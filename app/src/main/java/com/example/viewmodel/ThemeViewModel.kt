package com.example.viewmodel

import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.ai.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

data class ThemeState(
    val lockStyle: Int = 0,
    val statusBar: Int = 0,
    val icons: Int = 0,
    val messaging: Int = 0,
    val dialer: Int = 0,
    val favorites: Int = 0,
    val bootAnimation: Int = 0,
    val generatedWallpaperBase64: String? = null
)

class ThemeViewModel : ViewModel() {
    private val _themeState = MutableStateFlow(ThemeState())
    val themeState: StateFlow<ThemeState> = _themeState.asStateFlow()

    private val _generationState = MutableStateFlow<GenerationState>(GenerationState.Idle)
    val generationState: StateFlow<GenerationState> = _generationState.asStateFlow()

    private val _analysisState = MutableStateFlow<AnalysisState>(AnalysisState.Idle)
    val analysisState: StateFlow<AnalysisState> = _analysisState.asStateFlow()

    fun updateThemeCategory(category: String, index: Int) {
        val current = _themeState.value
        _themeState.value = when (category) {
            "lockStyle" -> current.copy(lockStyle = index)
            "statusBar" -> current.copy(statusBar = index)
            "icons" -> current.copy(icons = index)
            "messaging" -> current.copy(messaging = index)
            "dialer" -> current.copy(dialer = index)
            "favorites" -> current.copy(favorites = index)
            "bootAnimation" -> current.copy(bootAnimation = index)
            else -> current
        }
    }

    fun generateImage(prompt: String, quality: String, aspectRatio: String, size: String) {
        _generationState.value = GenerationState.Loading
        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                    generationConfig = GenerationConfig(
                        imageConfig = ImageConfig(aspectRatio = aspectRatio, imageSize = size),
                        responseModalities = listOf("TEXT", "IMAGE")
                    )
                )

                val response = if (quality == "Pro") {
                    RetrofitClient.service.generateImagePro(apiKey, request)
                } else {
                    RetrofitClient.service.generateImageFlash(apiKey, request)
                }

                val inlineData = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull { it.inlineData != null }?.inlineData
                
                if (inlineData != null) {
                    _generationState.value = GenerationState.Success(inlineData.data)
                    _themeState.value = _themeState.value.copy(generatedWallpaperBase64 = inlineData.data)
                } else {
                    _generationState.value = GenerationState.Error("No image returned")
                }
            } catch (e: Exception) {
                _generationState.value = GenerationState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun analyzeImage(bitmap: Bitmap, prompt: String) {
        _analysisState.value = AnalysisState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val base64 = bitmap.toBase64()
                val apiKey = BuildConfig.GEMINI_API_KEY
                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(
                            parts = listOf(
                                Part(text = prompt),
                                Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64))
                            )
                        )
                    )
                )

                val response = RetrofitClient.service.generateContentPro(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No analysis text"
                
                withContext(Dispatchers.Main) {
                    _analysisState.value = AnalysisState.Success(text)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _analysisState.value = AnalysisState.Error(e.message ?: "Unknown error")
                }
            }
        }
    }

    fun clearAnalysisState() {
        _analysisState.value = AnalysisState.Idle
    }

    private fun Bitmap.toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}

sealed class GenerationState {
    object Idle : GenerationState()
    object Loading : GenerationState()
    data class Success(val base64Image: String) : GenerationState()
    data class Error(val message: String) : GenerationState()
}

sealed class AnalysisState {
    object Idle : AnalysisState()
    object Loading : AnalysisState()
    data class Success(val text: String) : AnalysisState()
    data class Error(val message: String) : AnalysisState()
}
