package me.rutrackersearch.app.ui.search.root

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import me.rutrackersearch.app.R
import me.rutrackersearch.app.ui.common.AppBar
import me.rutrackersearch.app.ui.common.Button
import me.rutrackersearch.app.ui.common.DynamicBox
import me.rutrackersearch.app.ui.common.Empty
import me.rutrackersearch.app.ui.common.FocusableLazyColumn
import me.rutrackersearch.app.ui.common.IconButton
import me.rutrackersearch.app.ui.common.Placeholder
import me.rutrackersearch.app.ui.common.dividedItems
import me.rutrackersearch.app.ui.common.focusableItems
import me.rutrackersearch.app.ui.common.loadingItem
import me.rutrackersearch.app.ui.common.resId
import me.rutrackersearch.models.search.Filter
import me.rutrackersearch.models.search.Search
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
    DynamicBox(
        mobileContent = {
            MobileSearchScreen(
                state = state,
                onAction = onAction,
            )
        },
        tvContent = {
            TVSearchScreen(
                state = state,
                onAction = onAction,
            )
        },
    )
}

@Composable
private fun MobileSearchScreen(
    state: SearchState,
    onAction: (SearchAction) -> Unit,
) {
    val pinnedScrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }
    Scaffold(
        modifier = Modifier.nestedScroll(pinnedScrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                title = { Text(stringResource(R.string.search_title)) },
                actions = {
                    if (state != SearchState.Unauthorised) {
                        IconButton(
                            onClick = { onAction(SearchAction.SearchActionClick) },
                            imageVector = Icons.Outlined.Search,
                        )
                    }
                },
                scrollBehavior = pinnedScrollBehavior,
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
private fun TVSearchScreen(
    state: SearchState,
    onAction: (SearchAction) -> Unit,
) {
    FocusableLazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 32.dp,
            top = 16.dp,
            end = 32.dp,
            bottom = 32.dp,
        ),
    ) {
        when (state) {
            is SearchState.Unauthorised -> item {
                Unauthorized(
                    modifier = Modifier.fillParentMaxSize(),
                    onLoginClick = { onAction(SearchAction.LoginClick) },
                )
            }

            is SearchState.Initial -> loadingItem()
            is SearchState.Empty -> {
                focusableStickyHeader { SearchActionItem(onClick = { onAction(SearchAction.SearchActionClick) }) }
                item { Empty(modifier = Modifier.fillParentMaxSize()) }
            }

            is SearchState.SearchList -> {
                focusableStickyHeader { SearchActionItem(onClick = { onAction(SearchAction.SearchActionClick) }) }
                focusableItems(state.items) { search ->
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
private fun Unauthorized(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit,
) {
    Placeholder(
        modifier = modifier.fillMaxSize(),
        title = { Text(stringResource(R.string.search_unauthorized_title)) },
        subtitle = {
            Text(
                text = stringResource(R.string.search_unauthorized_subtitle),
                textAlign = TextAlign.Center,
            )
        },
        icon = {
            Image(
                painter = painterResource(R.drawable.ill_auth_requried),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
            )
        },
        action = {
            Button(
                onClick = onLoginClick,
                text = stringResource(R.string.action_login),
                color = MaterialTheme.colorScheme.primary,
            )
        },
    )
}

@Composable
private fun Empty(modifier: Modifier = Modifier) = Empty(
    modifier = modifier,
    titleRes = R.string.search_history_title,
    subtitleRes = R.string.search_history_subtitle,
    iconRes = R.drawable.ill_search,
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
            Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    search.filter.query?.let { query ->
                        stringResource(R.string.search_item_query, query)
                    } ?: stringResource(
                        R.string.search_item_period,
                        stringResource(search.filter.period.resId),
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = buildString {
                        search.filter.author?.run {
                            append(stringResource(R.string.search_item_author, name))
                            append(", ")
                        }
                        val categories = search.filter.categories
                        append(
                            when {
                                categories.isNullOrEmpty() -> {
                                    stringResource(R.string.search_item_all_categories)
                                }

                                categories.size == 1 -> {
                                    stringResource(
                                        R.string.search_item_category,
                                        categories.first().name,
                                    )
                                }

                                else -> {
                                    stringResource(
                                        R.string.search_item_categories,
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
            Icon(imageVector = Icons.Outlined.ChevronRight, contentDescription = null)
        }
    }
}

@Composable
private fun SearchActionItem(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            text = stringResource(R.string.search_input_hint),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.outline,
            ),
        )
    }
}
