package flow.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import flow.designsystem.component.AppBar
import flow.designsystem.component.AppBarState
import flow.designsystem.component.Body
import flow.designsystem.component.BodyLarge
import flow.designsystem.component.Button
import flow.designsystem.component.CollectionPreviewParameterProvider
import flow.designsystem.component.Empty
import flow.designsystem.component.LazyList
import flow.designsystem.component.Placeholder
import flow.designsystem.component.Scaffold
import flow.designsystem.component.SearchButton
import flow.designsystem.component.SearchIcon
import flow.designsystem.component.Surface
import flow.designsystem.component.Text
import flow.designsystem.component.ThemePreviews
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme
import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.search.Order
import flow.models.search.Period
import flow.models.search.Search
import flow.models.search.Sort
import flow.models.topic.Author
import flow.ui.component.loadingItem
import flow.ui.component.resId
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun SearchScreen(
    viewModel: SearchViewModel,
    openLogin: () -> Unit,
    openSearchInput: () -> Unit,
    openSearch: (Filter) -> Unit,
) {
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SearchSideEffect.OpenLogin -> openLogin()
            is SearchSideEffect.OpenSearch -> openSearch(sideEffect.filter)
            is SearchSideEffect.OpenSearchInput -> openSearchInput()
        }
    }
    val state by viewModel.collectAsState()
    SearchScreen(state, viewModel::perform)
}

@Composable
private fun SearchScreen(
    state: SearchState,
    onAction: (SearchAction) -> Unit,
) = Scaffold(
    topBar = { appBarState ->
        SearchScreenAppBar(
            state = state,
            appBarState = appBarState,
            onAction = onAction,
        )
    },
) { padding ->
    LazyList(
        modifier = Modifier.padding(padding),
        contentPadding = PaddingValues(vertical = AppTheme.spaces.medium),
    ) {
        when (state) {
            is SearchState.Unauthorised -> item {
                Unauthorized(
                    modifier = Modifier.fillParentMaxSize(),
                    onLoginClick = { onAction(SearchAction.LoginClick) },
                )
            }

            is SearchState.Initial -> loadingItem()
            is SearchState.Empty -> item {
                Empty(modifier = Modifier.fillParentMaxSize())
            }

            is SearchState.SearchList -> {
                items(
                    items = state.items,
                    key = Search::id,
                    contentType = { it::class },
                ) { search ->
                    Search(
                        search = search,
                        onClick = { onAction(SearchAction.SearchItemClick(search)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchScreenAppBar(
    state: SearchState,
    appBarState: AppBarState,
    onAction: (SearchAction) -> Unit,
) = AppBar(
    title = { Text(stringResource(R.string.search_screen_title)) },
    actions = {
        AnimatedVisibility(state.showSearchAction) {
            SearchButton(onClick = { onAction(SearchAction.SearchActionClick) })
        }
    },
    appBarState = appBarState,
)

@Composable
private fun Unauthorized(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit,
) = Placeholder(
    modifier = modifier.fillMaxSize(),
    titleRes = R.string.search_screen_unauthorized_title,
    subtitleRes = R.string.search_screen_unauthorized_subtitle,
    imageRes = R.drawable.ill_unauthorised,
    action = {
        Button(
            onClick = onLoginClick,
            text = stringResource(flow.designsystem.R.string.designsystem_action_login),
            color = AppTheme.colors.primary,
        )
    },
)

@Composable
private fun Empty(modifier: Modifier = Modifier) = Empty(
    modifier = modifier,
    titleRes = R.string.search_screen_history_title,
    subtitleRes = R.string.search_screen_history_subtitle,
    imageRes = R.drawable.ill_search,
)

@Composable
private fun Search(
    search: Search,
    onClick: () -> Unit,
) = Surface(
    modifier = Modifier.padding(
        horizontal = AppTheme.spaces.mediumLarge,
        vertical = AppTheme.spaces.mediumSmall,
    ),
    onClick = onClick,
    shape = AppTheme.shapes.large,
    tonalElevation = AppTheme.elevations.small,
) {
    Row(
        modifier = Modifier.padding(
            horizontal = AppTheme.spaces.large,
            vertical = AppTheme.spaces.mediumLarge,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SearchIcon()
        Column(
            modifier = Modifier
                .padding(start = AppTheme.spaces.large)
                .weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            BodyLarge(search.filter.queryOrPeriod())
            Body(
                text = search.filter.description(),
                color = AppTheme.colors.outline,
            )
        }
    }
}

@Composable
private fun Filter.queryOrPeriod(): String {
    return query?.takeIf(String::isNotBlank)
        ?.let { query -> stringResource(R.string.search_screen_history_item_query, query) }
        ?: stringResource(R.string.search_screen_history_item_period, stringResource(period.resId))
}

@Composable
private fun Filter.description(): String {
    return buildString {
        author?.let<Author, Unit> { (id, name) ->
            if (name.isNotBlank()) {
                append(stringResource(R.string.search_screen_history_item_author, name), ", ")
            } else if (!id.isNullOrBlank()) {
                append(stringResource(R.string.search_screen_history_item_author, "[$id]"), ", ")
            }
        }
        categories.let { categories ->
            append(
                when {
                    categories.isNullOrEmpty() -> {
                        stringResource(R.string.search_screen_history_item_all_categories)
                    }

                    categories.size == 1 -> {
                        stringResource(
                            R.string.search_screen_history_item_category,
                            categories.first().name,
                        )
                    }

                    else -> {
                        stringResource(
                            R.string.search_screen_history_item_categories,
                            categories.size,
                        )
                    }
                },
            )
        }
    }
}

@ThemePreviews
@Composable
private fun SearchScreenPreview(@PreviewParameter(SearchStateProvider::class) state: SearchState) {
    FlowTheme {
        SearchScreen(state = state, onAction = {})
    }
}

private class SearchStateProvider : CollectionPreviewParameterProvider<SearchState>(
    SearchState.Initial,
    SearchState.Unauthorised,
    SearchState.Empty,
    SearchState.SearchList(
        listOf(
            Search(1, Filter()),
            Search(2, Filter(query = "The Witcher 3")),
            Search(3, Filter(query = "The Witcher 3", sort = Sort.SEEDS)),
            Search(4, Filter(query = "The Witcher 3", order = Order.DESCENDING)),
            Search(5, Filter(period = Period.LAST_THREE_DAYS, sort = Sort.TITLE)),
            Search(6, Filter(query = "The Witcher 3", author = Author(name = "_aUtHoR_999"))),
            Search(
                7,
                Filter(period = Period.LAST_TWO_WEEKS, author = Author(name = "_aUtHoR_999")),
            ),
            Search(
                8,
                Filter(period = Period.LAST_TWO_WEEKS, author = Author(id = "123123", name = "")),
            ),
            Search(
                9,
                Filter(
                    query = "Very very Very Very Very very long search query",
                    author = Author(name = "Very very Very Very Very very long name"),
                    categories = listOf(
                        Category(
                            "1",
                            "Very very Very Very Very very long category name",
                        ),
                    ),
                ),
            ),
        ),
    ),
)
