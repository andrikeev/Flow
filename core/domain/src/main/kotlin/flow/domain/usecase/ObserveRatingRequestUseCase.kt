package flow.domain.usecase

import flow.data.api.repository.RatingRepository
import flow.domain.model.rating.RatingRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ObserveRatingRequestUseCase : suspend () -> Flow<RatingRequest>

internal class ObserveRatingRequestUseCaseImpl @Inject constructor(
    private val observeSearchHistoryUseCase: ObserveSearchHistoryUseCase,
    private val observeVisitedUseCase: ObserveVisitedUseCase,
    private val observeBookmarksUseCase: ObserveBookmarksUseCase,
    private val ratingRepository: RatingRepository,
) : ObserveRatingRequestUseCase {
    override suspend fun invoke(): Flow<RatingRequest> {
        return ratingRepository.observeRatingRequestDisabled()
            .flatMapLatest { isDisabled ->
                if (isDisabled || ratingRepository.getLaunchCount() > 0) {
                    flowOf(RatingRequest.Hide)
                } else {
                    combine(
                        observeSearchHistoryUseCase().map { it.size > HistoryCounter },
                        observeVisitedUseCase().map { it.size > VisitedCounter },
                        observeBookmarksUseCase().map { it.size > BookmarksCounter },
                    ) { historyCondition, visitedCondition, bookmarksCondition ->
                        if (historyCondition || visitedCondition || bookmarksCondition) {
                            RatingRequest.Show(ratingRepository.isRatingRequestPostponed())
                        } else {
                            RatingRequest.Hide
                        }
                    }
                }
            }
    }

    private companion object {
        const val HistoryCounter = 3
        const val VisitedCounter = 5
        const val BookmarksCounter = 2
    }
}
