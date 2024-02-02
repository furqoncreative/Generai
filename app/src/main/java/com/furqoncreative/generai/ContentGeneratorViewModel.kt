package com.furqoncreative.generai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContentGeneratorViewModel(
    private val generativeModel: GenerativeModel
) : ViewModel() {

    private val _uiState: MutableStateFlow<ContentGeneratorUiState> =
        MutableStateFlow(ContentGeneratorUiState.Initial)
    val uiState: StateFlow<ContentGeneratorUiState> =
        _uiState.asStateFlow()

    fun generateContent(
        topic: String,
        tone: Tone,
        format: Format,
        length: Length,
    ) {
        _uiState.value = ContentGeneratorUiState.Loading

        val prompt = """
            Compose a ${length.prompt} ${format.prompt} addressing $topic, employing a ${tone.prompt} style
        """.trimIndent()

        viewModelScope.launch {
            try {
                val response = generativeModel.generateContent(prompt)
                response.text?.let { outputContent ->
                    _uiState.value = ContentGeneratorUiState.Success(outputContent)
                }
            } catch (e: Exception) {
                _uiState.value = ContentGeneratorUiState.Error(e.localizedMessage ?: "")
            }
        }
    }
}