package flow.testing.repository

import flow.data.api.ForumRepository
import flow.models.Page
import flow.models.forum.Category
import flow.models.forum.ForumItem
import flow.models.forum.ForumTree
import flow.models.forum.ForumTreeGroup
import flow.models.forum.ForumTreeRootGroup

class TestForumRepository : ForumRepository {
    var forumTree: ForumTree? = null

    override suspend fun loadForumTree() = requireNotNull(forumTree)

    override suspend fun loadCategoryPage(id: String, page: Int): Page<ForumItem> {
        TODO("Not yet implemented")
    }

    companion object {
        val TestForumTree = ForumTree(
            listOf(
                ForumTreeRootGroup(
                    "Root",
                    listOf(
                        ForumTreeGroup(
                            Category("01", "Forum 1"),
                            listOf(
                                Category("011", "Category 11"),
                                Category("012", "Category 12"),
                                Category("013", "Category 13"),
                            ),
                        ),
                        ForumTreeGroup(
                            Category("02", "Forum 2"),
                            listOf(
                                Category("021", "Category 21"),
                                Category("022", "Category 22"),
                                Category("023", "Category 23"),
                            ),
                        )
                    )
                )
            )
        )
    }
}
