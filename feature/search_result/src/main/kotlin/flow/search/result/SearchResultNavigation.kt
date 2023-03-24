package flow.search.result

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import flow.models.search.Filter
import flow.models.topic.Torrent
import flow.navigation.NavigationController
import flow.navigation.model.NavigationGraphBuilder
import flow.navigation.ui.NavigationAnimations
import flow.navigation.viewModel
import flow.ui.args.require
import flow.ui.parcel.FilterWrapper

private const val FilterKey = "Filter"

private val NavigationGraphBuilder.SearchResultRoute
    get() = route("SearchResult")

data class SearchResultNavigation(
    val addSearchResult: NavigationGraphBuilder.(
        back: () -> Unit,
        openSearchInput: (Filter) -> Unit,
        openSearchResult: (Filter) -> Unit,
        openTorrent: (Torrent) -> Unit,
        animations: NavigationAnimations,
    ) -> Unit,
    val openSearchResult: NavigationController.(
        filter: Filter,
    ) -> Unit,
)

fun NavigationGraphBuilder.buildSearchResultNavigation() = SearchResultNavigation(
    addSearchResult = NavigationGraphBuilder::addSearchResult,
    openSearchResult = { filter ->
        navigate(SearchResultRoute) {
            putFilter(filter)
        }
    },
)

internal fun NavigationGraphBuilder.addSearchResult(
    back: () -> Unit,
    openSearchInput: (Filter) -> Unit,
    openSearchResult: (Filter) -> Unit,
    openTorrent: (Torrent) -> Unit,
    animations: NavigationAnimations,
) = addDestination(
    route = SearchResultRoute,
    animations = animations,
) {
    SearchResultScreen(
        viewModel = viewModel(),
        back = back,
        openSearchInput = openSearchInput,
        openSearchResult = openSearchResult,
        openTorrent = openTorrent,
    )
}

private fun Bundle.putFilter(filter: Filter) {
    putParcelable(FilterKey, FilterWrapper(filter))
}

internal val SavedStateHandle.filter: Filter
    get() = require<FilterWrapper>(FilterKey).filter
