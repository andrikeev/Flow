package flow.domain.usecase

import flow.data.api.service.SearchService
import flow.models.Page
import flow.models.search.Filter
import flow.models.topic.Torrent
import javax.inject.Inject

class GetSearchPageUseCase @Inject constructor(
    private val searchService: SearchService,
) {
    suspend operator fun invoke(filter: Filter, page: Int): Page<Torrent> {
        return searchService.search(filter, page)
    }
}
