package flow.search.input

import androidx.compose.ui.text.input.TextFieldValue
import flow.models.search.Suggest

internal sealed interface SearchInputAction {
    data class InputChanged(val value: TextFieldValue) : SearchInputAction
    data class SuggestClick(val suggest: Suggest) : SearchInputAction
    data class SuggestSelected(val suggest: Suggest) : SearchInputAction
    object BackClick : SearchInputAction
    object ClearInputClick : SearchInputAction
    object SubmitClick : SearchInputAction
}
