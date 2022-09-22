package me.rutrackersearch.app.ui.navigation

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import kotlinx.coroutines.job
import me.rutrackersearch.app.R
import me.rutrackersearch.app.ui.args.wrap
import me.rutrackersearch.app.ui.args.wrapId
import me.rutrackersearch.app.ui.args.wrapPid
import me.rutrackersearch.app.ui.auth.LoginScreen
import me.rutrackersearch.app.ui.common.ContentElevation
import me.rutrackersearch.app.ui.common.ContentScale
import me.rutrackersearch.app.ui.common.DynamicBox
import me.rutrackersearch.app.ui.common.Focusable
import me.rutrackersearch.app.ui.common.NavigationBar
import me.rutrackersearch.app.ui.common.Page
import me.rutrackersearch.app.ui.common.PagesScreen
import me.rutrackersearch.app.ui.common.focusableSpec
import me.rutrackersearch.app.ui.common.rememberFocusRequester
import me.rutrackersearch.app.ui.forum.bookmarks.BookmarksScreen
import me.rutrackersearch.app.ui.forum.category.CategoryScreen
import me.rutrackersearch.app.ui.forum.root.ForumScreen
import me.rutrackersearch.app.ui.menu.MenuScreen
import me.rutrackersearch.app.ui.search.input.SearchInputScreen
import me.rutrackersearch.app.ui.search.result.SearchResultScreen
import me.rutrackersearch.app.ui.search.root.SearchScreen
import me.rutrackersearch.app.ui.topic.open.OpenTopicScreen
import me.rutrackersearch.app.ui.topic.topic.TopicScreen
import me.rutrackersearch.app.ui.topic.torrent.TorrentScreen
import me.rutrackersearch.app.ui.topics.favorites.FavoritesScreen
import me.rutrackersearch.app.ui.topics.history.HistoryScreen
import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.search.Filter
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.Torrent

private typealias Destination = @Composable (NavHostController, NavBackStackEntry) -> Unit

@Composable
fun Navigation(navController: NavHostController) {
    DynamicBox(
        mobileContent = { MobileNavigation(navController = navController) },
        tvContent = { TVNavigation(navController = navController) },
    )
}

@Composable
private fun MobileNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    MobileNavigation(
        modifier = modifier,
        navController = navController,
        navigationTabs = listOf(
            NavigationTab(
                route = NavigationGraph.Search.route,
                labelResId = R.string.search_label,
                icon = Icons.Outlined.Search,
            ),
            NavigationTab(
                route = NavigationGraph.Forums.route,
                labelResId = R.string.forum_label,
                icon = Icons.Outlined.ListAlt,
            ),
            NavigationTab(
                route = NavigationGraph.Topics.route,
                labelResId = R.string.topics_label,
                icon = Icons.Outlined.Forum,
            ),
            NavigationTab(
                route = NavigationGraph.Menu.route,
                labelResId = R.string.menu_label,
                icon = Icons.Outlined.Menu,
            ),
        ),
        navigationGraphs = listOf(
            NavigationGraph.Auth,
            NavigationGraph.Search,
            NavigationGraph.SearchInput,
            NavigationGraph.Forums,
            NavigationGraph.Topics,
            NavigationGraph.Menu,
            NavigationGraph.OpenTopic,
            NavigationGraph.Topic,
            NavigationGraph.Torrent,
        ),
    )
}

