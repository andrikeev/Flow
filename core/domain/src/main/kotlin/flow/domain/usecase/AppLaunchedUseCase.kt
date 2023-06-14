package flow.domain.usecase

import flow.data.api.repository.RatingRepository
import flow.dispatchers.api.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface AppLaunchedUseCase : suspend () -> Unit

internal class AppLaunchedUseCaseImpl @Inject constructor(
    private val ratingRepository: RatingRepository,
    private val dispatchers: Dispatchers,
) : AppLaunchedUseCase {
    override suspend fun invoke() = withContext(dispatchers.default) {
        ratingRepository.setLaunchCount((ratingRepository.getLaunchCount() - 1).coerceAtLeast(0))
    }
}
