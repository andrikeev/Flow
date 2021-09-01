package me.rutrackersearch.app.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.job
import me.rutrackersearch.app.R
import me.rutrackersearch.app.ui.args.wrap
import me.rutrackersearch.app.ui.args.wrapId
import me.rutrackersearch.app.ui.args.wrapPid
import me.rutrackersearch.app.ui.auth.LoginScreen
import me.rutrackersearch.app.ui.common.ContentElevation
import me.rutrackersearch.app.ui.common.ContentScale
import me.rutrackersearch.app.ui.common.Focusable
import me.rutrackersearch.app.ui.common.Page
import me.rutrackersearch.app.ui.common.PagesScreen
import me.rutrackersearch.app.ui.common.focusableSpec
import me.rutrackersearch.app.ui.common.rememberFocusRequester
import me.rutrackersearch.app.ui.forum.bookmarks.BookmarksScreen
import me.rutrackersearch.app.ui.forum.category.CategoryScreen
import me.rutrackersearch.app.ui.forum.root.ForumScreen
import me.rutrackersearch.app.ui.menu.MenuScreen
import me.rutrackersearch.app.ui.search.SearchScreen
import me.rutrackersearch.app.ui.search.input.SearchInputScreen
import me.rutrackersearch.app.ui.search.result.SearchResultScreen
import me.rutrackersearch.app.ui.theme.isLight
import me.rutrackersearch.app.ui.theme.surfaceColorAtElevation
import me.rutrackersearch.app.ui.topic.open.OpenTopicScreen
import me.rutrackersearch.app.ui.topic.topic.TopicScreen
import me.rutrackersearch.app.ui.topic.torrent.TorrentScreen
import me.rutrackersearch.app.ui.topics.favorites.FavoritesScreen
import me.rutrackersearch.app.ui.topics.history.HistoryScreen
import me.rutrackersearch.domain.entity.forum.Category
import me.rutrackersearch.domain.entity.search.Filter
import me.rutrackersearch.domain.entity.topic.Author
import me.rutrackersearch.domain.entity.topic.Topic
import me.rutrackersearch.domain.entity.topic.Torrent

typealias Destination = @Composable (NavHostController, NavBackStackEntry) -> Unit

@Composable
fun MobileNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            val navigationTabs = BottomNavigationTabs.tabs
            val navigationBarRoutes = navigationTabs.map(BottomNavigationTabs::route)
            val currentEntry by navController.currentBackStackEntryAsState()
            val currentGraphRoute = currentEntry?.destination?.parent?.route
            val showNavigationBar = navigationBarRoutes.contains(currentGraphRoute)
            val isNavigationItemSelected: (route: String) -> Boolean = { route ->
                currentGraphRoute == route
            }
            val onNavigationItemSelected: (route: String) -> Unit = { route ->
                navController.navigate(route) {
                    launchSingleTop = true
                    restoreState = true
                }
            }
            BottomNavigation(
                tabs = navigationTabs,
                visible = showNavigationBar,
                isSelected = isNavigationItemSelected,
                onClick = onNavigationItemSelected,
            )
        },
    ) { padding ->
        NavHost(
            modifier = Modifier.padding(padding),
            navController = navController,
            startDestination = NavigationGraph.Search.route,
        ) {
            NavigationGraph.bottomNavigationGraphs.forEach { graph ->
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
    modifier: Modifier = Modifier,
    visible: Boolean,
    tabs: List<BottomNavigationTabs>,
    isSelected: (route: String) -> Boolean,
    onClick: (route: String) -> Unit,
) {
    val systemUiController = rememberSystemUiController()
    val elevation by animateDpAsState(
        targetValue = if (visible) {
            ContentElevation.small
        } else {
            ContentElevation.zero
        },
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing,
        )
    )
    val systemUiColor = MaterialTheme.colorScheme.surfaceColorAtElevation(elevation)
    LaunchedEffect(systemUiColor) {
        systemUiController.setNavigationBarColor(
            color = systemUiColor,
            darkIcons = systemUiColor.isLight(),
        )
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        NavigationBar(modifier = modifier) {
            tabs.forEach { tab ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = stringResource(tab.labelResId),
                        )
                    },
                    label = { Text(stringResource(tab.labelResId)) },
                    selected = isSelected(tab.route),
                    onClick = { onClick(tab.route) }
                )
            }
        }
    }
}

private sealed class BottomNavigationTabs(
    val route: String,
    @StringRes val labelResId: Int,
    val icon: ImageVector,
) {
    private object Search : BottomNavigationTabs(
        route = NavigationGraph.Search.route,
        labelResId = R.string.search_label,
        icon = Icons.Outlined.Search,
    )

    private object Forum : BottomNavigationTabs(
        route = NavigationGraph.Forums.route,
        labelResId = R.string.forum_label,
        icon = Icons.Outlined.ListAlt,
    )

    private object Topics : BottomNavigationTabs(
        route = NavigationGraph.Topics.route,
        labelResId = R.string.topics_label,
        icon = Icons.Outlined.Forum,
    )

    private object Menu : BottomNavigationTabs(
        route = NavigationGraph.Menu.route,
        labelResId = R.string.menu_label,
        icon = Icons.Outlined.Menu,
    )

    companion object {
        val tabs = listOf(Search, Forum, Topics, Menu)
    }
}

