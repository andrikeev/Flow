package flow.menu

import flow.domain.usecase.ClearBookmarksUseCase
import flow.domain.usecase.ClearHistoryUseCase
import flow.domain.usecase.ClearLocalFavoritesUseCase
import flow.domain.usecase.ObserveSettingsUseCase
import flow.domain.usecase.SetBookmarksSyncPeriodUseCase
import flow.domain.usecase.SetEndpointUseCase
import flow.domain.usecase.SetFavoritesSyncPeriodUseCase
import flow.domain.usecase.SetThemeUseCase
import flow.models.settings.Endpoint
import flow.models.settings.SyncPeriod
import flow.models.settings.Theme
import flow.testing.TestDispatchers
import flow.testing.logger.TestLoggerFactory
import flow.testing.repository.TestBookmarksRepository
import flow.testing.repository.TestFavoritesRepository
import flow.testing.repository.TestSearchHistoryRepository
import flow.testing.repository.TestSettingsRepository
import flow.testing.repository.TestSuggestsRepository
import flow.testing.repository.TestVisitedRepository
import flow.testing.rule.MainDispatcherRule
import flow.testing.service.TestBackgroundService
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.orbitmvi.orbit.liveTest
import org.orbitmvi.orbit.test

class MenuViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val bookmarksRepository = TestBookmarksRepository()
    private val favoritesRepository = TestFavoritesRepository()
    private val suggestsRepository = TestSuggestsRepository()
    private val searchHistoryRepository = TestSearchHistoryRepository()
    private val topicHistoryRepository = TestVisitedRepository()
    private val settingsRepository = TestSettingsRepository()
    private val backgroundService = TestBackgroundService()
    private val dispatchers = TestDispatchers()
    private val loggerFactory = TestLoggerFactory()

    private lateinit var viewModel: MenuViewModel

    @Before
    fun setUp() {
        viewModel = MenuViewModel(
            clearBookmarksUseCase = ClearBookmarksUseCase(
                bookmarksRepository = bookmarksRepository,
                dispatchers = dispatchers
            ),
            clearLocalFavoritesUseCase = ClearLocalFavoritesUseCase(
                favoritesRepository = favoritesRepository,
                dispatchers = dispatchers,
            ),
            clearHistoryUseCase = ClearHistoryUseCase(
                suggestsRepository = suggestsRepository,
                searchHistoryRepository = searchHistoryRepository,
                visitedRepository = topicHistoryRepository,
                dispatchers = dispatchers,
            ),
            observeSettingsUseCase = ObserveSettingsUseCase(
                settingsRepository = settingsRepository,
            ),
            setBookmarksSyncPeriodUseCase = SetBookmarksSyncPeriodUseCase(
                settingsRepository = settingsRepository,
                backgroundService = backgroundService,
                dispatchers = dispatchers,
            ),
            setFavoritesSyncPeriodUseCase = SetFavoritesSyncPeriodUseCase(
                settingsRepository = settingsRepository,
                backgroundService = backgroundService,
                dispatchers = dispatchers,
            ),
            setThemeUseCase = SetThemeUseCase(
                settingsRepository = settingsRepository,
            ),
            setEndpointUseCase = SetEndpointUseCase(
                settingsRepository = settingsRepository,
            ),
            loggerFactory = loggerFactory,
        )
    }

    @Test
    fun `Initial state`() = runTest {
        // set
        val containerTest = viewModel.test()
        // check
        containerTest.assert(MenuState())
    }

    @Test
    fun `Set theme`() = runTest {
        // set
        val containerTest = viewModel.liveTest()
        containerTest.runOnCreate()
        // do
        containerTest.testIntent { perform(MenuAction.SetTheme(Theme.DARK)) }
        // check
        containerTest.assert(MenuState()) {
            states(
                { this },
                { copy(theme = Theme.DARK) },
            )
        }
    }

    @Test
    fun `Set endpoint`() = runTest {
        // set
        val containerTest = viewModel.liveTest()
        containerTest.runOnCreate()
        // do
        containerTest.testIntent { perform(MenuAction.SetEndpoint(Endpoint.RutrackerOrg)) }
        // check
        containerTest.assert(MenuState()) {
            states(
                { this },
                { copy(endpoint = Endpoint.RutrackerOrg) },
            )
        }
    }

    @Test
    fun `Set favorites sync period`() = runTest {
        // set
        val containerTest = viewModel.liveTest()
        containerTest.runOnCreate()
        // do
        containerTest.testIntent { perform(MenuAction.SetFavoritesSyncPeriod(SyncPeriod.HOUR)) }
        // check
        containerTest.assert(MenuState()) {
            states(
                { this },
                { copy(favoritesSyncPeriod = SyncPeriod.HOUR) },
            )
        }
    }

    @Test
    fun `Set bookmarks sync period`() = runTest {
        // set
        val containerTest = viewModel.liveTest()
        containerTest.runOnCreate()
        // do
        containerTest.testIntent { perform(MenuAction.SetFavoritesSyncPeriod(SyncPeriod.HOUR)) }
        // check
        containerTest.assert(MenuState()) {
            states(
                { this },
                { copy(favoritesSyncPeriod = SyncPeriod.HOUR) },
            )
        }
    }

    @Test
    fun `Show confirmation`() = runTest {
        // set
        val containerTest = viewModel.liveTest()
        containerTest.runOnCreate()
        // do
        containerTest.testIntent {
            perform(
                MenuAction.ConfirmableAction(
                    TestTitleId,
                    TestMessageId,
                    TestAction
                )
            )
        }
        // check
        containerTest.assert(MenuState()) {
            states(
                { this },
            )
            postedSideEffects(
                MenuSideEffect.ShowConfirmation(
                    TestTitleId,
                    TestMessageId,
                    TestAction
                )
            )
        }
    }

    @Test
    fun `Send feedback`() = runTest {
        // set
        val containerTest = viewModel.liveTest()
        containerTest.runOnCreate()
        // do
        containerTest.testIntent { perform(MenuAction.SendFeedbackClick) }
        // check
        containerTest.assert(MenuState()) {
            states(
                { this },
            )
            postedSideEffects(MenuSideEffect.OpenLink("mailto:rutracker.search@gmail.com"))
        }
    }

    @Test
    fun `Show about`() = runTest {
        // set
        val containerTest = viewModel.liveTest()
        containerTest.runOnCreate()
        // do
        containerTest.testIntent { perform(MenuAction.AboutClick) }
        // check
        containerTest.assert(MenuState()) {
            states(
                { this },
            )
            postedSideEffects(MenuSideEffect.ShowAbout)
        }
    }

    companion object {
        const val TestTitleId = 0
        const val TestMessageId = 0
        val TestAction = {}
    }
}
