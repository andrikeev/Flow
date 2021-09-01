package me.rutrackersearch.app.ui.search.input

import androidx.compose.ui.text.input.TextFieldValue
import me.rutrackersearch.domain.entity.search.Filter
import me.rutrackersearch.domain.entity.search.Suggest

data class SearchInputState(
    val filter: Filter,
    val searchInput: TextFieldValue,
    val suggests: List<Suggest>,
) {
    val isClearButtonVisible = searchInput.text.isNotEmpty()
}
