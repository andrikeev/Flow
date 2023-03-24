package flow.search.result

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import flow.designsystem.component.AppBarDefaults
import flow.designsystem.component.AppBarState
import flow.designsystem.component.BackButton
import flow.designsystem.component.ExpandableAppBar
import flow.designsystem.component.IconButton
import flow.designsystem.component.LazyList
import flow.designsystem.component.Scaffold
import flow.designsystem.component.ScrollBackFloatingActionButton
import flow.designsystem.drawables.FlowIcons
import flow.models.search.Filter
import flow.models.topic.Torrent
import flow.search.result.filter.FilterBar
import flow.ui.component.LoadState
import flow.ui.component.TopicListItem
import flow.ui.component.appendItems
import flow.ui.component.dividedItems
import flow.ui.component.emptyItem
import flow.ui.component.errorItem
import flow.ui.component.loadingItem
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun SearchResultScreen(
    viewModel: SearchResultViewModel,
    back: () -> Unit,
    openSearchInput: (Filter) -> Unit,
    openSearchResult: (Filter) -> Unit,
    openTorrent: (Torrent) -> Unit,
) {
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SearchResultSideEffect.Back -> back()
            is SearchResultSideEffect.OpenSearchInput -> openSearchInput(sideEffect.filter)
            is SearchResultSideEffect.OpenSearchResult -> openSearchResult(sideEffect.filter)
            is SearchResultSideEffect.OpenTorrent -> openTorrent(sideEffect.torrent)
        }
    }
    val state by viewModel.collectAsState()
    SearchResultScreen(state, viewModel::perform)
}

@Composable
private fun SearchResultScreen(
    state: SearchResultState,
    onAction: (SearchResultAction) -> Unit,
) {
    val scrollState = rememberLazyListState()
    val scrollBehavior = AppBarDefaults.appBarScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SearchAppBar(
                state = state,
                onAction = onAction,
                appBarState = scrollBehavior.state,
            )
        },
        content = { padding ->
            SearchResultList(
                modifier = Modifier.padding(padding),
                state = state,
                scrollState = scrollState,
                onAction = onAction,
            )
        },
        floatingActionButton = { ScrollBackFloatingActionButton(scrollState) },
    )
}

@Composable
private fun SearchAppBar(
    state: SearchResultState,
    onAction: (SearchResultAction) -> Unit,
    appBarState: AppBarState,
) {
    ExpandableAppBar(
        navigationIcon = { BackButton { onAction(SearchResultAction.BackClick) } },
        title = {
            SearchTextItem(
                modifier = Modifier.fillMaxWidth(),
                filter = state.filter,
                onClick = { onAction(SearchResultAction.SearchClick) },
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        actions = {
            FilterButton(
                isExpanded = state.isAppBarExpanded,
                showBadge = state.showFilterBadge,
                onClick = { onAction(SearchResultAction.ExpandAppBarClick) }
            )
        },
        isExpanded = state.isAppBarExpanded,
        expandableContent = {
            FilterBar(
                filter = state.filter,
                categories = when (state.content) {
                    is SearchResultContent.Content -> state.content.categories
                    is SearchResultContent.Empty,
                    is SearchResultContent.Initial -> emptyList()
                },
                onSelectSort = { onAction(SearchResultAction.SetSort(it)) },
                onSelectOrder = { onAction(SearchResultAction.SetOrder(it)) },
                onSelectPeriod = { onAction(SearchResultAction.SetPeriod(it)) },
                onSelectAuthor = { onAction(SearchResultAction.SetAuthor(it)) },
                onSelectCategories = { onAction(SearchResultAction.SetCategories(it)) },
            )
        },
        appBarState = appBarState,
    )
}

@Composable
private fun FilterButton(
    isExpanded: Boolean,
    showBadge: Boolean,
    onClick: () -> Unit,
) {
    val rotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)
    IconButton(onClick = onClick) {
        BadgedBox(
            badge = { if (!isExpanded && showBadge) Badge() },
            content = {
                Icon(
                    modifier = Modifier.rotate(rotation),
                    imageVector = if (isExpanded) {
                        FlowIcons.Expand
                    } else {
                        FlowIcons.Filters
                    },
                    contentDescription = stringResource(R.string.search_screen_content_description_filter),
                )
            }
        )
    }
}

@Composable
private fun SearchTextItem(
    modifier: Modifier = Modifier,
    filter: Filter,
    onClick: () -> Unit,
    containerColor: Color,
    textColor: Color,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        color = containerColor,
    ) {
        filter.query.let { query ->
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                text = if (query.isNullOrBlank()) {
                    stringResource(R.string.search_screen_input_hint)
                } else {
                    query
                },
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = textColor.copy(
                        alpha = if (query.isNullOrBlank()) {
                            0.7f
                        } else {
                            1f
                        }
                    ),
                ),
            )
        }
    }
}

@Composable
private fun SearchResultList(
    modifier: Modifier,
    state: SearchResultState,
    scrollState: LazyListState,
    onAction: (SearchResultAction) -> Unit,
) {
    LazyList(
        modifier = modifier,
        state = scrollState,
        contentPadding = PaddingValues(top = 8.dp, bottom = 56.dp),
        onEndOfListReached = { onAction(SearchResultAction.ListBottomReached) },
    ) {
        when (state.content) {
            is SearchResultContent.Initial -> when (state.loadStates.refresh) {
                is LoadState.Loading,
                is LoadState.NotLoading -> loadingItem()

                is LoadState.Error -> errorItem(onRetryClick = { onAction(SearchResultAction.RetryClick) })
            }

            is SearchResultContent.Empty -> emptyItem(
                titleRes = R.string.search_screen_result_empty_title,
                subtitleRes = R.string.search_screen_result_empty_subtitle,
                imageRes = flow.ui.R.drawable.ill_empty,
            )

            is SearchResultContent.Content -> {
                dividedItems(items = state.content.torrents,
                    key = { it.topic.id },
                    contentType = { it.topic::class }) { item ->
                    TopicListItem(
                        topicModel = item,
                        onClick = { onAction(SearchResultAction.TorrentClick(item.topic)) },
                        onFavoriteClick = { onAction(SearchResultAction.FavoriteClick(item)) },
                    )
                }
                appendItems(
                    state = state.loadStates.append,
                    onRetryClick = { onAction(SearchResultAction.RetryClick) },
                )
            }
        }
    }
}
