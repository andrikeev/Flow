package flow.search

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import flow.models.search.Filter
import flow.navigation.viewModel
import kotlinx.serialization.Serializable

@Serializable
data object SearchHistoryRoute : NavKey

fun EntryProviderScope<NavKey>.addSearchHistory(
    openLogin: () -> Unit,
    openSearchInput: () -> Unit,
    openSearchResult: (Filter) -> Unit,
) {
    entry<SearchHistoryRoute> {
        SearchScreen(
            viewModel = viewModel(),
            openLogin = openLogin,
            openSearchInput = openSearchInput,
            openSearch = openSearchResult,
        )
    }
}
