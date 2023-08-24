package flow.search.input

import flow.models.search.Filter

internal sealed interface SearchInputSideEffect {
    data object Back : SearchInputSideEffect
    data object HideKeyboard : SearchInputSideEffect
    data class OpenSearch(val filter: Filter) : SearchInputSideEffect
}
