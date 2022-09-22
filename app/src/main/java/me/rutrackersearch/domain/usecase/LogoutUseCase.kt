package me.rutrackersearch.domain.usecase

import me.rutrackersearch.domain.repository.AccountRepository
import me.rutrackersearch.domain.repository.BookmarksRepository
import me.rutrackersearch.domain.repository.FavoritesRepository
import me.rutrackersearch.domain.repository.SearchHistoryRepository
import me.rutrackersearch.domain.repository.SuggestsRepository
import me.rutrackersearch.domain.repository.TopicHistoryRepository
import me.rutrackersearch.domain.service.LoadFavoritesService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogoutUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val suggestsRepository: SuggestsRepository,
    private val favoritesRepository: FavoritesRepository,
    private val bookmarksRepository: BookmarksRepository,
    private val topicHistoryRepository: TopicHistoryRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val loadFavoritesService: LoadFavoritesService,
) {
    suspend operator fun invoke() {
        accountRepository.clear()
        suggestsRepository.clear()
        favoritesRepository.clear()
        bookmarksRepository.clear()
        topicHistoryRepository.clear()
        searchHistoryRepository.clear()
        loadFavoritesService.stop()
    }
}
