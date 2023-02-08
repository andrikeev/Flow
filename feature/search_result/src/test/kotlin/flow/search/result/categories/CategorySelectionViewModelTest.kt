package flow.search.result.categories

import flow.domain.usecase.GetForumTreeUseCase
import flow.models.forum.Category
import flow.search.result.domain.GetCategoriesByGroupIdUseCase
import flow.search.result.domain.GetFlattenForumTreeUseCase
import flow.search.result.domain.models.ForumTreeItem.Group
import flow.search.result.domain.models.ForumTreeItem.Root
import flow.search.result.domain.models.SelectState
import flow.testing.TestDispatchers
import flow.testing.logger.TestLoggerFactory
import flow.testing.service.TestForumService
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.orbitmvi.orbit.liveTest
import org.orbitmvi.orbit.test

class CategorySelectionViewModelTest {
    private val forumRepository = TestForumService()
    private val getForumTreeUseCase = GetForumTreeUseCase(forumRepository)
    private val dispatchers = TestDispatchers()
    private val loggerFactory = TestLoggerFactory()

    private lateinit var viewModel: CategorySelectionViewModel

    @Before
    fun setUp() {
        viewModel = CategorySelectionViewModel(
            getFlattenForumTreeUseCase = GetFlattenForumTreeUseCase(
                getForumTreeUseCase = getForumTreeUseCase,
                dispatchers = dispatchers,
            ),
            getCategoriesByGroupIdUseCase = GetCategoriesByGroupIdUseCase(
                getForumTreeUseCase = getForumTreeUseCase,
                dispatchers = dispatchers,
            ),
            loggerFactory = loggerFactory,
        )
    }

    @Test
    fun `Initial state`() = runTest {
        // set
        val containerTest = viewModel.test()
        // check
        containerTest.assert(CategorySelectionState.Loading)
    }

    @Test
    fun `Set selected items`() = runTest {
        // set
        forumRepository.forumTree = TestForumService.TestForumTree
        val containerTest = viewModel.liveTest()
        // do
        containerTest.testIntent { setSelectedCategories(listOf(Category("01", "Forum 1"))) }
        // check
        containerTest.assert(CategorySelectionState.Loading) {
            states(
                { this },
                {
                    CategorySelectionState.Success(
                        listOf(Root(id = "c-0", name = "Root", expandable = true, expanded = false)),
                    )
                },
            )
        }
    }

    @Test
    fun `Expand click`() = runTest {
        // set
        forumRepository.forumTree = TestForumService.TestForumTree
        val containerTest = viewModel.liveTest()
        // do
        containerTest.testIntent { setSelectedCategories(listOf(Category("01", "Forum 1"))) }
        containerTest.testIntent {
            perform(
                CategorySelectionAction.ExpandClick(
                    Root(
                        id = "c-0",
                        name = "Root",
                        expandable = true,
                        expanded = false,
                    )
                )
            )
        }
        // check
        containerTest.assert(CategorySelectionState.Loading) {
            states(
                { this },
                {
                    CategorySelectionState.Success(
                        listOf(Root(id = "c-0", name = "Root", expandable = true, expanded = false)),
                    )
                },
                {
                    CategorySelectionState.Success(
                        listOf(
                            Root(id = "c-0", name = "Root", expandable = true, expanded = true),
                            Group(
                                id = "01",
                                name = "Forum 1",
                                expandable = true,
                                expanded = false,
                                selectState = SelectState.Selected,
                            ),
                            Group(
                                id = "02",
                                name = "Forum 2",
                                expandable = true,
                                expanded = false,
                                selectState = SelectState.Unselected,
                            ),
                        ),
                    )
                },
            )
        }
    }