@Composable
fun TVNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    Scaffold(modifier = modifier) { padding ->
        val navigationTabs = SideNavigationTabs.tabs
        val navigationBarRoutes = navigationTabs.map(SideNavigationTabs::route)
        val currentEntry by navController.currentBackStackEntryAsState()
        val currentGraphRoute = currentEntry?.destination?.parent?.route
        val showNavigationBar = navigationBarRoutes.contains(currentGraphRoute)
        val isNavigationItemSelected: (route: String) -> Boolean = { route ->
            currentGraphRoute == route
        }
        val onNavigationItemSelected: (route: String) -> Unit = { route ->
            navController.navigate(route) {
                launchSingleTop = true
                restoreState = true
            }
        }
        Row(
            modifier = Modifier.padding(padding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SideNavigation(
                tabs = navigationTabs,
                visible = showNavigationBar,
                isTabSelected = isNavigationItemSelected,
                onClick = onNavigationItemSelected,
            )
            NavHost(
                modifier = Modifier.padding(padding),
                navController = navController,
                startDestination = NavigationGraph.Search.route,
            ) {
                NavigationGraph.sideNavigationGraphs.forEach { graph ->
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
    tabs: List<SideNavigationTabs>,
    isTabSelected: (route: String) -> Boolean,
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
                val isSelected = isTabSelected(tab.route)
                Focusable(
                    modifier = Modifier.focusRequester(
                        if (isSelected) {
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
                        selected = isSelected,
                        onClick = { onClick(tab.route) },
                    )
                }
            }
        }
    }
}

private sealed class SideNavigationTabs(
    val route: String,
    @StringRes val labelResId: Int,
    val icon: ImageVector,
) {
    private object Search : SideNavigationTabs(
        route = NavigationGraph.Search.route,
        labelResId = R.string.search_label,
        icon = Icons.Outlined.Search,
    )

    private object Forum : SideNavigationTabs(
        route = NavigationGraph.Forum.route,
        labelResId = R.string.forum_label,
        icon = Icons.Outlined.ListAlt,
    )

    private object Bookmarks : SideNavigationTabs(
        route = NavigationGraph.Bookmarks.route,
        labelResId = R.string.bookmarks_label,
        icon = Icons.Outlined.Bookmarks,
    )

    private object History : SideNavigationTabs(
        route = NavigationGraph.History.route,
        labelResId = R.string.history_label,
        icon = Icons.Outlined.History,
    )

    private object Favorites : SideNavigationTabs(
        route = NavigationGraph.Favorites.route,
        labelResId = R.string.favorites_label,
        icon = Icons.Outlined.Favorite,
    )

    private object Settings : SideNavigationTabs(
        route = NavigationGraph.Menu.route,
        labelResId = R.string.settings_label,
        icon = Icons.Outlined.Settings,
    )

    companion object {
        val tabs = listOf(Search, Forum, Bookmarks, History, Favorites, Settings)
    }
}

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

    companion object {
        val bottomNavigationGraphs = listOf(
            Auth,
            Search,
            SearchInput,
            Forums,
            Topics,
            Menu,
            OpenTopic,
            Topic,
            Torrent
        )
        val sideNavigationGraphs = listOf(
            Auth,
            Search,
            SearchInput,
            Forum,
            Bookmarks,
            Favorites,
            History,
            Menu,
            OpenTopic,
            Topic,
            Torrent
        )
    }
}

private sealed interface NavigationDestination {
    val route: String
    val content: Destination

    object Login : NavigationDestination {
        override val route: String = "${NavigationGraph.Auth.route}/login"
        override val content: Destination = { navController, _ ->
            LoginScreen(onSuccess = navController::popBackStack)
        }
    }

    object SearchHistory : NavigationDestination {
        override val route: String = "${NavigationGraph.Search.route}/search_history"
        override val content: Destination = { navController, _ ->
            SearchScreen(
                onLoginClick = navController::openLogin,
                onSearchActionClick = navController::openSearchInput,
                onSearchClick = navController::openSearchResult,
            )
        }
    }

    object SearchInput : NavigationDestination {
        override val route: String = "${NavigationGraph.SearchInput.route}/search_input"
        override val content: Destination = { navController, _ ->
            SearchInputScreen(
                onBackClick = navController::popBackStack,
                onSubmit = { filter ->
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
                onBackClick = navController::popBackStack,
                onSearchClick = navController::openSearchInput,
                onTorrentClick = navController::openTorrent,
                onNewFilter = navController::openSearchResult,
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
                        ForumScreen(onCategoryClick = navController::openCategory)
                    },
                    Page(
                        labelResId = R.string.bookmarks_title,
                        icon = Icons.Outlined.Bookmark,
                    ) {
                        BookmarksScreen(onBookmarkClick = navController::openCategory)
                    }
                )
            )
        }
    }

    object Forum : NavigationDestination {
        override val route: String = "${NavigationGraph.Forums.route}/forum"
        override val content: Destination = { navController, _ ->
            ForumScreen(onCategoryClick = navController::openCategory)
        }
    }

    object Bookmarks : NavigationDestination {
        override val route: String = "${NavigationGraph.Bookmarks.route}/bookmarks"
        override val content: Destination = { navController, _ ->
            BookmarksScreen(onBookmarkClick = navController::openCategory)
        }
    }

    object Favorites : NavigationDestination {
        override val route: String = "${NavigationGraph.Favorites.route}/favorites"
        override val content: Destination = { navController, _ ->
            FavoritesScreen(
                onTopicClick = navController::openTopic,
                onTorrentClick = navController::openTorrent,
            )
        }
    }

    object History : NavigationDestination {
        override val route: String = "${NavigationGraph.History.route}/history"
        override val content: Destination = { navController, _ ->
            HistoryScreen(
                onTopicClick = navController::openTopic,
                onTorrentClick = navController::openTorrent,
            )
        }
    }

    object Category : NavigationDestination {
        override val route: String = "${NavigationGraph.Forums.route}/category"
        override val content: Destination = { navController, _ ->
            CategoryScreen(
                onBackClick = navController::popBackStack,
                onCategoryClick = navController::openCategory,
                onTopicClick = navController::openTopic,
                onTorrentClick = navController::openTorrent,
                onSearchClick = navController::openSearchResult,
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
                            onTopicClick = navController::openTopic,
                            onTorrentClick = navController::openTorrent,
                        )
                    },
                    Page(
                        labelResId = R.string.topics_favorites_title,
                        icon = Icons.Outlined.Favorite,
                    ) {
                        FavoritesScreen(
                            onTopicClick = navController::openTopic,
                            onTorrentClick = navController::openTorrent,
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
                onBackClick = navController::popBackStack,
                onLoginClick = navController::openLogin,
            )
        }
    }

    object Torrent : NavigationDestination {
        override val route: String = "${NavigationGraph.Torrent.route}/torrent"
        override val content: Destination = { navController, _ ->
            TorrentScreen(
                onBackClick = navController::popBackStack,
                onLoginClick = navController::openLogin,
                onCommentsClick = navController::openTopic,
                onCategoryClick = navController::openCategory,
                onAuthorClick = navController::openSearchResult,
            )
        }
    }

    object OpenTopic : NavigationDestination {
        override val route: String = "${NavigationGraph.OpenTopic.route}/open_topic"
        override val content: Destination = { navController, _ ->
            OpenTopicScreen(
                onBackClick = navController::popBackStack,
                onTopicLoaded = {
                    navController.popBackStack()
                    navController.openTopic(it)
                },
                onTorrentLoaded = {
                    navController.popBackStack()
                    navController.openTorrent(it)
                },
            )
        }
    }

    object Menu : NavigationDestination {
        override val route: String = "${NavigationGraph.Menu.route}/menu"
        override val content: Destination = { navController, _ ->
            MenuScreen(onLoginClick = navController::openLogin)
        }
    }
}

