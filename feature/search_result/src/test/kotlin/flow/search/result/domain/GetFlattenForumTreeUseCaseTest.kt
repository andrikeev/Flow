package flow.search.result.domain

import flow.domain.usecase.GetForumTreeUseCase
import flow.search.result.domain.models.ForumTreeItem
import flow.search.result.domain.models.SelectState
import flow.testing.TestDispatchers
import flow.testing.service.TestForumService
import flow.testing.service.TestForumService.Companion.TestForumTree
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class GetFlattenForumTreeUseCaseTest {
    private val forumRepository = TestForumService()
    private val getForumTreeUseCase = GetForumTreeUseCase(forumRepository)
    private val dispatchers = TestDispatchers()

    private lateinit var useCase: GetFlattenForumTreeUseCase

    @Before
    fun setUp() {
        useCase = GetFlattenForumTreeUseCase(getForumTreeUseCase, dispatchers)
    }

    @Test
    fun `Should return list of expanded items`() = runTest {
        // set
        forumRepository.forumTree = TestForumTree
        val expandedIds = setOf("c-0", "01")
        val selectedIds = setOf("01", "011", "022")
        //do
        val result = useCase.invoke(expandedIds, selectedIds)
        // check
        assertThat(
            result,
            hasItems(
                ForumTreeItem.Root(id = "c-0", name = "Root", expandable = true, expanded = true),
                ForumTreeItem.Group(
                    id = "01",
                    name = "Forum 1",
                    expandable = true,
                    expanded = true,
                    selectState = SelectState.Selected
                ),
                ForumTreeItem.Category(
                    "011",
                    "Category 11",
                    selectState = SelectState.Selected
                ),
                ForumTreeItem.Category(
                    "012",
                    "Category 12",
                    selectState = SelectState.Unselected
                ),
                ForumTreeItem.Category(
                    "013",
                    "Category 13",
                    selectState = SelectState.Unselected
                ),
                ForumTreeItem.Group(
                    id = "02",
                    name = "Forum 2",
                    expandable = true,
                    expanded = false,
                    selectState = SelectState.PartSelected
                ),
            ),
        )
    }
}
