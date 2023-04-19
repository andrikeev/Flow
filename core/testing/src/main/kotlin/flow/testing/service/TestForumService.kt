package flow.testing.service

import flow.data.api.service.ForumService
import flow.models.Page
import flow.models.forum.Forum
import flow.models.forum.ForumCategory
import flow.models.forum.ForumItem

class TestForumService : ForumService {
    var forum: Forum? = null

    override suspend fun getForum() = requireNotNull(forum)

    override suspend fun getCategoryPage(id: String, page: Int): Page<ForumItem> {
        TODO("Not yet implemented")
    }

    companion object {
        val TestForum = Forum(
            listOf(
                ForumCategory(
                    "1",
                    "Root",
                    listOf(
                        ForumCategory(
                            "01",
                            "Forum 1",
                            listOf(
                                ForumCategory("011", "Category 11"),
                                ForumCategory("012", "Category 12"),
                                ForumCategory("013", "Category 13"),
                            ),
                        ),
                        ForumCategory(
                            "02",
                            "Forum 2",
                            listOf(
                                ForumCategory("021", "Category 21"),
                                ForumCategory("022", "Category 22"),
                                ForumCategory("023", "Category 23"),
                            ),
                        )
                    )
                )
            )
        )
    }
}
