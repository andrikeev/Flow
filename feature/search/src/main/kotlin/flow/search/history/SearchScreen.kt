package flow.search.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import flow.designsystem.component.AppBar
import flow.designsystem.component.AppBarDefaults
import flow.designsystem.component.Button
import flow.designsystem.component.Empty
import flow.designsystem.component.IconButton
import flow.designsystem.component.Placeholder
import flow.designsystem.component.Scaffold
import flow.designsystem.component.ThemePreviews
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.FlowTheme
import flow.models.search.Filter
import flow.models.search.Search
import flow.search.R
import flow.ui.component.dividedItems
import flow.ui.component.loadingItem
import flow.ui.component.resId
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SearchScreen(
    openLogin: () -> Unit,
    openSearchInput: () -> Unit,
    openSearch: (Filter) -> Unit,
) {
    SearchScreen(
        viewModel = hiltViewModel(),
        openLogin = openLogin,
        openSearchInput = openSearchInput,
        openSearch = openSearch,
    )
}

@Composable
private fun SearchScreen(
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
) {
    val scrollBehavior = AppBarDefaults.appBarScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                title = { Text(stringResource(R.string.search_screen_title)) },
                actions = {
                    if (state != SearchState.Unauthorised) {
                        IconButton(
                            onClick = { onAction(SearchAction.SearchActionClick) },
                            imageVector = FlowIcons.Search,
                        )
                    }
                },
                appBarState = scrollBehavior.state,
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            when (state) {
                is SearchState.Unauthorised -> item {
                    Unauthorized(
                        modifier = Modifier.fillParentMaxSize(),
                        onLoginClick = { onAction(SearchAction.LoginClick) }
                    )
                }

                is SearchState.Initial -> loadingItem()
                is SearchState.Empty -> item {
                    Empty(modifier = Modifier.fillParentMaxSize())
                }

                is SearchState.SearchList -> {
                    dividedItems(
                        items = state.items,
                        key = Search::id,
                        contentType = { it::class }
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
}

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
            color = MaterialTheme.colorScheme.primary,
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
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = FlowIcons.Search, contentDescription = null)
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    search.filter.query?.let { query ->
                        stringResource(R.string.search_screen_history_item_query, query)
                    } ?: stringResource(
                        R.string.search_screen_history_item_period,
                        stringResource(search.filter.period.resId),
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = buildString {
                        search.filter.author?.run {
                            append(stringResource(R.string.search_screen_history_item_author, name))
                            append(", ")
                        }
                        val categories = search.filter.categories
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
                            }
                        )
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.outline,
                    ),
                )
            }
            Icon(imageVector = FlowIcons.ArrowRight, contentDescription = null)
        }
    }
}

@ThemePreviews
@Composable
private fun SearchScreen_Preview() {
    FlowTheme {
        SearchScreen(state = SearchState.Empty, onAction = {})
    }
}