@Composable
private fun MobileNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    navigationTabs: List<NavigationTab>,
    navigationGraphs: List<NavigationGraph>,
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            val navigationBarRoutes = remember { navigationTabs.map(NavigationTab::route) }
            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentGraphRoute by derivedStateOf { currentBackStackEntry?.destination?.parent?.route }
            val showNavigationBar by derivedStateOf { navigationBarRoutes.contains(currentGraphRoute) }
            BottomNavigation(
                tabs = navigationTabs,
                visible = showNavigationBar,
                isSelected = { route ->
                    currentGraphRoute == route
                },
                onClick = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        },
    ) { padding ->
        NavHost(
            modifier = Modifier.padding(padding),
            navController = navController,
            startDestination = NavigationGraph.Search.route,
        ) {
            navigationGraphs.forEach { graph ->
                navigation(
                    route = graph.route,
                    startDestination = graph.startDestination.route,
                ) {
                    graph.destinations.forEach { destination ->
                        composable(route = destination.route) { entry ->
                            destination.content(navController, entry)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavigation(
    visible: Boolean,
    tabs: List<NavigationTab>,
    isSelected: (route: String) -> Boolean,
    onClick: (route: String) -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        NavigationBar {
            tabs.forEach { tab ->
                NavigationBarItem(icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = stringResource(tab.labelResId),
                    )
                },
                    label = { Text(stringResource(tab.labelResId)) },
                    selected = isSelected(tab.route),
                    onClick = { onClick(tab.route) })
            }
        }
    }
}

@Composable
private fun TVNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    TVNavigation(
        modifier = modifier,
        navController = navController,
        navigationTabs = listOf(
            NavigationTab(
                route = NavigationGraph.Search.route,
                labelResId = R.string.search_label,
                icon = Icons.Outlined.Search,
            ),
            NavigationTab(
                route = NavigationGraph.Forum.route,
                labelResId = R.string.forum_label,
                icon = Icons.Outlined.ListAlt,
            ),
            NavigationTab(
                route = NavigationGraph.Bookmarks.route,
                labelResId = R.string.bookmarks_label,
                icon = Icons.Outlined.Bookmarks,
            ),
            NavigationTab(
                route = NavigationGraph.History.route,
                labelResId = R.string.history_label,
                icon = Icons.Outlined.History,
            ),
            NavigationTab(
                route = NavigationGraph.Favorites.route,
                labelResId = R.string.favorites_label,
                icon = Icons.Outlined.Favorite,
            ),
            NavigationTab(
                route = NavigationGraph.Menu.route,
                labelResId = R.string.settings_label,
                icon = Icons.Outlined.Settings,
            )
        ),
        navigationGraphs = listOf(
            NavigationGraph.Auth,
            NavigationGraph.Search,
            NavigationGraph.SearchInput,
            NavigationGraph.Forum,
            NavigationGraph.Bookmarks,
            NavigationGraph.Favorites,
            NavigationGraph.History,
            NavigationGraph.Menu,
            NavigationGraph.OpenTopic,
            NavigationGraph.Topic,
            NavigationGraph.Torrent
        ),
    )
}

@Composable
private fun TVNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    navigationTabs: List<NavigationTab>,
    navigationGraphs: List<NavigationGraph>,
) {
    Scaffold(modifier = modifier) { padding ->
        val navigationBarRoutes = remember { navigationTabs.map(NavigationTab::route) }
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentGraphRoute by derivedStateOf { currentBackStackEntry?.destination?.parent?.route }
        val showNavigationBar by derivedStateOf { navigationBarRoutes.contains(currentGraphRoute) }
        Row(
            modifier = Modifier.padding(padding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SideNavigation(
                tabs = navigationTabs,
                visible = showNavigationBar,
                isSelected = { route ->
                    currentGraphRoute == route
                },
                onClick = { route ->
                    navController.backQueue.filter { it.destination.route?.contains(route) == true }
                        .forEach(navController.backQueue::remove)
                    navController.navigate(route)
                },
            )
            NavHost(
                modifier = Modifier.padding(padding),
                navController = navController,
                startDestination = NavigationGraph.Search.route,
            ) {
                navigationGraphs.forEach { graph ->
                    navigation(
                        route = graph.route,
                        startDestination = graph.startDestination.route,
                    ) {
                        graph.destinations.forEach { destination ->
                            composable(route = destination.route) { entry ->
                                destination.content(navController, entry)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SideNavigation(
    modifier: Modifier = Modifier,
    visible: Boolean,
    tabs: List<NavigationTab>,
    isSelected: (route: String) -> Boolean,
    onClick: (route: String) -> Unit,
) {
    val focusRequester = rememberFocusRequester()
    var hasFocus by remember { mutableStateOf(false) }
    LaunchedEffect(hasFocus) {
        if (hasFocus) {
            coroutineContext.job.invokeOnCompletion { error ->
                if (error == null) {
                    focusRequester.requestFocus()
                }
            }
        }
    }
    LaunchedEffect(visible) {
        if (visible) {
            coroutineContext.job.invokeOnCompletion { error ->
                if (error == null) {
                    focusRequester.requestFocus()
                }
            }
        }
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandHorizontally(),
        exit = fadeOut() + shrinkHorizontally(),
    ) {
        Column(
            modifier = modifier
                .padding(horizontal = 8.dp)
                .fillMaxHeight()
                .selectableGroup()
                .onFocusChanged { hasFocus = it.hasFocus },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            tabs.forEach { tab ->
                Focusable(
                    modifier = Modifier.focusRequester(
                        if (isSelected(tab.route)) {
                            focusRequester
                        } else {
                            FocusRequester.Default
                        }
                    ),
                    spec = focusableSpec(
                        elevation = ContentElevation.small,
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.surface,
                        paddingValues = PaddingValues(8.dp),
                        scale = ContentScale.large,
                    ),
                ) { padding ->
                    NavigationRailItem(
                        modifier = Modifier.padding(padding),
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = stringResource(tab.labelResId),
                            )
                        },
                        label = {
                            Text(stringResource(tab.labelResId))
                        },
                        selected = isSelected(tab.route),
                        onClick = { onClick(tab.route) },
                    )
                }
            }
        }
    }
}

@Preview(name = "Mobile navbar")
@Composable
private fun BottomNavigation_Preview() {
    BottomNavigation(
        tabs = listOf(
            NavigationTab(
                route = NavigationGraph.Search.route,
                labelResId = R.string.search_label,
                icon = Icons.Outlined.Search,
            ),
            NavigationTab(
                route = NavigationGraph.Forums.route,
                labelResId = R.string.forum_label,
                icon = Icons.Outlined.ListAlt,
            ),
            NavigationTab(
                route = NavigationGraph.Topics.route,
                labelResId = R.string.topics_label,
                icon = Icons.Outlined.Forum,
            ),
            NavigationTab(
                route = NavigationGraph.Menu.route,
                labelResId = R.string.menu_label,
                icon = Icons.Outlined.Menu,
            ),
        ),
        visible = true,
        isSelected = { it == NavigationGraph.Search.route },
        onClick = {},
    )
}

@Preview(name = "Tv navbar", showBackground = true)
@Composable
private fun SideNavigation_Preview() {
    SideNavigation(
        tabs = listOf(
            NavigationTab(
                route = NavigationGraph.Search.route,
                labelResId = R.string.search_label,
                icon = Icons.Outlined.Search,
            ),
            NavigationTab(
                route = NavigationGraph.Forum.route,
                labelResId = R.string.forum_label,
                icon = Icons.Outlined.ListAlt,
            ),
            NavigationTab(
                route = NavigationGraph.Bookmarks.route,
                labelResId = R.string.bookmarks_label,
                icon = Icons.Outlined.Bookmarks,
            ),
            NavigationTab(
                route = NavigationGraph.History.route,
                labelResId = R.string.history_label,
                icon = Icons.Outlined.History,
            ),
            NavigationTab(
                route = NavigationGraph.Favorites.route,
                labelResId = R.string.favorites_label,
                icon = Icons.Outlined.Favorite,
            ),
            NavigationTab(
                route = NavigationGraph.Menu.route,
                labelResId = R.string.settings_label,
                icon = Icons.Outlined.Settings,
            )
        ),
        visible = true,
        isSelected = { it == NavigationGraph.Search.route },
        onClick = {},
    )
}

private data class NavigationTab(
    val route: String,
    @StringRes val labelResId: Int,
    val icon: ImageVector,
)

private sealed interface NavigationGraph {
    val route: String
    val startDestination: NavigationDestination
    val destinations: List<NavigationDestination>

    object Auth : NavigationGraph {
        override val route: String = "auth"
        override val startDestination: NavigationDestination = NavigationDestination.Login
        override val destinations: List<NavigationDestination> = listOf(NavigationDestination.Login)
    }

    object Search : NavigationGraph {
        override val route: String = "search_root"
        override val startDestination: NavigationDestination = NavigationDestination.SearchHistory
        override val destinations: List<NavigationDestination> = listOf(
            NavigationDestination.SearchHistory,
            NavigationDestination.SearchResult,
        )
    }

    object SearchInput : NavigationGraph {
        override val route: String = "search_input_root"
        override val startDestination: NavigationDestination = NavigationDestination.SearchInput
        override val destinations: List<NavigationDestination> =
            listOf(NavigationDestination.SearchInput)
    }

    object Forums : NavigationGraph {
        override val route: String = "forums_root"
        override val startDestination: NavigationDestination = NavigationDestination.Forums
        override val destinations: List<NavigationDestination> = listOf(
            NavigationDestination.Forum,
            NavigationDestination.Forums,
            NavigationDestination.Category,
        )
    }

    object Forum : NavigationGraph {
        override val route: String = "forums_root"
        override val startDestination: NavigationDestination = NavigationDestination.Forum
        override val destinations: List<NavigationDestination> = listOf(
            NavigationDestination.Forum,
            NavigationDestination.Category,
        )
    }

    object Bookmarks : NavigationGraph {
        override val route: String = "bookmarks_root"
        override val startDestination: NavigationDestination = NavigationDestination.Bookmarks
        override val destinations: List<NavigationDestination> =
            listOf(NavigationDestination.Bookmarks)
    }

    object Favorites : NavigationGraph {
        override val route: String = "favorites_root"
        override val startDestination: NavigationDestination = NavigationDestination.Favorites
        override val destinations: List<NavigationDestination> =
            listOf(NavigationDestination.Favorites)
    }

    object History : NavigationGraph {
        override val route: String = "history_root"
        override val startDestination: NavigationDestination = NavigationDestination.History
        override val destinations: List<NavigationDestination> =
            listOf(NavigationDestination.History)
    }

    object Topics : NavigationGraph {
        override val route: String = "topics_root"
        override val startDestination: NavigationDestination = NavigationDestination.Topics
        override val destinations: List<NavigationDestination> =
            listOf(NavigationDestination.Topics)
    }

    object Menu : NavigationGraph {
        override val route: String = "menu_root"
        override val startDestination: NavigationDestination = NavigationDestination.Menu
        override val destinations: List<NavigationDestination> = listOf(NavigationDestination.Menu)
    }

    object Topic : NavigationGraph {
        override val route: String = "topic_root"
        override val startDestination: NavigationDestination = NavigationDestination.Topic
        override val destinations: List<NavigationDestination> = listOf(NavigationDestination.Topic)
    }

    object Torrent : NavigationGraph {
        override val route: String = "torrent_root"
        override val startDestination: NavigationDestination = NavigationDestination.Torrent
        override val destinations: List<NavigationDestination> = listOf(
            NavigationDestination.Torrent,
            NavigationDestination.Topic,
        )
    }

    object OpenTopic : NavigationGraph {
        override val route: String = "open_topic_root"
        override val startDestination: NavigationDestination = NavigationDestination.OpenTopic
        override val destinations: List<NavigationDestination> =
            listOf(NavigationDestination.OpenTopic)
    }
}

private sealed interface NavigationDestination {
    val route: String
    val content: Destination

    object Login : NavigationDestination {
        override val route: String = "${NavigationGraph.Auth.route}/login"
        override val content: Destination = { navController, _ ->
            LoginScreen(back = navController::popBackStack)
        }
    }

    object SearchHistory : NavigationDestination {
        override val route: String = "${NavigationGraph.Search.route}/search_history"
        override val content: Destination = { navController, _ ->
            SearchScreen(
                openLogin = navController::openLogin,
                openSearchInput = navController::openSearchInput,
                openSearch = navController::openSearchResult,
            )
        }
    }

    object SearchInput : NavigationDestination {
        override val route: String = "${NavigationGraph.SearchInput.route}/search_input"
        override val content: Destination = { navController, _ ->
            SearchInputScreen(
                back = navController::popBackStack,
                openSearch = { filter ->
                    navController.popBackStack()
                    navController.openSearchResult(filter)
                },
            )
        }
    }

    object SearchResult : NavigationDestination {
        override val route: String = "${NavigationGraph.Search.route}/search_result"
        override val content: Destination = { navController, _ ->
            SearchResultScreen(
                back = navController::popBackStack,
                openSearchInput = navController::openSearchInput,
                openTorrent = navController::openTorrent,
                openSearchResult = navController::openSearchResult,
            )
        }
    }

    object Forums : NavigationDestination {
        override val route: String = "${NavigationGraph.Forums.route}/forums"
        override val content: Destination = { navController, _ ->
            PagesScreen(
                pages = listOf(
                    Page(
                        labelResId = R.string.forum_title,
                        icon = Icons.Outlined.ListAlt,
                    ) {
                        ForumScreen(openCategory = navController::openCategory)
                    },
                    Page(
                        labelResId = R.string.bookmarks_title,
                        icon = Icons.Outlined.Bookmark,
                    ) {
                        BookmarksScreen(openCategory = navController::openCategory)
                    }
                )
            )
        }
    }

    object Forum : NavigationDestination {
        override val route: String = "${NavigationGraph.Forums.route}/forum"
        override val content: Destination = { navController, _ ->
            ForumScreen(openCategory = navController::openCategory)
        }
    }

    object Bookmarks : NavigationDestination {
        override val route: String = "${NavigationGraph.Bookmarks.route}/bookmarks"
        override val content: Destination = { navController, _ ->
            BookmarksScreen(openCategory = navController::openCategory)
        }
    }

    object Favorites : NavigationDestination {
        override val route: String = "${NavigationGraph.Favorites.route}/favorites"
        override val content: Destination = { navController, _ ->
            FavoritesScreen(
                openTopic = navController::openTopic,
                openTorrent = navController::openTorrent,
            )
        }
    }

    object History : NavigationDestination {
        override val route: String = "${NavigationGraph.History.route}/history"
        override val content: Destination = { navController, _ ->
            HistoryScreen(
                openTopic = navController::openTopic,
                openTorrent = navController::openTorrent,
            )
        }
    }

    object Category : NavigationDestination {
        override val route: String = "${NavigationGraph.Forums.route}/category"
        override val content: Destination = { navController, _ ->
            CategoryScreen(
                back = navController::popBackStack,
                openCategory = navController::openCategory,
                openSearchInput = navController::openSearchInput,
                openTopic = navController::openTopic,
                openTorrent = navController::openTorrent,
            )
        }
    }

    object Topics : NavigationDestination {
        override val route: String = "${NavigationGraph.Topics.route}/topics"
        override val content: Destination = { navController, _ ->
            PagesScreen(
                pages = listOf(
                    Page(
                        labelResId = R.string.topics_history_title,
                        icon = Icons.Outlined.History,
                    ) {
                        HistoryScreen(
                            openTopic = navController::openTopic,
                            openTorrent = navController::openTorrent,
                        )
                    },
                    Page(
                        labelResId = R.string.topics_favorites_title,
                        icon = Icons.Outlined.Favorite,
                    ) {
                        FavoritesScreen(
                            openTopic = navController::openTopic,
                            openTorrent = navController::openTorrent,
                        )
                    },
                )
            )
        }
    }

    object Topic : NavigationDestination {
        override val route: String = "${NavigationGraph.Topic.route}/topic"
        override val content: Destination = { navController, _ ->
            TopicScreen(
                back = navController::popBackStack,
                openLogin = navController::openLogin,
            )
        }
    }

    object Torrent : NavigationDestination {
        override val route: String = "${NavigationGraph.Torrent.route}/torrent"
        override val content: Destination = { navController, _ ->
            TorrentScreen(
                back = navController::popBackStack,
                openLogin = navController::openLogin,
                openComments = navController::openTopic,
                openCategory = navController::openCategory,
                openSearch = navController::openSearchResult,
            )
        }
    }

    object OpenTopic : NavigationDestination {
        override val route: String = "${NavigationGraph.OpenTopic.route}/open_topic"
        override val content: Destination = { navController, _ ->
            OpenTopicScreen(
                back = navController::popBackStack,
                openTopic = {
                    navController.popBackStack()
                    navController.openTopic(it)
                },
                openTorrent = {
                    navController.popBackStack()
                    navController.openTorrent(it)
                },
            )
        }
    }

    object Menu : NavigationDestination {
        override val route: String = "${NavigationGraph.Menu.route}/menu"
        override val content: Destination = { navController, _ ->
            MenuScreen(openLogin = navController::openLogin)
        }
    }
}

fun NavHostController.openSearchResult(filter: Filter) = navigate(
    route = NavigationDestination.SearchResult.route,
    arg = filter.wrap(),
)

fun NavHostController.openCategory(category: Category) = navigate(
    route = NavigationDestination.Category.route,
    arg = category.wrap(),
)

fun NavHostController.openTopic(id: String? = null, pid: String? = null) = navigate(
    route = NavigationDestination.OpenTopic.route,
    args = listOf(id.wrapId(), pid.wrapPid()),
)

private fun NavHostController.openLogin() = navigate(NavigationDestination.Login.route)

private fun NavHostController.openSearchInput(filter: Filter = Filter()) = navigate(
    route = NavigationDestination.SearchInput.route,
    arg = filter.wrap(),
)

private fun NavHostController.openTopic(topic: Topic) = navigate(
    route = NavigationDestination.Topic.route,
    arg = topic.wrap(),
)

private fun NavHostController.openTorrent(torrent: Torrent) = navigate(
    route = NavigationDestination.Torrent.route,
    arg = torrent.wrap(),
)

private fun NavController.navigate(
    route: String,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null,
    arg: Pair<String, Parcelable>,
) {
    navigate(route, navOptions, navigatorExtras)
    currentBackStackEntry?.arguments?.apply {
        putParcelable(arg.first, arg.second)
    }
}

private fun NavController.navigate(
    route: String,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null,
    args: List<Pair<String, Parcelable>> = emptyList(),
) {
    navigate(route, navOptions, navigatorExtras)
    currentBackStackEntry?.arguments?.apply {
        args.forEach { putParcelable(it.first, it.second) }
    }
}
