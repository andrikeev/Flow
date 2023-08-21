package flow.search.input

import androidx.compose.ui.text.input.TextFieldValue
import flow.models.search.Suggest

internal sealed interface SearchInputAction {
    data object BackClick : SearchInputAction
    data object ClearInputClick : SearchInputAction
    data class InputChanged(val value: TextFieldValue) : SearchInputAction
    data object SubmitClick : SearchInputAction
    data class SuggestEditClick(val suggest: Suggest) : SearchInputAction
    data class SuggestClick(val suggest: Suggest) : SearchInputAction
}
