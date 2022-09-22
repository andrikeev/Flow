package me.rutrackersearch.app.ui.forum.category

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import me.rutrackersearch.app.R
import me.rutrackersearch.app.ui.common.AppBar
import me.rutrackersearch.app.ui.common.BackButton
import me.rutrackersearch.app.ui.common.CategoryListItem
import me.rutrackersearch.app.ui.common.DynamicBox
import me.rutrackersearch.app.ui.common.FocusableLazyColumn
import me.rutrackersearch.app.ui.common.IconButton
import me.rutrackersearch.app.ui.common.LazyColumn
import me.rutrackersearch.app.ui.common.ScrollBackFloatingActionButton
import me.rutrackersearch.app.ui.common.TopicListItem
import me.rutrackersearch.app.ui.common.appendItems
import me.rutrackersearch.app.ui.common.dividedItems
import me.rutrackersearch.app.ui.common.emptyItem
import me.rutrackersearch.app.ui.common.errorItem
import me.rutrackersearch.app.ui.common.focusableItems
import me.rutrackersearch.app.ui.common.loadingItem
import me.rutrackersearch.app.ui.forum.category.CategoryAction.BackClick
import me.rutrackersearch.app.ui.forum.category.CategoryAction.BookmarkClick
import me.rutrackersearch.app.ui.forum.category.CategoryAction.CategoryClick
import me.rutrackersearch.app.ui.forum.category.CategoryAction.EndOfListReached
import me.rutrackersearch.app.ui.forum.category.CategoryAction.FavoriteClick
import me.rutrackersearch.app.ui.forum.category.CategoryAction.RetryClick
import me.rutrackersearch.app.ui.forum.category.CategoryAction.SearchClick
import me.rutrackersearch.app.ui.forum.category.CategoryAction.TopicClick
import me.rutrackersearch.app.ui.forum.category.CategoryAction.TorrentClick
import me.rutrackersearch.app.ui.paging.LoadState
import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.forum.CategoryModel
import me.rutrackersearch.models.search.Filter
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.TopicModel
import me.rutrackersearch.models.topic.Torrent
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
        viewModel = hiltViewModel(),
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
    DynamicBox(
        mobileContent = { MobileCategoryScreen(state, viewModel::perform) },
        tvContent = { TVCategoryScreen(state, viewModel::perform) },
    )
}

@Composable
private fun MobileCategoryScreen(
    state: CategoryState,
    onAction: (CategoryAction) -> Unit,
) {
    val category = state.categoryModelState.category
    val scrollState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                navigationIcon = { BackButton { onAction(BackClick) } },
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
                        onClick = { onAction(SearchClick) },
                        imageVector = Icons.Outlined.Search,
                    )
                    if (state.categoryModelState is CategoryModelState.Loaded) {
                        BookmarkButton(
                            isBookmark = state.categoryModelState.categoryModel.isBookmark,
                            onBookmarkClick = { onAction(BookmarkClick) },
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = { ScrollBackFloatingActionButton(scrollState) }
    ) { padding ->
        LazyColumn(
            state = scrollState,
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(top = 8.dp, bottom = 64.dp),
            onEndOfListReached = { onAction(EndOfListReached) },
        ) {
            when (state.content) {
                is CategoryContent.Initial -> when (val loadState = state.loadStates.refresh) {
                    is LoadState.Loading,
                    is LoadState.NotLoading -> loadingItem()
                    is LoadState.Error -> errorItem(
                        error = loadState.error,
                        onRetryClick = { onAction(RetryClick) },
                    )
                }

                is CategoryContent.Empty -> emptyItem(
                    titleRes = R.string.forum_empty_title,
                    subtitleRes = R.string.forum_empty_subtitle,
                    iconRes = R.drawable.ill_empty_search,
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
                                category = item.category,
                                onClick = { onAction(CategoryClick(item.category)) }
                            )

                            is TopicModel<out Topic> -> TopicListItem(
                                topicModel = item,
                                showCategory = false,
                                onClick = {
                                    val topic = item.topic
                                    onAction(
                                        if (topic is Torrent) {
                                            TorrentClick(topic)
                                        } else {
                                            TopicClick(topic)
                                        }
                                    )
                                },
                                onFavoriteClick = { onAction(FavoriteClick(item)) },
                            )

                            else -> Unit
                        }
                    }
                    appendItems(
                        state = state.loadStates.append,
                        onRetryClick = { onAction(RetryClick) },
                    )
                }
            }
        }
    }
}

@Composable
private fun TVCategoryScreen(
    state: CategoryState,
    onAction: (CategoryAction) -> Unit,
) {
    val category = state.categoryModelState.category
    val scrollState = rememberLazyListState()
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = category.name,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineMedium,
            )
            IconButton(
                onClick = { onAction(SearchClick) },
                imageVector = Icons.Outlined.Search,
            )
            if (state.categoryModelState is CategoryModelState.Loaded) {
                BookmarkButton(
                    isBookmark = state.categoryModelState.categoryModel.isBookmark,
                    onBookmarkClick = { onAction(BookmarkClick) },
                )
            }
        }
        FocusableLazyColumn(
            state = scrollState,
            contentPadding = PaddingValues(start = 32.dp, end = 32.dp, bottom = 32.dp),
            refocusFirst = false,
            onEndOfListReached = { onAction(EndOfListReached) },
        ) {
            when (state.content) {
                is CategoryContent.Initial -> when (val loadState = state.loadStates.refresh) {
                    is LoadState.Loading,
                    is LoadState.NotLoading -> loadingItem()
                    is LoadState.Error -> errorItem(
                        error = loadState.error,
                        onRetryClick = { onAction(RetryClick) },
                    )
                }

                is CategoryContent.Empty -> emptyItem(
                    titleRes = R.string.forum_empty_title,
                    subtitleRes = R.string.forum_empty_subtitle,
                    iconRes = R.drawable.ill_empty_search,
                )

                is CategoryContent.Content -> {
                    focusableItems(
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
                                category = item.category,
                                onClick = { onAction(CategoryClick(item.category)) }
                            )

                            is TopicModel<out Topic> -> TopicListItem(
                                topicModel = item,
                                showCategory = false,
                                onClick = {
                                    val topic = item.topic
                                    onAction(
                                        if (topic is Torrent) {
                                            TorrentClick(topic)
                                        } else {
                                            TopicClick(topic)
                                        }
                                    )
                                },
                            )

                            else -> Unit
                        }
                    }
                    appendItems(
                        state = state.loadStates.append,
                        onRetryClick = { onAction(RetryClick) },
                    )
                }
            }
        }
    }
}

@Composable
private fun BookmarkButton(
    isBookmark: Boolean,
    onBookmarkClick: () -> Unit,
) {
    IconButton(
        onClick = onBookmarkClick,
        imageVector = if (isBookmark) {
            Icons.Outlined.Bookmark
        } else {
            Icons.Outlined.BookmarkBorder
        },
        tint = if (isBookmark) {
            MaterialTheme.colorScheme.primary
        } else {
            Color.Unspecified
        }
    )
}
