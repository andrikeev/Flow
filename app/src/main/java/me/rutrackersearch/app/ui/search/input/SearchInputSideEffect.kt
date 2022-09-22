package me.rutrackersearch.app.ui.search.input

import me.rutrackersearch.models.search.Filter

sealed interface SearchInputSideEffect {
    object Back : SearchInputSideEffect
    object HideKeyboard : SearchInputSideEffect
    data class OpenSearch(val filter: Filter) : SearchInputSideEffect
}
