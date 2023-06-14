package flow.domain.usecase

import flow.data.api.repository.RatingRepository
import flow.dispatchers.api.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface PostponeRatingRequestUseCase : suspend () -> Unit

internal class PostponeRatingRequestUseCaseImpl @Inject constructor(
    private val ratingRepository: RatingRepository,
    private val dispatchers: Dispatchers,
) : PostponeRatingRequestUseCase {
    override suspend fun invoke() = withContext(dispatchers.default) {
        ratingRepository.setLaunchCount(PostponedLaunchCount)
        ratingRepository.postponeRatingRequest()
    }

    companion object {
        const val PostponedLaunchCount = 10
    }
}
