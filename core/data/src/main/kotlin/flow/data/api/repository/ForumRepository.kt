package flow.data.api.repository

import flow.models.forum.Category
import flow.models.forum.Forum

interface ForumRepository {
    suspend fun isNotEmpty(): Boolean
    suspend fun isForumFresh(maxAgeInDays: Int = 7): Boolean
    suspend fun storeForum(forum: Forum)
    suspend fun getForum(): Forum
    suspend fun getCategory(id: String): Category
}
