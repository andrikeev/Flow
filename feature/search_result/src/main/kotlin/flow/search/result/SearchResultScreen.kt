package flow.search.result

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import flow.designsystem.component.AppBarDefaults
import flow.designsystem.component.AppBarState
import flow.designsystem.component.BackButton
import flow.designsystem.component.BadgeBox
import flow.designsystem.component.BodyLarge
import flow.designsystem.component.ExpandableAppBar
import flow.designsystem.component.Icon
import flow.designsystem.component.IconButton
import flow.designsystem.component.InvertedSurface
import flow.designsystem.component.LazyList
import flow.designsystem.component.Scaffold
import flow.designsystem.component.ScrollBackFloatingActionButton
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.models.LoadState
import flow.models.search.Filter
import flow.models.topic.Torrent
import flow.search.result.filter.FilterBar
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
    state: SearchPageState,
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
    state: SearchPageState,
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
            )
        },
        actions = {
            FilterButton(
                expanded = state.appBarExpanded,
                showBadge = state.showFilterBadge,
                onClick = { onAction(SearchResultAction.ExpandAppBarClick) }
            )
        },
        expanded = state.appBarExpanded,
        expandableContent = {
            FilterBar(
                filter = state.filter,
                categories = state.categories,
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
    expanded: Boolean,
    showBadge: Boolean,
    onClick: () -> Unit,
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "FilterButton_Rotation"
    )
    IconButton(onClick = onClick) {
        BadgeBox(
            showBadge = !expanded && showBadge,
            content = {
                Icon(
                    modifier = Modifier.rotate(rotation),
                    icon = if (expanded) {
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
    filter: Filter,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = InvertedSurface(
    modifier = modifier
        .fillMaxWidth()
        .height(AppTheme.sizes.default),
    onClick = onClick,
    shape = AppTheme.shapes.small,
    tonalElevation = AppTheme.elevations.medium,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterStart,
    ) {
        BodyLarge(
            modifier = Modifier
                .padding(horizontal = AppTheme.spaces.large)
                .alpha(if (filter.query.isNullOrBlank()) 0.7f else 1f),
            text = filter.query?.takeIf(String::isNotBlank)
                ?: stringResource(flow.designsystem.R.string.designsystem_hint_search),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SearchResultList(
    modifier: Modifier,
    state: SearchPageState,
    scrollState: LazyListState,
    onAction: (SearchResultAction) -> Unit,
) {
    LazyList(
        modifier = modifier,
        state = scrollState,
        contentPadding = PaddingValues(
            top = AppTheme.spaces.medium,
            bottom = AppTheme.spaces.extraLargeBottom,
        ),
        onLastItemVisible = { onAction(SearchResultAction.ListBottomReached) },
    ) {
        when (state.loadStates.refresh) {
            is LoadState.Error -> errorItem(onRetryClick = { onAction(SearchResultAction.RetryClick) })
            is LoadState.Loading -> loadingItem()
            is LoadState.NotLoading -> when (state.searchContent) {
                is SearchResultContent.Content -> {
                    dividedItems(items = state.searchContent.torrents,
                        key = { it.topic.id },
                        contentType = { it.topic::class }) { item ->
                        TopicListItem(
                            topicModel = item,
                            onClick = { onAction(SearchResultAction.TorrentClick(item)) },
                            onFavoriteClick = { onAction(SearchResultAction.FavoriteClick(item)) },
                        )
                    }
                    appendItems(
                        state = state.loadStates.append,
                        onRetryClick = { onAction(SearchResultAction.RetryClick) },
                    )
                }
                is SearchResultContent.Empty -> emptyItem(
                    titleRes = R.string.search_screen_result_empty_title,
                    subtitleRes = R.string.search_screen_result_empty_subtitle,
                    imageRes = flow.ui.R.drawable.ill_empty,
                )
                is SearchResultContent.Initial -> loadingItem()
            }
        }
    }
}