private fun NavHostController.openLogin() {
    navigate(NavigationDestination.Login.route)
}

private fun NavHostController.openSearchInput(filter: Filter = Filter()) {
    navigate(
        route = NavigationDestination.SearchInput.route,
        args = listOf(filter.wrap()),
    )
}

private fun NavHostController.openSearchResult(category: Category) {
    openSearchResult(Filter(categories = listOf(category)))
}

private fun NavHostController.openSearchResult(author: Author) {
    openSearchResult(Filter(author = author))
}

fun NavHostController.openSearchResult(filter: Filter) {
    navigate(
        route = NavigationDestination.SearchResult.route,
        args = listOf(filter.wrap()),
    )
}

fun NavHostController.openCategory(category: Category) {
    navigate(
        route = NavigationDestination.Category.route,
        args = listOf(category.wrap()),
    )
}

fun NavHostController.openTopic(id: String? = null, pid: String? = null) {
    navigate(
        route = NavigationDestination.OpenTopic.route,
        args = listOf(id.wrapId(), pid.wrapPid()),
    )
}

private fun NavHostController.openTopic(topic: Topic) {
    navigate(
        route = NavigationDestination.Topic.route,
        args = listOf(topic.wrap()),
    )
}

private fun NavHostController.openTorrent(torrent: Torrent) {
    navigate(
        route = NavigationDestination.Torrent.route,
        args = listOf(torrent.wrap()),
    )
}
