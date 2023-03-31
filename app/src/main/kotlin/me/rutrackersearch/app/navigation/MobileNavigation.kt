package me.rutrackersearch.app.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import flow.designsystem.component.Page
import flow.designsystem.component.PagesScreen
import flow.designsystem.drawables.FlowIcons
import flow.favorites.FavoritesScreen
import flow.forum.ForumScreen
import flow.forum.bookmarks.BookmarksScreen
import flow.forum.category.addCategory
import flow.forum.category.openCategory
import flow.login.addLogin
import flow.login.openLogin
import flow.menu.MenuScreen
import flow.models.topic.Topic
import flow.models.topic.Torrent
import flow.navigation.NavigationController
import flow.navigation.model.NavigationBarItem
import flow.navigation.model.NavigationGraphBuilder
import flow.navigation.model.buildRoute
import flow.navigation.rememberNestedNavigationController
import flow.navigation.ui.MobileNavigation
import flow.navigation.ui.NavigationAnimations
import flow.navigation.ui.NavigationAnimations.Companion.slideInLeft
import flow.navigation.ui.NavigationAnimations.Companion.slideInRight
import flow.navigation.ui.NavigationAnimations.Companion.slideOutLeft
import flow.navigation.ui.NavigationAnimations.Companion.slideOutRight
import flow.navigation.ui.NestedMobileNavigation
import flow.search.addSearchHistory
import flow.search.input.addSearchInput
import flow.search.input.openSearchInput
import flow.search.result.addSearchResult
import flow.search.result.openSearchResult
import flow.topic.open.addOpenTopic
import flow.topic.topic.addTopic
import flow.topic.topic.openTopic
import flow.topic.torrent.addTorrent
import flow.topic.torrent.openTorrent
import flow.visited.VisitedScreen
import me.rutrackersearch.app.R

@Composable
fun MobileNavigation(navigationController: NavigationController) {
    MobileNavigation(navigationController) {
        with(navigationController) {
            addLogin(
                back = ::popBackStack,
                animations = NavigationAnimations.ScaleInOutAnimation,
            )
            addSearchInput(
                back = ::popBackStack,
                openSearchResult = { filter ->
                    popBackStack()
                    openSearchResult(filter)
                },
                animations = NavigationAnimations.Default,
            )
            addSearchResult(
                back = ::popBackStack,
                openSearchInput = { filter -> openSearchInput(filter) },
                openSearchResult = { filter -> openSearchResult(filter) },
                openTorrent = { torrent -> openTorrent(torrent) },
                deepLinkUrls = DeepLinks.searchResultUrls,
                animations = NavigationAnimations.Default,
            )
            addCategory(
                back = ::popBackStack,
                openCategory = { id -> openCategory(id) },
                openLogin = { openLogin() },
                openSearchInput = { categoryId -> openSearchInput(categoryId) },
                openTopic = { topic -> openTopic(topic) },
                openTorrent = { torrent -> openTorrent(torrent) },
                deepLinkUrls = DeepLinks.categoryUrls,
                animations = NavigationAnimations.ScaleInOutAnimation,
            )
            addOpenTopic(
                back = ::popBackStack,
                openTopic = { topic -> openTopic(topic) },
                openTorrent = { torrent -> openTorrent(torrent) },
                deepLinkUrls = DeepLinks.topicUrls,
                animations = NavigationAnimations.ScaleInOutAnimation,
            )
            addTopic(
                back = ::popBackStack,
                openLogin = { openLogin() },
                animations = NavigationAnimations.ScaleInOutAnimation,
            )
            addTorrent(
                back = ::popBackStack,
                openLogin = { openLogin() },
                openComments = { topic -> openTopic(topic) },
                openCategory = { id -> openCategory(id) },
                openSearch = { filter -> openSearchResult(filter) },
                animations = NavigationAnimations.ScaleInOutAnimation,
            )
            addNestedNavigation(
                openSearchInput = { filter -> openSearchInput(filter) },
                openLogin = { openLogin() },
                openTopic = { topic -> openTopic(topic) },
                openTorrent = { torrent -> openTorrent(torrent) },
            )
        }
    }
}

context(NavigationGraphBuilder)
private fun addNestedNavigation(
    openSearchInput: (String) -> Unit,
    openLogin: () -> Unit,
    openTopic: (Topic) -> Unit,
    openTorrent: (Torrent) -> Unit,
) = addDestination(
    isStartRoute = true,
    route = "root",
) {
    val navigationController = rememberNestedNavigationController()
    with(navigationController) {
        val navigationBarItems = remember { BottomRoute.values().map(BottomRoute::navigationBarItem) }
        NestedMobileNavigation(
            navigationController = navigationController,
            navigationBarItems = navigationBarItems,
        ) {
            addSearch(
                openLogin = openLogin,
                openTorrent = openTorrent,
            )
            addForum(
                openSearchInput = openSearchInput,
                openLogin = openLogin,
                openTopic = openTopic,
                openTorrent = openTorrent,
            )
            addTopics(
                openTopic = openTopic,
                openTorrent = openTorrent,
            )
            addMenu(
                openLogin = openLogin,
            )
        }
    }
}

