package me.rutrackersearch.domain.repository

import me.rutrackersearch.domain.entity.Page
import me.rutrackersearch.domain.entity.forum.ForumItem
import me.rutrackersearch.domain.entity.forum.ForumTree

interface ForumRepository {
    suspend fun loadForumTree(): ForumTree
    suspend fun loadCategoryPage(id: String, page: Int): Page<ForumItem>
}
