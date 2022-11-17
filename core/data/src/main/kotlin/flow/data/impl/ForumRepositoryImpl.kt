package flow.data.impl

import flow.data.api.ForumRepository
import flow.models.Page
import flow.models.forum.ForumItem
import flow.models.forum.ForumTree
import flow.network.NetworkApi
import javax.inject.Inject

class ForumRepositoryImpl @Inject constructor(
    private val networkApi: NetworkApi,
) : ForumRepository {

    @Volatile
    private var cache: ForumTree? = null

    override suspend fun loadForumTree(): ForumTree {
        return cache ?: networkApi.forumTree().also { cache = it }
    }

    override suspend fun loadCategoryPage(id: String, page: Int): Page<ForumItem> {
        return networkApi.category(id, page)
    }
}
