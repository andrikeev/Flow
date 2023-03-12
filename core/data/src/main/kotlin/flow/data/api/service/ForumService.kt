package flow.data.api.service

import flow.models.Page
import flow.models.forum.ForumItem
import flow.models.forum.ForumTree

interface ForumService {
    suspend fun loadForumTree(): ForumTree
    suspend fun getCategoryPage(id: String, page: Int): Page<ForumItem>
}
