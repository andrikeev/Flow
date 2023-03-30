package flow.search.input

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import flow.models.search.Filter
import flow.navigation.NavigationController
import flow.navigation.model.NavigationGraphBuilder
import flow.navigation.ui.NavigationAnimations
import flow.navigation.viewModel
import flow.ui.args.require
import flow.ui.parcel.FilterWrapper

private const val FilterKey = "Filter"

private val NavigationGraphBuilder.SearchInputRoute
    get() = route("SearchInput")

data class SearchInputNavigation(
    val addSearchInput: NavigationGraphBuilder.(
        back: () -> Unit,
        openSearchResult: (Filter) -> Unit,
        animations: NavigationAnimations,
    ) -> Unit,
    val openSearchInput: NavigationController.(filter: Filter) -> Unit,
)

fun NavigationGraphBuilder.buildSearchInputNavigation() = SearchInputNavigation(
    addSearchInput = NavigationGraphBuilder::addSearchInput,
    openSearchInput = { filter ->
        navigate(SearchInputRoute) {
            putFilter(filter)
        }
    },
)

internal fun NavigationGraphBuilder.addSearchInput(
    back: () -> Unit,
    openSearchResult: (Filter) -> Unit,
    animations: NavigationAnimations,
) = addDestination(
    route = SearchInputRoute,
    animations = animations,
) {
    SearchInputScreen(
        viewModel = viewModel(),
        back = back,
        openSearchResult = openSearchResult,
    )
}

private fun Bundle.putFilter(filter: Filter) {
    putParcelable(FilterKey, FilterWrapper(filter))
}

internal val SavedStateHandle.filter: Filter
    get() = require<FilterWrapper>(FilterKey).filter
