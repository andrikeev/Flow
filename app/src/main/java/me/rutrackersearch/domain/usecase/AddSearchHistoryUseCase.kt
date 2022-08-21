package me.rutrackersearch.domain.usecase

import me.rutrackersearch.models.search.Filter
import me.rutrackersearch.domain.repository.SearchHistoryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddSearchHistoryUseCase @Inject constructor(
    private val repository: SearchHistoryRepository,
) {
    suspend operator fun invoke(filter: Filter) {
        repository.addSearch(filter)
    }
}
