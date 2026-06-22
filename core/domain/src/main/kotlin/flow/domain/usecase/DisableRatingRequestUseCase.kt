package flow.domain.usecase

import flow.data.api.repository.RatingRepository
import flow.dispatchers.api.Dispatchers
import kotlinx.coroutines.withContext

interface DisableRatingRequestUseCase : suspend () -> Unit

internal class DisableRatingRequestUseCaseImpl(
    private val ratingRepository: RatingRepository,
    private val dispatchers: Dispatchers,
) : DisableRatingRequestUseCase {
    override suspend fun invoke() = withContext(dispatchers.default) {
        ratingRepository.disableRatingRequest()
    }
}
