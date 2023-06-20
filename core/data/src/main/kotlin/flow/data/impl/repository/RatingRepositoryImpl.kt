package flow.data.impl.repository

import flow.data.api.repository.RatingRepository
import flow.securestorage.SecureStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

internal class RatingRepositoryImpl @Inject constructor(
    private val secureStorage: SecureStorage,
) : RatingRepository {
    private val mutableRatingDisabledFlow: MutableStateFlow<Boolean> by lazy {
        MutableStateFlow(secureStorage.getRatingDisabled())
    }
    private val mutableLaunchCountFlow: MutableStateFlow<Int> by lazy {
        MutableStateFlow(secureStorage.getRatingLaunchCount())
    }

    override suspend fun getLaunchCount(): Int {
        return mutableLaunchCountFlow.value
    }

    override fun observeLaunchCount(): Flow<Int> {
        return mutableLaunchCountFlow.asSharedFlow()
    }

    override suspend fun setLaunchCount(value: Int) {
        mutableLaunchCountFlow.emit(value)
        secureStorage.setRatingLaunchCount(value)
    }

    override fun observeRatingRequestDisabled(): Flow<Boolean> {
        return mutableRatingDisabledFlow.asSharedFlow()
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
