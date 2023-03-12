package flow.testing.service

import flow.data.api.service.SearchService
import flow.models.Page
import flow.models.search.Filter
import flow.models.topic.Torrent

class TestSearchService : SearchService {
    override suspend fun search(filter: Filter, page: Int): Page<Torrent> {
        TODO("Not yet implemented")
    }
}
