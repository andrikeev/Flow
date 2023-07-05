package flow.forum.category

internal sealed interface CategorySideEffect {
    object Back : CategorySideEffect
    data class OpenCategory(val categoryId: String) : CategorySideEffect
    data class OpenSearch(val categoryId: String) : CategorySideEffect
    data class OpenTopic(val id: String) : CategorySideEffect
    object ShowLoginDialog : CategorySideEffect
    object OpenLogin : CategorySideEffect
}
