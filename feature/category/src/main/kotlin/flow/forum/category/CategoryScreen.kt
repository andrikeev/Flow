package flow.forum.category

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import flow.designsystem.component.AppBar
import flow.designsystem.component.AppBarDefaults
import flow.designsystem.component.AppBarState
import flow.designsystem.component.BackButton
import flow.designsystem.component.IconButton
import flow.designsystem.component.LazyList
import flow.designsystem.component.Scaffold
import flow.designsystem.component.ScrollBackFloatingActionButton
import flow.designsystem.drawables.FlowIcons
import flow.models.forum.Category
import flow.models.forum.CategoryModel
import flow.models.search.Filter
import flow.models.topic.Topic
import flow.models.topic.TopicModel
import flow.models.topic.Torrent
import flow.navigation.viewModel
import flow.ui.component.CategoryListItem
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
fun CategoryScreen(
    back: () -> Unit,
    openCategory: (Category) -> Unit,
    openSearchInput: (Filter) -> Unit,
    openTopic: (Topic) -> Unit,
    openTorrent: (Torrent) -> Unit,
) {
    CategoryScreen(
        viewModel = viewModel(),
        back = back,
        openCategory = openCategory,
        openSearchInput = openSearchInput,
        openTopic = openTopic,
        openTorrent = openTorrent,
    )
}

@Composable
private fun CategoryScreen(
    viewModel: CategoryViewModel,
    back: () -> Unit,
    openCategory: (Category) -> Unit,
    openSearchInput: (Filter) -> Unit,
    openTopic: (Topic) -> Unit,
    openTorrent: (Torrent) -> Unit,
) {
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is CategorySideEffect.Back -> back()
            is CategorySideEffect.OpenCategory -> openCategory(sideEffect.category)
            is CategorySideEffect.OpenSearch -> openSearchInput(sideEffect.filter)
            is CategorySideEffect.OpenTopic -> openTopic(sideEffect.topic)
            is CategorySideEffect.OpenTorrent -> openTorrent(sideEffect.torrent)
        }
    }
    val state by viewModel.collectAsState()
    CategoryScreen(state, viewModel::perform)
}

@Composable
private fun CategoryScreen(
    state: CategoryState,
    onAction: (CategoryAction) -> Unit,
) {
    val scrollState = rememberLazyListState()
    val scrollBehavior = AppBarDefaults.appBarScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            val category = state.categoryModelState.category
            val showBookmark = state.categoryModelState is CategoryModelState.Loaded
            val isBookmark = state.categoryModelState is CategoryModelState.Loaded &&
                    state.categoryModelState.categoryModel.isBookmark
            CategoryAppBar(
                category = category,
                showBookmark = showBookmark,
                isBookmark = isBookmark,
                onBookmarkClick = { onAction(CategoryAction.BookmarkClick) },
                onSearchClick = { onAction(CategoryAction.SearchClick) },
                onBackClick = { onAction(CategoryAction.BackClick) },
                appBarState = scrollBehavior.state,
            )
        },
        content = { padding ->
            CategoryScreenList(
                modifier = Modifier.padding(padding),
                state = state,
                scrollState = scrollState,
                onAction = onAction,
            )
        },
        floatingActionButton = { ScrollBackFloatingActionButton(scrollState) }
    )
}

