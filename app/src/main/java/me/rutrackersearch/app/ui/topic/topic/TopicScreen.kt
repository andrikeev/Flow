package me.rutrackersearch.app.ui.topic.topic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FirstPage
import androidx.compose.material.icons.outlined.LastPage
import androidx.compose.material.icons.outlined.Pin
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import me.rutrackersearch.app.R
import me.rutrackersearch.app.ui.common.BackButton
import me.rutrackersearch.app.ui.common.DynamicBox
import me.rutrackersearch.app.ui.common.ExpandableAppBar
import me.rutrackersearch.app.ui.common.FavoriteButton
import me.rutrackersearch.app.ui.common.IconButton
import me.rutrackersearch.app.ui.common.LazyColumn
import me.rutrackersearch.app.ui.common.LocalSnackbarHostState
import me.rutrackersearch.app.ui.common.PageResult
import me.rutrackersearch.app.ui.common.Post
import me.rutrackersearch.app.ui.common.TVAppBar
import me.rutrackersearch.app.ui.common.TextButton
import me.rutrackersearch.app.ui.common.appendItems
import me.rutrackersearch.app.ui.common.dividedItems
import me.rutrackersearch.app.ui.common.emptyItem
import me.rutrackersearch.app.ui.common.errorItem
import me.rutrackersearch.app.ui.common.loadingItem
import me.rutrackersearch.app.ui.common.rememberTabAppBarScrollBehavior
import me.rutrackersearch.app.ui.topic.topic.TopicAction.AddComment
import me.rutrackersearch.app.ui.topic.topic.TopicAction.BackClick
import me.rutrackersearch.app.ui.topic.topic.TopicAction.EndOfListReached
import me.rutrackersearch.app.ui.topic.topic.TopicAction.FavoriteClick
import me.rutrackersearch.app.ui.topic.topic.TopicAction.FirstVisibleItemIndexChanged
import me.rutrackersearch.app.ui.topic.topic.TopicAction.LoginClick
import me.rutrackersearch.app.ui.topic.topic.TopicAction.RetryClick
import me.rutrackersearch.models.topic.Post

@Composable
fun TopicScreen(
    back: () -> Unit,
    openLogin: () -> Unit,
) {
    TopicScreen(
        viewModel = hiltViewModel(),
        onBackClick = back,
        onLoginClick = openLogin,
    )
}

@Composable
private fun TopicScreen(
    viewModel: TopicViewModel,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val snackbarState = remember { SnackbarHostState() }
    val onAction: (TopicAction) -> Unit = { action ->
        when (action) {
            is BackClick -> onBackClick()
            is LoginClick -> onLoginClick()
            else -> viewModel.perform(action)
        }
    }
    CompositionLocalProvider(LocalSnackbarHostState provides snackbarState) {
        DynamicBox(
            mobileContent = { MobileTopicScreen(state, onAction) },
            tvContent = { TVTopicScreen(state, onAction) },
        )
    }
}

