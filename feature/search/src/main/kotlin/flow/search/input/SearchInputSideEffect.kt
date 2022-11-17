package flow.search.input

import flow.models.search.Filter

internal sealed interface SearchInputSideEffect {
    object Back : SearchInputSideEffect
    object HideKeyboard : SearchInputSideEffect
    data class OpenSearch(val filter: Filter) : SearchInputSideEffect
}
