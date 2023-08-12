package flow.visited

internal sealed interface VisitedSideEffect {
    data class OpenTopic(val id: String) : VisitedSideEffect
    data object ShowFavoriteToggleError : VisitedSideEffect
}
