package flow.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import flow.designsystem.component.AppBar
import flow.designsystem.component.AppBarState
import flow.designsystem.component.Body
import flow.designsystem.component.BodyLarge
import flow.designsystem.component.Button
import flow.designsystem.component.CollectionPreviewParameterProvider
import flow.designsystem.component.Divider
import flow.designsystem.component.Empty
import flow.designsystem.component.Icon
import flow.designsystem.component.LazyList
import flow.designsystem.component.Placeholder
import flow.designsystem.component.Scaffold
import flow.designsystem.component.SearchButton
import flow.designsystem.component.SearchIcon
import flow.designsystem.component.Surface
import flow.designsystem.component.Text
import flow.designsystem.component.ThemePreviews
import flow.designsystem.drawables.FlowIcons
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
import kotlinx.coroutines.launch
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
    val swipeState = rememberSwipeState()
    LazyList(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
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
                if (state.pinned.isNotEmpty()) {
                    item { PinnedHeader(modifier = Modifier.animateItem()) }
                    items(
                        items = state.pinned,
                        key = Search::id,
                        contentType = { it::class },
                    ) { search ->
                        PinnedSearch(
                            modifier = Modifier.animateItem(),
                            search = search,
                            swipeState = swipeState,
                            onClick = { onAction(SearchAction.SearchItemClick(search)) },
                            onUnpinClick = { onAction(SearchAction.UnpinItemClick(search)) },
                            onDeleteClick = { onAction(SearchAction.DeleteItemClick(search)) },
                        )
                    }
                }
                if (state.pinned.isNotEmpty() && state.other.isNotEmpty()) {
                    item {
                        Divider(
                            modifier = Modifier
                                .padding(8.dp)
                                .animateItem(),
                        )
                    }
                }
                items(
                    items = state.other,
                    key = Search::id,
                    contentType = { it::class },
                ) { search ->
                    HistorySearch(
                        modifier = Modifier.animateItem(),
                        search = search,
                        swipeState = swipeState,
                        onClick = { onAction(SearchAction.SearchItemClick(search)) },
                        onPinClick = { onAction(SearchAction.PinItemClick(search)) },
                        onDeleteClick = { onAction(SearchAction.DeleteItemClick(search)) },
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
private fun PinnedHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 4.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            icon = FlowIcons.Pin,
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = "PINNED",
        )
    }
}

@Composable
private fun SwipeLayout(
    id: Int,
    swipeState: SwipeState,
    modifier: Modifier = Modifier,
    onLeftActionTriggered: () -> Unit,
    onRightActionTriggered: () -> Unit,
    leftAction: @Composable () -> Unit,
    rightAction: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        val haptic = LocalHapticFeedback.current
        val scope = rememberCoroutineScope()
        val dragOffset = remember { Animatable(0f) }

        var leftThreshold by remember { mutableFloatStateOf(0f) }
        val leftThresholdReached = dragOffset.value > leftThreshold
        var rightThreshold by remember { mutableFloatStateOf(0f) }
        val rightThresholdReached = dragOffset.value < -rightThreshold
        LaunchedEffect(leftThresholdReached, rightThresholdReached) {
            if (leftThresholdReached || rightThresholdReached) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }

        val dragState = rememberDraggableState { dragAmount ->
            val distance = (dragOffset.value + dragAmount)
                .coerceIn(
                    minimumValue = -rightThreshold * 1.1f,
                    maximumValue = leftThreshold * 1.1f,
                )
            scope.launch { dragOffset.snapTo(distance) }
        }
        LaunchedEffect(swipeState.currentSwipeKey) {
            if (swipeState.currentSwipeKey != id) {
                dragOffset.animateTo(0f)
            }
        }

        val leftActionScale by animateFloatAsState(
            targetValue = if (leftThresholdReached) 1.3f else 1f,
            label = "LeftAction_Scale",
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .onSizeChanged { leftThreshold = it.width.toFloat() }
                .padding(horizontal = AppTheme.spaces.mediumLarge)
                .size(48.dp)
                .padding(12.dp)
                .scale(leftActionScale),
            content = { leftAction() },
        )

        val rightActionScale by animateFloatAsState(
            targetValue = if (rightThresholdReached) 1.3f else 1f,
            label = "LeftAction_Scale",
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .onSizeChanged { rightThreshold = it.width.toFloat() }
                .padding(horizontal = AppTheme.spaces.mediumLarge)
                .size(48.dp)
                .padding(12.dp)
                .scale(rightActionScale),
            content = { rightAction() },
        )
        Box(
            modifier = Modifier
                .draggable(
                    state = dragState,
                    orientation = Orientation.Horizontal,
                    onDragStarted = { swipeState.captureSwipe(id) },
                    onDragStopped = {
                        when {
                            rightThresholdReached -> onRightActionTriggered.invoke()
                            leftThresholdReached -> onLeftActionTriggered.invoke()
                        }
                        dragOffset.animateTo(0f)
                    },
                )
                .graphicsLayer { translationX = dragOffset.value },
            content = { content() },
        )
    }
}

