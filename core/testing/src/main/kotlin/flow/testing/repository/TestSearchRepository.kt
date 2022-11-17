package flow.testing.repository

import flow.data.api.SearchRepository
import flow.models.Page
import flow.models.search.Filter
import flow.models.topic.Torrent

class TestSearchRepository : SearchRepository {
    override suspend fun search(filter: Filter, page: Int): Page<Torrent> {
        TODO("Not yet implemented")
    }
}
