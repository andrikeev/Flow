package flow.search.result.domain

import flow.domain.usecase.GetForumTreeUseCase
import flow.models.forum.Category
import flow.testing.TestDispatchers
import flow.testing.service.TestForumService
import flow.testing.service.TestForumService.Companion.TestForumTree
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetCategoriesByGroupIdUseCaseTest {
    private val forumRepository = TestForumService()
    private val getForumTreeUseCase = GetForumTreeUseCase(forumRepository)
    private val dispatchers = TestDispatchers()

    private lateinit var useCase: GetCategoriesByGroupIdUseCase

    @Before
    fun setUp() {
        useCase = GetCategoriesByGroupIdUseCase(getForumTreeUseCase, dispatchers)
    }

    @Test
    fun `Should return list of categories`() = runTest {
        // set
        forumRepository.forumTree = TestForumTree
        // do
        val result = useCase.invoke("01")
        // check
        assertThat(
            result,
            hasItems(
                Category("01", "Forum 1"),
                Category("011", "Category 11"),
                Category("012", "Category 12"),
                Category("013", "Category 13"),
            ),
        )
    }

    @Test
    fun `Should return empty list`() = runTest {
        // set
        forumRepository.forumTree = TestForumTree
        // do
        val result = useCase.invoke("123")
        // check
        assertTrue(result.isEmpty())
    }
}
