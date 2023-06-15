package flow.data.impl.repository

import flow.data.api.repository.RatingRepository
import flow.securestorage.SecureStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

internal class RatingRepositoryImpl @Inject constructor(
    private val secureStorage: SecureStorage,
) : RatingRepository {
    private val mutableRatingDisabledFlow = MutableSharedFlow<Boolean>()

    override suspend fun getLaunchCount(): Int {
        return secureStorage.getRatingLaunchCount()
    }

    override suspend fun setLaunchCount(value: Int) {
        secureStorage.setRatingLaunchCount(value)
    }

    override fun observeRatingRequestDisabled(): Flow<Boolean> {
        return mutableRatingDisabledFlow.onStart {
            emit(secureStorage.getRatingDisabled())
        }
    }

    override suspend fun disableRatingRequest() {
        mutableRatingDisabledFlow.emit(true)
        secureStorage.setRatingDisabled(true)
    }

    override suspend fun isRatingRequestPostponed(): Boolean {
        return secureStorage.getRatingPostponed()
    }

    override suspend fun postponeRatingRequest() {
        secureStorage.setRatingPostponed(true)
    }
}
