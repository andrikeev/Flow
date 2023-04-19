package flow.forum

internal sealed interface ForumSideEffect {
    data class OpenCategory(val categoryId: String) : ForumSideEffect
}
