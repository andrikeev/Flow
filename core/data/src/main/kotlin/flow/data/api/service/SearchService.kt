package flow.data.api.service

import flow.models.Page
import flow.models.search.Filter
import flow.models.topic.Torrent

interface SearchService {
    suspend fun search(filter: Filter, page: Int): Page<Torrent>
}
