package flow.search

import flow.models.search.Filter
import flow.navigation.model.NavigationGraphBuilder
import flow.navigation.ui.NavigationAnimations
import flow.navigation.viewModel

private val NavigationGraphBuilder.SearchHistoryRoute
    get() = route("SearchHistory")

fun NavigationGraphBuilder.addSearchHistory(
    openLogin: () -> Unit,
    openSearchInput: () -> Unit,
    openSearchResult: (Filter) -> Unit,
    animations: NavigationAnimations,
) = addDestination(
    route = SearchHistoryRoute,
    isStartRoute = true,
    arguments = emptyList(),
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
