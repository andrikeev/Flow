package flow.domain.usecase

import flow.auth.api.AuthRepository
import flow.data.api.BookmarksRepository
import flow.data.api.FavoritesRepository
import flow.data.api.SearchHistoryRepository
import flow.data.api.SuggestsRepository
import flow.data.api.TopicHistoryRepository
import flow.work.api.BackgroundService
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val backgroundService: BackgroundService,
    private val bookmarksRepository: BookmarksRepository,
    private val favoritesRepository: FavoritesRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val suggestsRepository: SuggestsRepository,
    private val topicHistoryRepository: TopicHistoryRepository,
) {
    suspend operator fun invoke() {
        authRepository.clear()
        backgroundService.stopBackgroundWorks()
        bookmarksRepository.clear()
        favoritesRepository.clear()
        searchHistoryRepository.clear()
        suggestsRepository.clear()
        topicHistoryRepository.clear()
    }
}
