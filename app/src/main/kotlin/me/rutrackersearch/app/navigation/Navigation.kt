package me.rutrackersearch.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import flow.designsystem.component.Page
import flow.designsystem.component.PagesScreen
import flow.designsystem.component.Scaffold
import flow.designsystem.drawables.FlowIcons
import flow.favorites.FavoritesScreen
import flow.forum.ForumScreen
import flow.forum.bookmarks.BookmarksScreen
import flow.forum.category.CategoryScreen
import flow.login.LoginScreen
import flow.menu.MenuScreen
import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.topic.Topic
import flow.models.topic.Torrent
import flow.navigation.BottomNavigation
import flow.navigation.NavigationBarItem
import flow.navigation.NavigationController
import flow.navigation.NavigationDestination
import flow.navigation.NavigationGraph
import flow.navigation.NavigationHost
import flow.search.SearchScreen
import flow.search.input.SearchInputScreen
import flow.search.result.SearchResultScreen
import flow.topic.open.OpenTopicScreen
import flow.topic.topic.TopicScreen
import flow.topic.torrent.TorrentScreen
import flow.ui.args.wrap
import flow.ui.args.wrapId
import flow.ui.args.wrapPid
import flow.ui.component.LocalSnackbarHostState
import flow.visited.VisitedScreen
import me.rutrackersearch.app.R

@Composable
fun MobileNavigation(navigationController: NavigationController) {
    val snackbarState = remember { SnackbarHostState() }
    CompositionLocalProvider(LocalSnackbarHostState provides snackbarState) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarState) },
            content = { padding ->
                NavigationHost(
                    modifier = Modifier.padding(padding),
                    navigationController = navigationController,
                    navigationGraph = Navigation.navigationGraph,
                )
            },
            bottomBar = {
                BottomNavigation(
                    navigationController = navigationController,
                    items = Navigation.bottomBarItems,
                )
            },
        )
    }
}

private object Navigation {
    val Login = NavigationDestination.Destination(
        route = Routes.Login,
        content = { navigationController ->
            LoginScreen(back = navigationController::popBackStack)
        },
    )

    val Search = NavigationDestination.Graph(
        route = Routes.Search,
        startRoute = Routes.SearchHistory,
        destinations = listOf(
            NavigationDestination.Destination(
                route = Routes.SearchHistory,
                content = { navigationController ->
                    SearchScreen(
                        openLogin = navigationController::openLogin,
                        openSearchInput = navigationController::showInput,
                        openSearch = navigationController::showSearchResult,
                    )
                },
            ),
            NavigationDestination.Destination(
                route = Routes.SearchResult,
                content = { navigationController ->
                    SearchResultScreen(
                        back = navigationController::popBackStack,
                        openSearchInput = navigationController::showInput,
                        openTorrent = navigationController::openTorrent,
                        openSearchResult = navigationController::showSearchResult,
                    )
                },
            )
        ),
    )

    val SearchInput = NavigationDestination.Destination(
        route = Routes.SearchInput,
        content = { navigationController ->
            SearchInputScreen(
                back = navigationController::popBackStack,
                openSearch = { filter ->
                    navigationController.popBackStack()
                    navigationController.showSearchResult(filter)
                },
            )
        },
    )

    val Forum = NavigationDestination.Graph(
        route = Routes.Forum,
        startRoute = Routes.Forums,
        destinations = listOf(
            NavigationDestination.Destination(
                route = Routes.Forums,
                content = { navigationController ->
                    PagesScreen(
                        pages = listOf(
                            Page(
                                labelResId = R.string.tab_title_forum,
                                icon = FlowIcons.Forum,
                                content = {
                                    ForumScreen(openCategory = navigationController::openCategory)
                                },
                            ),
                            Page(
                                labelResId = R.string.tab_title_bookmarks,
                                icon = FlowIcons.Bookmarks,
                                content = {
                                    BookmarksScreen(openCategory = navigationController::openCategory)
                                },
                            ),
                        )
                    )
                }
            ),
            NavigationDestination.Destination(
                route = Routes.Category,
                content = { navigationController ->
                    CategoryScreen(
                        back = navigationController::popBackStack,
                        openCategory = navigationController::openCategory,
                        openSearchInput = navigationController::showInput,
                        openTopic = navigationController::openTopic,
                        openTorrent = navigationController::openTorrent,
                    )
                }
            ),
        ),
    )

