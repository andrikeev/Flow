package me.rutrackersearch.domain.usecase

import me.rutrackersearch.domain.entity.Page
import me.rutrackersearch.domain.entity.search.Filter
import me.rutrackersearch.domain.entity.topic.Torrent
import me.rutrackersearch.domain.repository.SearchRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadSearchPageUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
) {
    suspend operator fun invoke(filter: Filter, page: Int): Page<Torrent> {
        return searchRepository.search(filter, page)
    }
}
