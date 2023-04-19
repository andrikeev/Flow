package flow.data.api.service

import flow.models.Page
import flow.models.forum.Forum
import flow.models.forum.ForumItem

interface ForumService {
    suspend fun getForum(): Forum
    suspend fun getCategoryPage(id: String, page: Int): Page<ForumItem>
}