@Composable
private fun CategoryScreenList(
    modifier: Modifier = Modifier,
    state: CategoryState,
    scrollState: LazyListState,
    onAction: (CategoryAction) -> Unit,
) = LazyList(
    modifier = modifier,
    state = scrollState,
    contentPadding = PaddingValues(top = 8.dp, bottom = 64.dp),
    onEndOfListReached = { onAction(CategoryAction.EndOfListReached) },
) {
    when (state.content) {
        is CategoryContent.Initial -> when (state.loadStates.refresh) {
            is LoadState.Loading,
            is LoadState.NotLoading -> loadingItem()

            is LoadState.Error -> errorItem(onRetryClick = { onAction(CategoryAction.RetryClick) })
        }

        is CategoryContent.Empty -> emptyItem(
            titleRes = R.string.forum_screen_forum_empty_title,
            subtitleRes = R.string.forum_screen_forum_empty_subtitle,
            imageRes = flow.ui.R.drawable.ill_empty,
        )

        is CategoryContent.Content -> {
            dividedItems(
                items = state.content.run { categories + topics },
                key = { item ->
                    when (item) {
                        is CategoryModel -> item.category.id
                        is TopicModel<out Topic> -> item.topic.id
                        else -> Unit
                    }
                },
                contentType = { item ->
                    when (item) {
                        is CategoryModel -> item.category::class
                        is TopicModel<out Topic> -> item.topic::class
                        else -> Unit
                    }
                },
            ) { item ->
                when (item) {
                    is CategoryModel -> CategoryListItem(
                        text = item.category.name,
                        onClick = { onAction(CategoryAction.CategoryClick(item.category)) }
                    )

                    is TopicModel<out Topic> -> TopicListItem(
                        topicModel = item,
                        showCategory = false,
                        onClick = {
                            val topic = item.topic
                            onAction(
                                if (topic is Torrent) {
                                    CategoryAction.TorrentClick(topic)
                                } else {
                                    CategoryAction.TopicClick(topic)
                                }
                            )
                        },
                        onFavoriteClick = { onAction(CategoryAction.FavoriteClick(item)) },
                    )

                    else -> Unit
                }
            }
            appendItems(
                state = state.loadStates.append,
                onRetryClick = { onAction(CategoryAction.RetryClick) },
            )
        }
    }
}


@Composable
internal fun CategoryAppBar(state: AppBarState) {
    CategoryAppBar(
        viewModel = viewModel(),
        appBarState = state,
    )
}

@Composable
private fun CategoryAppBar(
    viewModel: CategoryViewModel,
    appBarState: AppBarState,
) {
    val state by viewModel.collectAsState()
    CategoryAppBar(
        state = state,
        appBarState = appBarState,
        onAction = viewModel::perform,
    )
}

@Composable
private fun CategoryAppBar(
    state: CategoryState,
    appBarState: AppBarState,
    onAction: (CategoryAction) -> Unit,
) {
    val category = state.categoryModelState.category
    val showBookmark = state.categoryModelState is CategoryModelState.Loaded
    val isBookmark = state.categoryModelState is CategoryModelState.Loaded &&
            state.categoryModelState.categoryModel.isBookmark
    CategoryAppBar(
        category = category,
        showBookmark = showBookmark,
        isBookmark = isBookmark,
        onBookmarkClick = { onAction(CategoryAction.BookmarkClick) },
        onSearchClick = { onAction(CategoryAction.SearchClick) },
        onBackClick = { onAction(CategoryAction.BackClick) },
        appBarState = appBarState,
    )
}

@Composable
private fun CategoryAppBar(
    category: Category,
    showBookmark: Boolean,
    isBookmark: Boolean,
    onBookmarkClick: () -> Unit,
    onSearchClick: () -> Unit,
    onBackClick: () -> Unit,
    appBarState: AppBarState,
) {
    AppBar(
        navigationIcon = { BackButton(onClick = onBackClick) },
        title = {
            Text(
                text = category.name,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
            )
        },
        actions = {
            IconButton(
                onClick = onSearchClick,
                imageVector = FlowIcons.Search,
            )
            if (showBookmark) {
                BookmarkButton(
                    isBookmark = isBookmark,
                    onBookmarkClick = onBookmarkClick,
                )
            }
        },
        appBarState = appBarState,
    )
}

@Composable
private fun BookmarkButton(
    isBookmark: Boolean,
    onBookmarkClick: () -> Unit,
) {
    IconButton(
        onClick = onBookmarkClick,
        imageVector = if (isBookmark) {
            FlowIcons.BookmarkChecked
        } else {
            FlowIcons.BookmarkUnchecked
        },
        tint = if (isBookmark) {
            MaterialTheme.colorScheme.tertiary
        } else {
            Color.Unspecified
        }
    )
}
