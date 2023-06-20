package flow.data.api.repository

import kotlinx.coroutines.flow.Flow

interface RatingRepository {
    suspend fun getLaunchCount(): Int
    fun observeLaunchCount(): Flow<Int>
    suspend fun setLaunchCount(value: Int)
    fun observeRatingRequestDisabled(): Flow<Boolean>
    suspend fun disableRatingRequest()
    suspend fun isRatingRequestPostponed(): Boolean
    suspend fun postponeRatingRequest()
}
