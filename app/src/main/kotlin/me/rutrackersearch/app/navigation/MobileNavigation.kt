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
import flow.forum.category.buildCategoryNavigation
import flow.login.buildLoginNavigation
import flow.menu.MenuScreen
import flow.models.search.Filter
import flow.models.topic.Topic
import flow.models.topic.Torrent
import flow.navigation.NavigationController
import flow.navigation.model.NavigationBarItem
import flow.navigation.model.NavigationGraphBuilder
import flow.navigation.rememberNavigationController
import flow.navigation.ui.MobileNavigation
import flow.navigation.ui.NavigationAnimations
import flow.navigation.ui.NavigationAnimations.Companion.slideInLeft
import flow.navigation.ui.NavigationAnimations.Companion.slideInRight
import flow.navigation.ui.NavigationAnimations.Companion.slideOutLeft
import flow.navigation.ui.NavigationAnimations.Companion.slideOutRight
import flow.navigation.ui.NestedMobileNavigation
import flow.search.addSearchHistory
import flow.search.input.buildSearchInputNavigation
import flow.search.result.buildSearchResultNavigation
import flow.topic.topic.buildTopicNavigation
import flow.topic.torrent.buildTorrentNavigation
import flow.visited.VisitedScreen
import me.rutrackersearch.app.R

