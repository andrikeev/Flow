package flow.forum.category

internal sealed interface CategorySideEffect {
    data class OpenCategory(val categoryId: String) : CategorySideEffect
    data class OpenSearch(val categoryId: String) : CategorySideEffect
    data class OpenTopic(val id: String) : CategorySideEffect
    data object Back : CategorySideEffect
    data object OpenLogin : CategorySideEffect
    data object ShowFavoriteToggleError : CategorySideEffect
    data object ShowLoginDialog : CategorySideEffect
}