    @Test
    fun `Remove click`() = runTest {
        // set
        forumRepository.forumTree = TestForumService.TestForumTree
        val containerTest = viewModel.liveTest()
        // do
        containerTest.testIntent { setSelectedCategories(listOf(Category("01", "Forum 1"))) }
        containerTest.testIntent {
            perform(
                CategorySelectionAction.ExpandClick(
                    Root(
                        id = "c-0",
                        name = "Root",
                        expandable = true,
                        expanded = false,
                    )
                )
            )
        }
        containerTest.testIntent {
            perform(
                CategorySelectionAction.SelectClick(
                    Group(
                        id = "01",
                        name = "Forum 1",
                        expandable = true,
                        expanded = false,
                        selectState = SelectState.Selected,
                    )
                )
            )
        }
        // check
        containerTest.assert(CategorySelectionState.Loading) {
            states(
                { this },
                {
                    CategorySelectionState.Success(
                        listOf(Root(id = "c-0", name = "Root", expandable = true, expanded = false)),
                    )
                },
                {
                    CategorySelectionState.Success(
                        listOf(
                            Root(id = "c-0", name = "Root", expandable = true, expanded = true),
                            Group(
                                id = "01",
                                name = "Forum 1",
                                expandable = true,
                                expanded = false,
                                selectState = SelectState.Selected,
                            ),
                            Group(
                                id = "02",
                                name = "Forum 2",
                                expandable = true,
                                expanded = false,
                                selectState = SelectState.Unselected,
                            ),
                        ),
                    )
                },
                {
                    CategorySelectionState.Success(
                        listOf(
                            Root(id = "c-0", name = "Root", expandable = true, expanded = true),
                            Group(
                                id = "01",
                                name = "Forum 1",
                                expandable = true,
                                expanded = false,
                                selectState = SelectState.Unselected,
                            ),
                            Group(
                                id = "02",
                                name = "Forum 2",
                                expandable = true,
                                expanded = false,
                                selectState = SelectState.Unselected,
                            ),
                        ),
                    )
                },
            )
            postedSideEffects(
                CategorySelectionSideEffect.OnRemove(
                    items = listOf(
                        Category(id = "01", name = "Forum 1"),
                        Category(id = "011", name = "Category 11"),
                        Category(id = "012", name = "Category 12"),
                        Category(id = "013", name = "Category 13"),
                    )
                )
            )
        }
    }

    @Test
    fun `Select click`() = runTest {
        // set
        forumRepository.forumTree = TestForumService.TestForumTree
        val containerTest = viewModel.liveTest()
        // do
        containerTest.testIntent { setSelectedCategories(listOf(Category("01", "Forum 1"))) }
        containerTest.testIntent {
            perform(
                CategorySelectionAction.ExpandClick(
                    Root(
                        id = "c-0",
                        name = "Root",
                        expandable = true,
                        expanded = false,
                    )
                )
            )
        }
        containerTest.testIntent {
            perform(
                CategorySelectionAction.SelectClick(
                    Group(
                        id = "02",
                        name = "Forum 1",
                        expandable = true,
                        expanded = false,
                        selectState = SelectState.Selected,
                    )
                )
            )
        }
        // check
        containerTest.assert(CategorySelectionState.Loading) {
            states(
                { this },
                {
                    CategorySelectionState.Success(
                        listOf(Root(id = "c-0", name = "Root", expandable = true, expanded = false)),
                    )
                },
                {
                    CategorySelectionState.Success(
                        listOf(
                            Root(id = "c-0", name = "Root", expandable = true, expanded = true),
                            Group(
                                id = "01",
                                name = "Forum 1",
                                expandable = true,
                                expanded = false,
                                selectState = SelectState.Selected,
                            ),
                            Group(
                                id = "02",
                                name = "Forum 2",
                                expandable = true,
                                expanded = false,
                                selectState = SelectState.Unselected,
                            ),
                        ),
                    )
                },
                {
                    CategorySelectionState.Success(
                        listOf(
                            Root(id = "c-0", name = "Root", expandable = true, expanded = true),
                            Group(
                                id = "01",
                                name = "Forum 1",
                                expandable = true,
                                expanded = false,
                                selectState = SelectState.Selected,
                            ),
                            Group(
                                id = "02",
                                name = "Forum 2",
                                expandable = true,
                                expanded = false,
                                selectState = SelectState.Selected,
                            ),
                        ),
                    )
                },
            )
            postedSideEffects(
                CategorySelectionSideEffect.OnSelect(
                    items = listOf(
                        Category(id = "02", name = "Forum 2"),
                        Category(id = "021", name = "Category 21"),
                        Category(id = "022", name = "Category 22"),
                        Category(id = "023", name = "Category 23"),
                    )
                )
            )
        }
    }
}