@Composable
fun MobileNavigation(navigationController: NavigationController) {
    MobileNavigation(navigationController) {
        with(navigationController) {
            val (addLogin, openLogin) = buildLoginNavigation()
            val (addCategory, openCategory) = buildCategoryNavigation()
            val (addTopic, openTopic) = buildTopicNavigation()
            val (addTorrent, openTorrent) = buildTorrentNavigation()
            val (addSearchInput, openSearchInput) = buildSearchInputNavigation()
            val (addSearchResult, openSearchResult) = buildSearchResultNavigation()
            addLogin(
                ::popBackStack,
                NavigationAnimations.ScaleInOutAnimation,
            )
            addSearchInput(
                ::popBackStack,
                { filter ->
                    popBackStack()
                    openSearchResult(filter)
                },
                NavigationAnimations.Default,
            )
            addSearchResult(
                ::popBackStack,
                { filter -> openSearchInput(filter) },
                { filter -> openSearchResult(filter) },
                { torrent -> openTorrent(torrent) },
                NavigationAnimations.Default,
            )
            addCategory(
                ::popBackStack,
                { category -> openCategory(category) },
                { filter -> openSearchInput(filter) },
                { topic -> openTopic(topic) },
                { torrent -> openTorrent(torrent) },
                NavigationAnimations.ScaleInOutAnimation,
            )
            addTopic(
                ::popBackStack,
                { openLogin() },
                NavigationAnimations.ScaleInOutAnimation,
            )
            addTorrent(
                ::popBackStack,
                { openLogin() },
                { topic -> openTopic(topic) },
                { category -> openCategory(category) },
                { filter -> openSearchResult(filter) },
                NavigationAnimations.ScaleInOutAnimation,
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

private fun NavigationGraphBuilder.addNestedNavigation(
    openSearchInput: (Filter) -> Unit,
    openLogin: () -> Unit,
    openTopic: (Topic) -> Unit,
    openTorrent: (Torrent) -> Unit,
) = addDestination(
    isStartRoute = true,
    route = "Root",
) {
    val navigationController = rememberNavigationController()
    val navigationBarItems = remember { BottomRoute.values().map(BottomRoute::navigationBarItem) }
    NestedMobileNavigation(
        navigationController = navigationController,
        navigationBarItems = navigationBarItems,
    ) {
        addSearch(navigationController, openLogin, openTorrent)
        addForum(navigationController, openSearchInput, openTopic, openTorrent)
        addTopics(openTopic, openTorrent)
        addMenu(openLogin)
    }
}

private fun NavigationGraphBuilder.addSearch(
    navigationController: NavigationController,
    openLogin: () -> Unit,
    openTorrent: (Torrent) -> Unit,
) {
    with(navigationController) {
        addGraph(
            isStartRoute = true,
            route = BottomRoute.Search.route,
            animations = BottomRoute.Search.animations,
        ) {
            val (addSearchResult, openSearchResult) = buildSearchResultNavigation()
            val (addSearchInput, openSearchInput) = buildSearchInputNavigation()

            addSearchHistory(
                openLogin,
                { openSearchInput(Filter()) },
                { filter -> openSearchResult(filter) },
                NavigationAnimations.Default,
            )
            addSearchInput(
                ::popBackStack,
                { filter ->
                    popBackStack()
                    openSearchResult(filter)
                },
                NavigationAnimations.Default,
            )
            addSearchResult(
                navigationController::popBackStack,
                { filter -> openSearchInput(filter) },
                { filter -> openSearchResult(filter) },
                openTorrent,
                NavigationAnimations.Default,
            )
        }
    }
}

private fun NavigationGraphBuilder.addForum(
    navigationController: NavigationController,
    openSearchInput: (Filter) -> Unit,
    openTopic: (Topic) -> Unit,
    openTorrent: (Torrent) -> Unit,
) {
    with(navigationController) {
        addGraph(
            route = BottomRoute.Forum.route,
            animations = BottomRoute.Forum.animations,
        ) {
            val (addCategory, openCategory) = buildCategoryNavigation()
            addCategory(
                navigationController::popBackStack,
                { category -> openCategory(category) },
                openSearchInput,
                openTopic,
                openTorrent,
                NavigationAnimations.Default,
            )
            addDestination(
                route = route("Forums"),
                isStartRoute = true,
            ) {
                PagesScreen(
                    pages = listOf(
                        Page(
                            labelResId = R.string.tab_title_forum,
                            icon = FlowIcons.Forum,
                            content = { ForumScreen { category -> openCategory(category) } },
                        ),
                        Page(
                            labelResId = R.string.tab_title_bookmarks,
                            icon = FlowIcons.Bookmarks,
                            content = { BookmarksScreen { category -> openCategory(category) } },
                        ),
                    )
                )
            }
        }
    }
}

private fun NavigationGraphBuilder.addTopics(
    openTopic: (Topic) -> Unit,
    openTorrent: (Torrent) -> Unit,
) {
    addDestination(
        route = BottomRoute.Topics.route,
        animations = BottomRoute.Topics.animations
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
}

private fun NavigationGraphBuilder.addMenu(openLogin: () -> Unit) {
    addDestination(
        route = BottomRoute.Menu.route,
        animations = BottomRoute.Menu.animations,
        content = { MenuScreen(openLogin = openLogin) },
    )
}

private enum class BottomRoute(val navigationBarItem: NavigationBarItem) {
    Search(
        navigationBarItem = NavigationBarItem(
            route = "Search",
            labelResId = flow.ui.R.string.search_label,
            icon = FlowIcons.Search,
        ),
    ),
    Forum(
        navigationBarItem = NavigationBarItem(
            route = "Forum",
            labelResId = flow.ui.R.string.forum_label,
            icon = FlowIcons.Forum,
        ),
    ),
    Topics(
        navigationBarItem = NavigationBarItem(
            route = "Topics",
            labelResId = flow.ui.R.string.topics_label,
            icon = FlowIcons.Topics,
        ),
    ),
    Menu(
        navigationBarItem = NavigationBarItem(
            route = "Menu",
            labelResId = flow.ui.R.string.menu_label,
            icon = FlowIcons.Menu,
        ),
    );

    val route = navigationBarItem.route

    val animations: NavigationAnimations = NavigationAnimations(
        enterTransition = { from ->
            val route = BottomRoute.valueOf(from)
            when {
                route == null -> fadeIn()
                route.ordinal > this.ordinal -> slideInRight()
                route.ordinal < this.ordinal -> slideInLeft()
                else -> fadeIn()
            }
        },
        exitTransition = { to ->
            val route = BottomRoute.valueOf(to)
            when {
                route == null -> fadeOut()
                route.ordinal > this.ordinal -> slideOutRight()
                route.ordinal < this.ordinal -> slideOutLeft()
                else -> fadeOut()
            }
        },
    )

    private companion object {
        fun valueOf(value: String?): BottomRoute? {
            return value?.runCatching { BottomRoute.valueOf(this) }?.getOrNull()
        }
    }
}
