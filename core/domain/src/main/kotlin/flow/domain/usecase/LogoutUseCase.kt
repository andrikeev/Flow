package flow.domain.usecase

import flow.auth.api.AuthService
import flow.data.api.repository.BookmarksRepository
import flow.data.api.repository.FavoritesRepository
import flow.data.api.repository.SearchHistoryRepository
import flow.data.api.repository.SuggestsRepository
import flow.data.api.repository.VisitedRepository
import flow.dispatchers.api.Dispatchers
import flow.work.api.BackgroundService
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authService: AuthService,
    private val backgroundService: BackgroundService,
    private val bookmarksRepository: BookmarksRepository,
    private val favoritesRepository: FavoritesRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val suggestsRepository: SuggestsRepository,
    private val visitedRepository: VisitedRepository,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke() {
        withContext(dispatchers.default) {
            backgroundService.stopBackgroundWorks()
            authService.logout()
            bookmarksRepository.clear()
            favoritesRepository.clear()
            searchHistoryRepository.clear()
            suggestsRepository.clear()
            visitedRepository.clear()
        }
    }
}
