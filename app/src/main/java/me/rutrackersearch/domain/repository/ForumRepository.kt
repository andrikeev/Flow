package me.rutrackersearch.domain.repository

import me.rutrackersearch.models.Page
import me.rutrackersearch.models.forum.ForumItem
import me.rutrackersearch.models.forum.ForumTree

interface ForumRepository {
    suspend fun loadForumTree(): ForumTree
    suspend fun loadCategoryPage(id: String, page: Int): Page<ForumItem>
}
