package flow.favorites

sealed interface FavoritesSideEffect {
    data class OpenTopic(val id: String) : FavoritesSideEffect
}
