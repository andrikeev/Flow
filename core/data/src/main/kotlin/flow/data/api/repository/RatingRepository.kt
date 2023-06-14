package flow.data.api.repository

interface RatingRepository {
    suspend fun getLaunchCount(): Int
    suspend fun setLaunchCount(value: Int)
    suspend fun isRatingRequestDisabled(): Boolean
    suspend fun disableRatingRequest()
    suspend fun isRatingRequestPostponed(): Boolean
    suspend fun postponeRatingRequest()
}
