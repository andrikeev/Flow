package me.rutrackersearch.app.ui.search.input

import androidx.compose.ui.text.input.TextFieldValue
import me.rutrackersearch.models.search.Filter
import me.rutrackersearch.models.search.Suggest

data class SearchInputState(
    val filter: Filter,
    val searchInput: TextFieldValue,
    val suggests: List<Suggest>,
) {
    val isClearButtonVisible = searchInput.text.isNotEmpty()
}
