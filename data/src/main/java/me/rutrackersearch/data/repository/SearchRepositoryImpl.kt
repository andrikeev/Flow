package me.rutrackersearch.data.repository

import me.rutrackersearch.data.converters.parseList
import me.rutrackersearch.data.converters.parseTopic
import me.rutrackersearch.data.converters.readJson
import me.rutrackersearch.data.converters.toFailure
import me.rutrackersearch.data.network.ServerApi
import me.rutrackersearch.domain.entity.Page
import me.rutrackersearch.domain.entity.forum.Category
import me.rutrackersearch.domain.entity.search.Filter
import me.rutrackersearch.domain.entity.topic.Torrent
import me.rutrackersearch.domain.repository.SearchRepository
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val api: ServerApi,
) : SearchRepository {

    override suspend fun search(filter: Filter, page: Int): Page<Torrent> {
        return try {
            val response = api.search(
                query = filter.query.orEmpty(),
                sort = filter.sort,
                order = filter.order,
                period = filter.period,
                author = filter.author?.name.orEmpty(),
                authorId = filter.author?.id.orEmpty(),
                categories = filter.categories?.map(Category::id)?.joinToString(",").orEmpty(),
                page = page,
            )
            val json = response.readJson()
            Page(
                items = json
                    .getJSONArray("torrents")
                    .parseList(JSONObject::parseTopic)
                    .filterIsInstance<Torrent>(),
                page = json.getInt("page"),
                pages = json.getInt("pages"),
            )
        } catch (e: Exception) {
            throw e.toFailure()
        }
    }
}
