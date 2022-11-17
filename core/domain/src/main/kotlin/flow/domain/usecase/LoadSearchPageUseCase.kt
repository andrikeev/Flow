package flow.domain.usecase

import flow.data.api.SearchRepository
import flow.models.Page
import flow.models.search.Filter
import flow.models.topic.Torrent
import javax.inject.Inject

class LoadSearchPageUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
) {
    suspend operator fun invoke(filter: Filter, page: Int): Page<Torrent> {
        return searchRepository.search(filter, page)
    }
}
