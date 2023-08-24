package flow.domain.model.rating

sealed interface RatingRequest {
    data object Hide : RatingRequest
    data class Show(val allowDisableForever: Boolean) : RatingRequest
}
