package me.rutrackersearch.domain.repository

import me.rutrackersearch.models.Page
import me.rutrackersearch.models.search.Filter
import me.rutrackersearch.models.topic.Torrent

interface SearchRepository {
    suspend fun search(filter: Filter, page: Int): Page<Torrent>
}