@Composable
private fun MobileTopicScreen(
    state: TopicState,
    onAction: (TopicAction) -> Unit,
) {
    val (topic, _, isFavorite) = state.topic
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            var isExpanded by remember { mutableStateOf(false) }
            ExpandableAppBar(
                navigationIcon = { BackButton { onAction(BackClick) } },
                title = {
                    Text(
                        text = topic.title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleSmall,
                    )
                },
                actions = {
                    FavoriteButton(
                        isFavorite = isFavorite,
                        onClick = { onAction(FavoriteClick(state.topic)) },
                    )
                    AnimatedVisibility(visible = state.pages > 1) {
                        val rotation by animateFloatAsState(
                            targetValue = if (isExpanded) 180f else 0f
                        )
                        IconButton(
                            modifier = Modifier.rotate(rotation),
                            onClick = { isExpanded = !isExpanded },
                            imageVector = if (isExpanded) {
                                Icons.Outlined.ExpandMore
                            } else {
                                Icons.Outlined.Pin
                            },
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                isExpanded = isExpanded,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val isEnabled = state.content !is PageResult.Loading
                    fun goToPage(page: Int) = onAction(TopicAction.GoToPage(page))
                    IconButton(
                        onClick = { goToPage(1) },
                        enabled = isEnabled && state.page != 1,
                        imageVector = Icons.Outlined.FirstPage,
                    )
                    IconButton(
                        onClick = { goToPage(state.page - 1) },
                        enabled = isEnabled && state.page != 1,
                        imageVector = Icons.Outlined.ChevronLeft,
                    )
                    Box(
                        modifier = Modifier.width(48.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Crossfade(targetState = isEnabled) { showPage ->
                            if (showPage) {
                                Text(
                                    text = "${state.page}/${state.pages}",
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            } else {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 1.dp,
                                )
                            }
                        }
                    }
                    IconButton(
                        onClick = { goToPage(state.page + 1) },
                        enabled = isEnabled && state.page != state.pages,
                        imageVector = Icons.Outlined.ChevronRight,
                    )
                    IconButton(
                        onClick = { goToPage(state.pages) },
                        enabled = isEnabled && state.page != state.pages,
                        imageVector = Icons.Outlined.LastPage,
                    )
                }
            }
        },
        floatingActionButton = {
            var showAddCommentDialog by remember { mutableStateOf(false) }
            FloatingActionButton(
                onClick = { showAddCommentDialog = true },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.secondary,
            ) {
                Icon(imageVector = Icons.Outlined.Edit, contentDescription = null)
            }
            if (showAddCommentDialog) {
                var textValue by remember { mutableStateOf("") }
                Dialog(onDismissRequest = { showAddCommentDialog = false }) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surface,
                    ) {
                        Column {
                            val focusRequester = remember { FocusRequester() }
                            OutlinedTextField(
                                modifier = Modifier
                                    .padding(
                                        start = 24.dp,
                                        top = 16.dp,
                                        end = 24.dp,
                                    )
                                    .focusRequester(focusRequester)
                                    .defaultMinSize(minHeight = 96.dp),
                                value = textValue,
                                onValueChange = { textValue = it },
                                label = { Text("Новый комментарий") },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    autoCorrect = true,
                                    imeAction = ImeAction.Done,
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        showAddCommentDialog = false
                                        onAction(AddComment(textValue))
                                    }
                                ),
                                maxLines = 3,
                            )
                            LaunchedEffect(Unit) {
                                coroutineContext.job.invokeOnCompletion { error ->
                                    if (error == null) {
                                        focusRequester.requestFocus()
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                TextButton(
                                    text = stringResource(R.string.action_cancel),
                                    onClick = { showAddCommentDialog = false },
                                )
                                TextButton(
                                    text = stringResource(R.string.action_send),
                                    onClick = {
                                        showAddCommentDialog = false
                                        onAction(AddComment(textValue))
                                    },
                                )
                            }
                        }
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(LocalSnackbarHostState.current) },
    ) { padding ->
        val scrollState = rememberLazyListState()
        val firstVisibleItemIndex by remember {
            derivedStateOf {
                val layoutInfo = scrollState.layoutInfo
                val visibleItemsInfo = layoutInfo.visibleItemsInfo
                visibleItemsInfo.firstOrNull()?.index ?: 0
            }
        }
        LaunchedEffect(firstVisibleItemIndex) {
            onAction(FirstVisibleItemIndexChanged(firstVisibleItemIndex))
        }
        LazyColumn(
            modifier = Modifier.padding(padding),
            state = scrollState,
            contentPadding = PaddingValues(top = 8.dp, bottom = 56.dp),
            onEndOfListReached = { onAction(EndOfListReached) },
        ) {
            when (state.content) {
                is PageResult.Loading -> loadingItem()
                is PageResult.Error -> errorItem(
                    error = state.content.error,
                    onRetryClick = { onAction(RetryClick) },
                )
                is PageResult.Empty -> emptyItem(
                    titleRes = R.string.topic_empty_title,
                    subtitleRes = R.string.topic_empty_subtitle,
                    iconRes = R.drawable.ill_empty_search,
                )
                is PageResult.Content -> {
                    dividedItems(
                        items = state.content.content,
                        key = Post::id,
                        contentType = { it::class },
                        itemContent = { post -> PostItem(post = post) },
                    )
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
private fun TVTopicScreen(
    state: TopicState,
    onAction: (TopicAction) -> Unit,
) {
    val (topic, _, isFavorite) = state.topic
    val scrollBehavior = rememberTabAppBarScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TVAppBar(
                scrollBehavior = scrollBehavior,
                title = topic.title,
                action = {
                    FavoriteButton(
                        isFavorite = isFavorite,
                        onClick = { onAction(FavoriteClick(state.topic)) },
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(LocalSnackbarHostState.current) },
    ) { padding ->
        Row(
            modifier = Modifier.padding(
                start = 32.dp,
                top = padding.calculateTopPadding(),
                end = 32.dp,
                bottom = padding.calculateBottomPadding(),
            ),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            val scrollState = rememberLazyListState()
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                state = scrollState,
                contentPadding = PaddingValues(vertical = 16.dp),
                onEndOfListReached = { onAction(EndOfListReached) },
            ) {
                when (state.content) {
                    is PageResult.Loading -> loadingItem()
                    is PageResult.Error -> errorItem(
                        error = state.content.error,
                        onRetryClick = { onAction(RetryClick) },
                    )
                    is PageResult.Empty -> emptyItem(
                        titleRes = R.string.topic_empty_title,
                        subtitleRes = R.string.topic_empty_subtitle,
                        iconRes = R.drawable.ill_empty_search,
                    )
                    is PageResult.Content -> {
                        dividedItems(
                            items = state.content.content,
                            key = Post::id,
                            contentType = { it::class },
                            itemContent = { post -> PostItem(post = post) },
                        )
                        appendItems(
                            state = state.content.append,
                            onRetryClick = { onAction(RetryClick) },
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = state.content is PageResult.Content,
                enter = fadeIn() + expandHorizontally(expandFrom = Alignment.End),
                exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.End),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(vertical = 32.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    val coroutineScope = rememberCoroutineScope()
                    IconButton(
                        onClick = { coroutineScope.launch { scrollState.scrollBy(-100f) } },
                        imageVector = Icons.Default.KeyboardArrowUp,
                    )
                    IconButton(
                        onClick = { coroutineScope.launch { scrollState.scrollBy(100f) } },
                        imageVector = Icons.Default.KeyboardArrowDown,
                    )
                }
            }
        }
    }
}

@Composable
private fun PostItem(
    modifier: Modifier = Modifier,
    post: Post,
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        AsyncImage(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            model = post.author.avatarUrl,
            placeholder = painterResource(R.drawable.ill_avatar_placeholder),
            error = painterResource(R.drawable.ill_avatar_placeholder),
            contentDescription = null,
        )
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = post.author.name,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                    ),
                )
                Text(
                    text = post.date,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.outline,
                    ),
                )
            }
            Post(
                modifier = Modifier.padding(top = 4.dp),
                content = post.content,
            )
        }
    }
}
