package flow.data.impl.service

import flow.data.api.service.ForumService
import flow.data.converters.toCategoryPage
import flow.data.converters.toForum
import flow.models.Page
import flow.models.forum.Forum
import flow.models.forum.ForumItem
import flow.network.api.NetworkApi
import javax.inject.Inject

class ForumServiceImpl @Inject constructor(
    private val networkApi: NetworkApi,
) : ForumService {
    override suspend fun getForum(): Forum {
        return networkApi.getForum().toForum()
    }

    override suspend fun getCategoryPage(id: String, page: Int): Page<ForumItem> {
        return networkApi.getCategory(id, page).toCategoryPage()
    }
}
