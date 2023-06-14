package flow.rating

sealed interface RatingSideEffect {
    data class OpenLink(val link: String) : RatingSideEffect
}
