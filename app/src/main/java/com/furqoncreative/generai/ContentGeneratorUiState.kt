package com.furqoncreative.generai

/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface ContentGeneratorUiState {

    /**
     * Empty state when the screen is first shown
     */
    data object Initial : ContentGeneratorUiState

    /**
     * Still loading
     */
    data object Loading : ContentGeneratorUiState

    /**
     * Content has been generated
     */
    data class Success(
        val outputText: String
    ) : ContentGeneratorUiState

    /**
     * There was an error generating content
     */
    data class Error(
        val errorMessage: String
    ) : ContentGeneratorUiState
}