context(NavigationGraphBuilder, NavigationController)
private fun addSearch(
    openLogin: () -> Unit,
    openTorrent: (Torrent) -> Unit,
) = addGraph(
    isStartRoute = true,
    route = BottomRoute.Search.route,
    animations = BottomRoute.Search.animations,
) {
    addSearchHistory(
        openLogin = openLogin,
        openSearchInput = { openSearchInput() },
        openSearchResult = { filter -> openSearchResult(filter) },
        animations = NavigationAnimations.Default,
    )
    addSearchInput(
        back = ::popBackStack,
        openSearchResult = { filter ->
            popBackStack()
            openSearchResult(filter)
        },
        animations = NavigationAnimations.FadeInOutAnimations,
    )
    addSearchResult(
        back = ::popBackStack,
        openSearchInput = { filter -> openSearchInput(filter) },
        openSearchResult = { filter -> openSearchResult(filter) },
        openTorrent = openTorrent,
        animations = NavigationAnimations.Default,
    )
}

context(NavigationGraphBuilder, NavigationController)
private fun addForum(
    openSearchInput: (String) -> Unit,
    openLogin: () -> Unit,
    openTopic: (Topic) -> Unit,
    openTorrent: (Torrent) -> Unit,
) = addGraph(
    route = BottomRoute.Forum.route,
    animations = BottomRoute.Forum.animations,
) {
    addCategory(
        back = ::popBackStack,
        openCategory = { id -> openCategory(id) },
        openLogin = openLogin,
        openSearchInput = openSearchInput,
        openTopic = openTopic,
        openTorrent = openTorrent,
        animations = BottomRoute.Forum.animations,
    )
    addDestination(
        route = buildRoute("forums"),
        isStartRoute = true,
    ) {
        PagesScreen(
            pages = listOf(
                Page(
                    labelResId = R.string.tab_title_forum,
                    icon = FlowIcons.Forum,
                    content = { ForumScreen { id -> openCategory(id) } },
                ),
                Page(
                    labelResId = R.string.tab_title_bookmarks,
                    icon = FlowIcons.Bookmarks,
                    content = { BookmarksScreen { id -> openCategory(id) } },
                ),
            )
        )
    }
}

context(NavigationGraphBuilder)
private fun addTopics(
    openTopic: (Topic) -> Unit,
    openTorrent: (Torrent) -> Unit,
) = addDestination(
    route = BottomRoute.Topics.route, animations = BottomRoute.Topics.animations
) {
    PagesScreen(
        pages = listOf(
            Page(
                labelResId = R.string.tab_title_favorites,
                icon = FlowIcons.Favorite,
                content = {
                    FavoritesScreen(
                        openTopic = openTopic,
                        openTorrent = openTorrent,
                    )
                },
            ),
            Page(
                labelResId = R.string.tab_title_recents,
                icon = FlowIcons.History,
                content = {
                    VisitedScreen(
                        openTopic = openTopic,
                        openTorrent = openTorrent,
                    )
                },
            ),
        )
    )
}

context(NavigationGraphBuilder, NavigationController)
private fun addMenu(
    openLogin: () -> Unit,
) = addDestination(
    route = BottomRoute.Menu.route,
    animations = BottomRoute.Menu.animations,
    content = { MenuScreen(openLogin = openLogin) },
)

private enum class BottomRoute(val navigationBarItem: NavigationBarItem) {
    Search(
        navigationBarItem = NavigationBarItem(
            route = "search",
            labelResId = R.string.label_search,
            icon = FlowIcons.Search,
        ),
    ),
    Forum(
        navigationBarItem = NavigationBarItem(
            route = "forum",
            labelResId = R.string.label_forum,
            icon = FlowIcons.Forum,
        ),
    ),
    Topics(
        navigationBarItem = NavigationBarItem(
            route = "topics",
            labelResId = R.string.label_topics,
            icon = FlowIcons.Topics,
        ),
    ),
    Menu(
        navigationBarItem = NavigationBarItem(
            route = "menu",
            labelResId = R.string.label_menu,
            icon = FlowIcons.Menu,
        ),
    );

    val route = navigationBarItem.route

    val animations: NavigationAnimations = NavigationAnimations(
        enterTransition = {
            val route = BottomRoute.valueOf(from.graph ?: from.route)
            when {
                route == null -> fadeIn()
                route.ordinal > ordinal -> slideInRight()
                route.ordinal <= ordinal -> slideInLeft()
                else -> fadeIn()
            }
        },
        exitTransition = {
            val route = BottomRoute.valueOf(to.graph ?: to.route)
            when {
                route == null -> fadeOut()
                route.ordinal > ordinal -> slideOutRight()
                route.ordinal < ordinal -> slideOutLeft()
                else -> fadeOut()
            }
        },
        popEnterTransition = { fadeIn() },
        popExitTransition = { slideOutLeft() },
    )

    private companion object {
        fun valueOf(value: String?): BottomRoute? {
            return BottomRoute.values().firstOrNull { it.route == value }
        }
    }
}

private object DeepLinks {
    private val baseUrls = listOf(
        "rutracker.org/forum/",
        "rutracker.net/forum/",
    )
    val topicUrls = baseUrls.map { "${it}viewtopic.php" }
    val categoryUrls = baseUrls.map { "${it}viewforum.php" }
    val searchResultUrls = baseUrls.map { "${it}tracker.php" }
}
