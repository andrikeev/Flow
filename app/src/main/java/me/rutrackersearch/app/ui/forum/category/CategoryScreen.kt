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
import androidx.compose.runtime.collectAsState
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
import me.rutrackersearch.app.ui.common.PageResult
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
import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.forum.CategoryModel
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.TopicModel
import me.rutrackersearch.models.topic.Torrent

@Composable
fun CategoryScreen(
    onBackClick: () -> Unit,
    onCategoryClick: (Category) -> Unit,
    onTopicClick: (Topic) -> Unit,
    onTorrentClick: (Torrent) -> Unit,
    onSearchClick: (Category) -> Unit,
) {
    CategoryScreen(
        viewModel = hiltViewModel(),
        onBackClick = onBackClick,
        onCategoryClick = onCategoryClick,
        onTopicClick = onTopicClick,
        onTorrentClick = onTorrentClick,
        onSearchClick = onSearchClick,
    )
}

@Composable
private fun CategoryScreen(
    viewModel: CategoryViewModel,
    onBackClick: () -> Unit,
    onCategoryClick: (Category) -> Unit,
    onTopicClick: (Topic) -> Unit,
    onTorrentClick: (Torrent) -> Unit,
    onSearchClick: (Category) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val onAction: (CategoryAction) -> Unit = { action ->
        when (action) {
            is BackClick -> onBackClick()
            is SearchClick -> onSearchClick(action.category)
            is CategoryClick -> onCategoryClick(action.category)
            is TopicClick -> onTopicClick(action.topic)
            is TorrentClick -> onTorrentClick(action.torrent)
            else -> viewModel.perform(action)
        }
    }
    DynamicBox(
        mobileContent = { MobileCategoryScreen(state, onAction) },
        tvContent = { TVCategoryScreen(state, onAction) },
    )
}

@Composable
private fun MobileCategoryScreen(
    state: CategoryState,
    onAction: (CategoryAction) -> Unit,
) {
    val (category, isBookmark) = state.category
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
                    ActionButtons(
                        isBookmark = isBookmark,
                        onSearchClick = { onAction(SearchClick(category)) },
                        onBookmarkClick = { onAction(BookmarkClick(state.category)) },
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(vertical = 8.dp),
            onEndOfListReached = { onAction(EndOfListReached) },
        ) {
            when (state.content) {
                is PageResult.Loading -> loadingItem()
                is PageResult.Error -> errorItem(
                    error = state.content.error,
                    onRetryClick = { onAction(RetryClick) },
                )
                is PageResult.Empty -> emptyItem(
                    titleRes = R.string.forum_empty_title,
                    subtitleRes = R.string.forum_empty_subtitle,
                    iconRes = R.drawable.ill_empty_search,
                )
                is PageResult.Content -> {
                    dividedItems(
                        items = state.content.content.run { categories + topics },
                        key = { item ->
                            when (item) {
                                is CategoryModel -> item.data.id
                                is TopicModel<out Topic> -> item.data.id
                                else -> Unit
                            }
                        },
                        contentType = { item ->
                            when (item) {
                                is CategoryModel -> item.data::class
                                is TopicModel<out Topic> -> item.data::class
                                else -> Unit
                            }
                        },
                    ) { item ->
                        when (item) {
                            is CategoryModel -> CategoryListItem(
                                category = item.data,
                                onClick = { onAction(CategoryClick(item.data)) }
                            )
                            is TopicModel<out Topic> -> TopicListItem(
                                topicModel = item,
                                showCategory = false,
                                highlightNew = true,
                                onClick = {
                                    val topic = item.data
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
                        state = state.content.append,
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
    val (category, isBookmark) = state.category
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
            ActionButtons(
                isBookmark = isBookmark,
                onSearchClick = { onAction(SearchClick(category)) },
            ) { onAction(BookmarkClick(state.category)) }
        }
        FocusableLazyColumn(
            state = scrollState,
            contentPadding = PaddingValues(start = 32.dp, end = 32.dp, bottom = 32.dp),
            refocusFirst = false,
            onEndOfListReached = { onAction(EndOfListReached) },
        ) {
            when (state.content) {
                is PageResult.Loading -> loadingItem()
                is PageResult.Error -> errorItem(
                    error = state.content.error,
                    onRetryClick = { onAction(RetryClick) },
                )
                is PageResult.Empty -> emptyItem(
                    titleRes = R.string.forum_empty_title,
                    subtitleRes = R.string.forum_empty_subtitle,
                    iconRes = R.drawable.ill_empty_search,
                )
                is PageResult.Content -> {
                    focusableItems(
                        items = state.content.content.run { categories + topics },
                        key = { item ->
                            when (item) {
                                is CategoryModel -> item.data.id
                                is TopicModel<out Topic> -> item.data.id
                                else -> Unit
                            }
                        },
                        contentType = { item ->
                            when (item) {
                                is CategoryModel -> item.data::class
                                is TopicModel<out Topic> -> item.data::class
                                else -> Unit
                            }
                        },
                    ) { item ->
                        when (item) {
                            is CategoryModel -> CategoryListItem(
                                category = item.data,
                                onClick = { onAction(CategoryClick(item.data)) }
                            )
                            is TopicModel<out Topic> -> TopicListItem(
                                topicModel = item,
                                showCategory = false,
                                highlightNew = true,
                                onClick = {
                                    val topic = item.data
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
                        state = state.content.append,
                        onRetryClick = { onAction(RetryClick) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButtons(
    isBookmark: Boolean,
    onSearchClick: () -> Unit,
    onBookmarkClick: () -> Unit,
) {
    IconButton(
        onClick = onSearchClick,
        imageVector = Icons.Outlined.Search,
    )
    IconButton(
        onClick = onBookmarkClick,
        imageVector = if (isBookmark) {
            Icons.Outlined.Bookmark
        } else {
            Icons.Outlined.BookmarkBorder
        },
        tint = if (isBookmark) {
            MaterialTheme.colorScheme.secondary
        } else {
            Color.Unspecified
        }
    )
}
