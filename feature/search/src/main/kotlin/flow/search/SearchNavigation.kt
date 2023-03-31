package flow.search

import flow.models.search.Filter
import flow.navigation.model.NavigationGraphBuilder
import flow.navigation.model.buildRoute
import flow.navigation.ui.NavigationAnimations
import flow.navigation.viewModel

private const val SearchHistoryRoute = "search_history"

context(NavigationGraphBuilder)
fun addSearchHistory(
    openLogin: () -> Unit,
    openSearchInput: () -> Unit,
    openSearchResult: (Filter) -> Unit,
    animations: NavigationAnimations,
) = addDestination(
    route = buildRoute(SearchHistoryRoute),
    isStartRoute = true,
    content = {
        SearchScreen(
            viewModel = viewModel(),
            openLogin = openLogin,
            openSearchInput = openSearchInput,
            openSearch = openSearchResult,
        )
    },
    animations = animations,
)
