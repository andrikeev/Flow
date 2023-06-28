package flow.domain.usecase

import flow.data.api.repository.RatingRepository
import flow.domain.model.rating.RatingRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

interface ObserveRatingRequestUseCase : suspend () -> Flow<RatingRequest>

internal class ObserveRatingRequestUseCaseImpl @Inject constructor(
    private val observeSearchHistoryUseCase: ObserveSearchHistoryUseCase,
    private val observeVisitedUseCase: ObserveVisitedUseCase,
    private val observeBookmarksUseCase: ObserveBookmarksUseCase,
    private val ratingRepository: RatingRepository,
) : ObserveRatingRequestUseCase {
    override suspend fun invoke(): Flow<RatingRequest> {
        return combine<Boolean, Boolean>(
            flows = listOf(
                ratingRepository.observeRatingRequestDisabled().map(Boolean::not),
                ratingRepository.observeLaunchCount().map { it <= 0 },
                combine<Boolean, Boolean>(
                    flows = listOf(
                        observeSearchHistoryUseCase().map { it.size > HistoryCounter },
                        observeVisitedUseCase().map { it.size > VisitedCounter },
                        observeBookmarksUseCase().map { it.size > BookmarksCounter },
                    ),
                    transform = { conditions -> conditions.any { it } },
                ),
            ),
            transform = { conditions -> conditions.all { it } },
        ).mapLatest { show ->
            if (show) {
                RatingRequest.Show(ratingRepository.isRatingRequestPostponed())
            } else {
                RatingRequest.Hide
            }
        }
    }

    private companion object {
        const val HistoryCounter = 3
        const val VisitedCounter = 5
        const val BookmarksCounter = 2
    }
}
