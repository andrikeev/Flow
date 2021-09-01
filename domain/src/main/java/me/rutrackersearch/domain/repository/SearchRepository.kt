package me.rutrackersearch.domain.repository

import me.rutrackersearch.domain.entity.Page
import me.rutrackersearch.domain.entity.search.Filter
import me.rutrackersearch.domain.entity.topic.Torrent

interface SearchRepository {
    suspend fun search(filter: Filter, page: Int): Page<Torrent>
}
