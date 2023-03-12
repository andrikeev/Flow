package flow.data.impl.service

import flow.data.api.service.ForumService
import flow.data.converters.toCategoryPage
import flow.data.converters.toForumTree
import flow.models.Page
import flow.models.forum.ForumItem
import flow.models.forum.ForumTree
import flow.network.api.NetworkApi
import javax.inject.Inject

class ForumServiceImpl @Inject constructor(
    private val networkApi: NetworkApi,
) : ForumService {

    @Volatile
    private var cache: ForumTree? = null

    override suspend fun loadForumTree(): ForumTree {
        if (cache == null) {
            cache = networkApi.getForum().toForumTree()
        }
        return cache!!
    }

    override suspend fun getCategoryPage(id: String, page: Int): Page<ForumItem> {
        return networkApi.getCategory(id, page).toCategoryPage()
    }
}