    val Topics = NavigationDestination.Destination(
        route = Routes.Topics,
        content = { navigationController ->
            PagesScreen(
                pages = listOf(
                    Page(
                        labelResId = R.string.tab_title_favorites,
                        icon = FlowIcons.Favorite,
                        content = {
                            FavoritesScreen(
                                openTopic = navigationController::openTopic,
                                openTorrent = navigationController::openTorrent,
                            )
                        },
                    ),
                    Page(
                        labelResId = R.string.tab_title_recents,
                        icon = FlowIcons.History,
                        content = {
                            VisitedScreen(
                                openTopic = navigationController::openTopic,
                                openTorrent = navigationController::openTorrent,
                            )
                        },
                    ),
                )
            )
        }
    )

    val OpenTopic = NavigationDestination.Destination(
        route = Routes.OpenTopic,
        content = { navigationController ->
            OpenTopicScreen(
                back = navigationController::popBackStack,
                openTopic = {
                    navigationController.popBackStack()
                    navigationController.openTopic(it)
                },
                openTorrent = {
                    navigationController.popBackStack()
                    navigationController.openTorrent(it)
                },
            )
        },
    )

    val Topic = NavigationDestination.Destination(
        route = Routes.Topic,
        content = { navigationController ->
            TopicScreen(
                back = navigationController::popBackStack,
                openLogin = navigationController::openLogin,
            )
        },
    )

    val Torrent = NavigationDestination.Destination(
        route = Routes.Torrent,
        content = { navigationController ->
            TorrentScreen(
                back = navigationController::popBackStack,
                openLogin = navigationController::openLogin,
                openComments = navigationController::openTopic,
                openCategory = navigationController::openCategory,
                openSearch = navigationController::showSearchResult,
            )
        },
    )

    val Menu = NavigationDestination.Destination(
        route = Routes.Menu,
        content = { navigationController ->
            MenuScreen(openLogin = navigationController::openLogin)
        },
    )

    val navigationGraph = NavigationGraph(
        startRoute = Routes.Search,
        destinations = listOf(Login, Search, SearchInput, Forum, Topics, OpenTopic, Topic, Torrent, Menu),
    )

    val bottomBarItems = listOf(
        NavigationBarItem(
            route = Search.route,
            labelResId = flow.ui.R.string.search_label,
            icon = FlowIcons.Search,
        ),
        NavigationBarItem(
            route = Forum.route,
            labelResId = flow.ui.R.string.forum_label,
            icon = FlowIcons.Forum,
        ),
        NavigationBarItem(
            route = Topics.route,
            labelResId = flow.ui.R.string.topics_label,
            icon = FlowIcons.Topics,
        ),
        NavigationBarItem(
            route = Menu.route,
            labelResId = flow.ui.R.string.menu_label,
            icon = FlowIcons.Menu,
        ),
    )
}

private object Routes {
    const val Login = "Login"
    const val Search = "Search"
    const val SearchHistory = "SearchHistory"
    const val SearchResult = "SearchResult"
    const val SearchInput = "SearchInput"
    const val Forum = "Forum"
    const val Forums = "Forums"
    const val Category = "Category"
    const val Topics = "Topics"
    const val OpenTopic = "OpenTopic"
    const val Topic = "Topic"
    const val Torrent = "Torrent"
    const val Menu = "Menu"
}

fun NavigationController.showSearchResult(filter: Filter) = navigate(Routes.SearchResult, filter.wrap())

fun NavigationController.openCategory(category: Category) = navigate(Routes.Category, category.wrap())

fun NavigationController.openTopic(id: String?, pid: String?) = navigate(Routes.OpenTopic, id.wrapId(), pid.wrapPid())

private fun NavigationController.openLogin() = navigate(Routes.Login)

private fun NavigationController.showInput(filter: Filter = Filter()) = navigate(Routes.SearchInput, filter.wrap())

private fun NavigationController.openTopic(topic: Topic) = navigate(Routes.Topic, topic.wrap())

private fun NavigationController.openTorrent(torrent: Torrent) = navigate(Routes.Torrent, torrent.wrap())
