package me.rutrackersearch.data.repository

import me.rutrackersearch.domain.repository.ForumRepository
import me.rutrackersearch.models.Page
import me.rutrackersearch.models.forum.ForumItem
import me.rutrackersearch.models.forum.ForumTree
import me.rutrackersearch.network.NetworkApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ForumRepositoryImpl @Inject constructor(
    private val api: NetworkApi,
) : ForumRepository {

    private var cache: ForumTree? = null

    override suspend fun loadForumTree(): ForumTree {
        return cache ?: api.forumTree().also { cache = it }
    }

    override suspend fun loadCategoryPage(id: String, page: Int): Page<ForumItem> {
        return api.category(id, page)
    }
}
