package flow.data.api.repository

import flow.models.forum.Category
import flow.models.forum.Forum

class TestForumRepository : ForumRepository {
    override suspend fun isNotEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun isForumFresh(maxAgeInDays: Int): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun storeForum(forum: Forum) {
        TODO("Not yet implemented")
    }

    override suspend fun getForum(): Forum {
        TODO("Not yet implemented")
    }

    override suspend fun getCategory(id: String): Category {
        TODO("Not yet implemented")
    }
}
