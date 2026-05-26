package me.rutrackersearch.app.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import flow.designsystem.component.Page
import flow.designsystem.component.PagesScreen
import flow.designsystem.drawables.FlowIcons
import flow.favorites.FavoritesScreen
import flow.forum.ForumScreen
import flow.forum.bookmarks.BookmarksScreen
import flow.forum.category.CategoryRoute
import flow.forum.category.addCategory
import flow.login.LoginRoute
import flow.login.addLogin
import flow.menu.MenuScreen
import flow.navigation.Navigator
import flow.navigation.model.NavigationBarItem
import flow.navigation.rememberNavigationState
import flow.navigation.rememberNavigator
import flow.navigation.ui.MobileNavigation
import flow.search.SearchHistoryRoute
import flow.search.addSearchHistory
import flow.search.input.SearchInputRoute
import flow.search.input.addSearchInput
import flow.search.result.SearchResultRoute
import flow.search.result.addSearchResult
import flow.topic.TopicRoute
import flow.topic.addTopic
import flow.visited.VisitedScreen
import kotlinx.serialization.Serializable
import me.rutrackersearch.app.R

@Serializable data object ForumTabRoute : NavKey
@Serializable data object TopicsTabRoute : NavKey
@Serializable data object MenuTabRoute : NavKey

@Composable
fun MobileNavigation() {
    val navigationBarItems = remember {
        listOf(
            NavigationBarItem(
                route = SearchHistoryRoute,
                labelResId = R.string.label_search,
                icon = FlowIcons.Search,
            ),
            NavigationBarItem(
                route = ForumTabRoute,
                labelResId = R.string.label_forum,
                icon = FlowIcons.Forum,
            ),
            NavigationBarItem(
                route = TopicsTabRoute,
                labelResId = R.string.label_topics,
                icon = FlowIcons.Topics,
            ),
            NavigationBarItem(
                route = MenuTabRoute,
                labelResId = R.string.label_menu,
                icon = FlowIcons.Menu,
            ),
        )
    }
    val topLevelRoutes = remember(navigationBarItems) {
        navigationBarItems.map(NavigationBarItem::route)
    }
    val state = rememberNavigationState(
        startRoute = SearchHistoryRoute,
        topLevelRoutes = topLevelRoutes,
    )
    val navigator = rememberNavigator(
        state = state,
        deepLinkResolver = ::resolveDeepLink,
    )

    MobileNavigation(
        navigator = navigator,
        navigationBarItems = navigationBarItems,
        entryProvider = entryProvider<NavKey> { addEntries(navigator) },
    )
}

private fun EntryProviderScope<NavKey>.addEntries(navigator: Navigator) {
    addLogin(
        back = { navigator.popBackStack() },
    )
    addSearchInput(
        back = { navigator.popBackStack() },
        openSearchResult = { filter ->
            navigator.popBackStack()
            navigator.navigate(SearchResultRoute(filter))
        },
    )
    addSearchResult(
        back = { navigator.popBackStack() },
        openSearchInput = { navigator.navigate(SearchInputRoute(it)) },
        openSearchResult = { navigator.navigate(SearchResultRoute(it)) },
        openTopic = { navigator.navigate(TopicRoute(it)) },
    )
    addCategory(
        back = { navigator.popBackStack() },
        openCategory = { navigator.navigate(CategoryRoute(it)) },
        openLogin = { navigator.navigate(LoginRoute) },
        openSearchInput = { navigator.navigate(SearchInputRoute(it)) },
        openTopic = { navigator.navigate(TopicRoute(it)) },
    )
    addTopic(
        back = { navigator.popBackStack() },
        openCategory = { navigator.navigate(CategoryRoute(it)) },
        openLogin = { navigator.navigate(LoginRoute) },
        openSearch = { navigator.navigate(SearchResultRoute(it)) },
    )
    addSearchHistory(
        openLogin = { navigator.navigate(LoginRoute) },
        openSearchInput = { navigator.navigate(SearchInputRoute()) },
        openSearchResult = { navigator.navigate(SearchResultRoute(it)) },
    )
    addForumTab(navigator)
    addTopicsTab(navigator)
    addMenuTab(navigator)
}

private fun EntryProviderScope<NavKey>.addForumTab(navigator: Navigator) {
    entry<ForumTabRoute> {
        PagesScreen(
            pages = listOf(
                Page(
                    labelResId = R.string.tab_title_forum,
                    icon = FlowIcons.Forum,
                    content = {
                        ForumScreen { id ->
                            navigator.navigate(CategoryRoute(id))
                        }
                    },
                ),
                Page(
                    labelResId = R.string.tab_title_bookmarks,
                    icon = FlowIcons.Bookmarks,
                    content = {
                        BookmarksScreen { id ->
                            navigator.navigate(CategoryRoute(id))
                        }
                    },
                ),
            ),
        )
    }
}

private fun EntryProviderScope<NavKey>.addTopicsTab(navigator: Navigator) {
    entry<TopicsTabRoute> {
        PagesScreen(
            pages = listOf(
                Page(
                    labelResId = R.string.tab_title_favorites,
                    icon = FlowIcons.Favorite,
                    content = {
                        FavoritesScreen(openTopic = { navigator.navigate(TopicRoute(it)) })
                    },
                ),
                Page(
                    labelResId = R.string.tab_title_recents,
                    icon = FlowIcons.History,
                    content = {
                        VisitedScreen(openTopic = { navigator.navigate(TopicRoute(it)) })
                    },
                ),
            ),
        )
    }
}

private fun EntryProviderScope<NavKey>.addMenuTab(navigator: Navigator) {
    entry<MenuTabRoute> {
        MenuScreen(openLogin = { navigator.navigate(LoginRoute) })
    }
}

private fun resolveDeepLink(uri: Uri): NavKey? {
    if (uri.host != HOST) return null
    return when (uri.path) {
        PATH_TOPIC -> uri.getQueryParameter("t")?.let(::TopicRoute)
        PATH_CATEGORY -> uri.getQueryParameter("f")?.let(::CategoryRoute)
        PATH_SEARCH -> SearchResultRoute(
            query = uri.getQueryParameter("nm"),
            categoryIds = uri.getQueryParameter("f"),
            authorId = uri.getQueryParameter("pid"),
            authorName = uri.getQueryParameter("pn"),
            sort = uri.getQueryParameter("o"),
            order = uri.getQueryParameter("s"),
            period = uri.getQueryParameter("tm"),
        )
        else -> null
    }
}

private const val HOST = "rutracker.org"
private const val PATH_TOPIC = "/forum/viewtopic.php"
private const val PATH_CATEGORY = "/forum/viewforum.php"
private const val PATH_SEARCH = "/forum/tracker.php"
