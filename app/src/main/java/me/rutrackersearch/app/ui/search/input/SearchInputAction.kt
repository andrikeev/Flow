package me.rutrackersearch.app.ui.search.input

import androidx.compose.ui.text.input.TextFieldValue
import me.rutrackersearch.models.search.Suggest

sealed interface SearchInputAction {
    object BackClick : SearchInputAction
    object ClearInputClick : SearchInputAction
    data class SubmitClick(val query: String?) : SearchInputAction
    data class SuggestClick(val suggest: Suggest) : SearchInputAction
    data class InputChanged(val value: TextFieldValue) : SearchInputAction
    data class SuggestSelected(val suggest: Suggest) : SearchInputAction
}
