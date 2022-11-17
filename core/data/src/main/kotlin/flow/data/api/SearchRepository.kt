package flow.data.api

import flow.models.Page
import flow.models.search.Filter
import flow.models.topic.Torrent

interface SearchRepository {
    suspend fun search(filter: Filter, page: Int): Page<Torrent>
}