@Composable
private fun PinnedSearch(
    modifier: Modifier,
    search: Search,
    swipeState: SwipeState,
    onClick: () -> Unit,
    onUnpinClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    SwipeLayout(
        modifier = modifier,
        id = search.id,
        swipeState = swipeState,
        onLeftActionTriggered = onUnpinClick,
        onRightActionTriggered = onDeleteClick,
        leftAction = {
            Icon(
                icon = FlowIcons.Unpin,
                contentDescription = "Unpin",
                tint = AppTheme.colors.accentBlue,
            )
        },
        rightAction = {
            Icon(
                icon = FlowIcons.Delete,
                contentDescription = "Delete",
                tint = AppTheme.colors.accentRed,
            )
        },
    ) {
        Search(
            search = search,
            onClick = onClick,
        )
    }
}

@Composable
private fun HistorySearch(
    modifier: Modifier,
    search: Search,
    swipeState: SwipeState,
    onClick: () -> Unit,
    onPinClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    SwipeLayout(
        modifier = modifier,
        id = search.id,
        swipeState = swipeState,
        onLeftActionTriggered = onPinClick,
        onRightActionTriggered = onDeleteClick,
        leftAction = {
            Icon(
                icon = FlowIcons.Pin,
                contentDescription = "Pin",
                tint = AppTheme.colors.accentBlue,
            )
        },
        rightAction = {
            Icon(
                icon = FlowIcons.Delete,
                contentDescription = "Delete",
                tint = AppTheme.colors.accentRed,
            )
        },
    ) {
        Search(
            search = search,
            onClick = onClick,
        )
    }
}

@Composable
private fun Search(
    search: Search,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = Surface(
    modifier = modifier.padding(
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

@Stable
private interface SwipeState {
    val currentSwipeKey: Int
    fun captureSwipe(key: Int)
    fun release()
}

@Composable
private fun rememberSwipeState(): SwipeState {
    return remember {
        object : SwipeState {
            override var currentSwipeKey by mutableIntStateOf(-1)

            override fun captureSwipe(key: Int) {
                currentSwipeKey = key
            }

            override fun release() {
                currentSwipeKey = -1
            }
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
        pinned = listOf(
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
        other = listOf(
            Search(10, Filter()),
            Search(11, Filter(query = "The Witcher 3")),
            Search(12, Filter(query = "The Witcher 3", sort = Sort.SEEDS)),
            Search(13, Filter(query = "The Witcher 3", order = Order.DESCENDING)),
            Search(14, Filter(period = Period.LAST_THREE_DAYS, sort = Sort.TITLE)),
            Search(15, Filter(query = "The Witcher 3", author = Author(name = "_aUtHoR_999"))),
            Search(
                16,
                Filter(period = Period.LAST_TWO_WEEKS, author = Author(name = "_aUtHoR_999")),
            ),
            Search(
                17,
                Filter(period = Period.LAST_TWO_WEEKS, author = Author(id = "123123", name = "")),
            ),
            Search(
                18,
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
