package flow.data.api

import flow.models.Page
import flow.models.forum.ForumItem
import flow.models.forum.ForumTree

interface ForumRepository {
    suspend fun loadForumTree(): ForumTree
    suspend fun loadCategoryPage(id: String, page: Int): Page<ForumItem>
}
