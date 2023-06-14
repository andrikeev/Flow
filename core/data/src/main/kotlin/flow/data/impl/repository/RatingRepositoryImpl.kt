package flow.data.impl.repository

import flow.data.api.repository.RatingRepository
import flow.securestorage.SecureStorage
import javax.inject.Inject

internal class RatingRepositoryImpl @Inject constructor(
    private val secureStorage: SecureStorage,
) : RatingRepository {
    override suspend fun getLaunchCount(): Int {
        return secureStorage.getRatingLaunchCount()
    }

    override suspend fun setLaunchCount(value: Int) {
        secureStorage.setRatingLaunchCount(value)
    }

    override suspend fun isRatingRequestDisabled(): Boolean {
        return secureStorage.getRatingDisabled()
    }

    override suspend fun disableRatingRequest() {
        secureStorage.setRatingDisabled(true)
    }

    override suspend fun isRatingRequestPostponed(): Boolean {
        return secureStorage.getRatingPostponed()
    }

    override suspend fun postponeRatingRequest() {
        secureStorage.setRatingPostponed(true)
    }
}
