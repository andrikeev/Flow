package me.rutrackersearch.app.ui.navigation

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import flow.designsystem.component.NavigationBar
import flow.designsystem.component.Scaffold
import flow.designsystem.drawables.FlowIcons
import flow.forum.ForumScreen
import flow.forum.category.CategoryScreen
import flow.login.LoginScreen
import flow.menu.MenuScreen
import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.topic.Topic
import flow.models.topic.Torrent
import flow.search.history.SearchScreen
import flow.search.input.SearchInputScreen
import flow.search.result.SearchResultScreen
import flow.topic.open.OpenTopicScreen
import flow.topic.topic.TopicScreen
import flow.topic.torrent.TorrentScreen
import flow.topics.TopicsScreen
import flow.ui.args.wrap
import flow.ui.args.wrapId
import flow.ui.args.wrapPid

private typealias Destination = @Composable (NavHostController, NavBackStackEntry) -> Unit

@Composable
fun Navigation(navController: NavHostController) {
    Navigation(
        navController = navController,
        navigationTabs = listOf(
            NavigationTab(
                route = NavigationGraph.Search.route,
                labelResId = flow.ui.R.string.search_label,
                icon = FlowIcons.Search,
            ),
            NavigationTab(
                route = NavigationGraph.Forums.route,
                labelResId = flow.ui.R.string.forum_label,
                icon = FlowIcons.Forum,
            ),
            NavigationTab(
                route = NavigationGraph.Topics.route,
                labelResId = flow.ui.R.string.topics_label,
                icon = FlowIcons.Topics,
            ),
            NavigationTab(
                route = NavigationGraph.Menu.route,
                labelResId = flow.ui.R.string.menu_label,
                icon = FlowIcons.Menu,
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
private fun Navigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    navigationTabs: List<NavigationTab>,
    navigationGraphs: List<NavigationGraph>,
) {
    Scaffold(
        modifier = modifier,
        content = { padding ->
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
        },
        bottomBar = {
            val navigationBarRoutes = remember { navigationTabs.map(NavigationTab::route) }
            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentGraphRoute by remember { derivedStateOf { currentBackStackEntry?.destination?.parent?.route } }
            val showNavigationBar by remember { derivedStateOf { navigationBarRoutes.contains(currentGraphRoute) } }
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
    )
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

@Preview(name = "Mobile navbar")
@Composable
private fun BottomNavigation_Preview() {
    BottomNavigation(
        tabs = listOf(
            NavigationTab(
                route = NavigationGraph.Search.route,
                labelResId = flow.ui.R.string.search_label,
                icon = FlowIcons.Search,
            ),
            NavigationTab(
                route = NavigationGraph.Forums.route,
                labelResId = flow.ui.R.string.forum_label,
                icon = FlowIcons.Forum,
            ),
            NavigationTab(
                route = NavigationGraph.Topics.route,
                labelResId = flow.ui.R.string.topics_label,
                icon = FlowIcons.Topics,
            ),
            NavigationTab(
                route = NavigationGraph.Menu.route,
                labelResId = flow.ui.R.string.menu_label,
                icon = FlowIcons.Menu,
            ),
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
            NavigationDestination.Forums,
            NavigationDestination.Category,
        )
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
            ForumScreen(openCategory = navController::openCategory)
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
            TopicsScreen(
                openTopic = navController::openTopic,
                openTorrent = navController::openTorrent,
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
