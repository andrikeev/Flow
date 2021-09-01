package me.rutrackersearch.data.repository

import me.rutrackersearch.data.converters.parseCategoryPage
import me.rutrackersearch.data.converters.parseForumTree
import me.rutrackersearch.data.converters.readJson
import me.rutrackersearch.data.converters.toFailure
import me.rutrackersearch.data.network.ServerApi
import me.rutrackersearch.domain.entity.Page
import me.rutrackersearch.domain.entity.forum.ForumItem
import me.rutrackersearch.domain.entity.forum.ForumTree
import me.rutrackersearch.domain.repository.ForumRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ForumRepositoryImpl @Inject constructor(
    private val api: ServerApi,
) : ForumRepository {

    override suspend fun loadForumTree(): ForumTree {
        return try {
            api.forumTree()
                .readJson()
                .parseForumTree()
        } catch (e: Exception) {
            throw e.toFailure()
        }
    }

    override suspend fun loadCategoryPage(id: String, page: Int): Page<ForumItem> {
        return try {
            api.category(id, page)
                .readJson()
                .parseCategoryPage()
        } catch (e: Exception) {
            throw e.toFailure()
        }
    }
}
