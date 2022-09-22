package me.rutrackersearch.app.ui.search.input

import androidx.compose.ui.text.input.TextFieldValue
import me.rutrackersearch.models.search.Suggest

data class SearchInputState(
    val searchInput: TextFieldValue = TextFieldValue(),
    val suggests: List<Suggest> = emptyList(),
) {
    val showClearButton = searchInput.text.isNotEmpty()
}
